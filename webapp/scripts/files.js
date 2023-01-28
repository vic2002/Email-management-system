var myState = {
    pdf: null,
    currentPage: 1,
    zoom: -5,
};

window.onload = () => {

    if (document.getElementById("association") != null) {
        document.getElementById("association").innerText = getCookie("organizationName");
    }
    sendSearchRequest();
}

const getFormData = () => {
    const searchFilter = document.getElementById("search").value;
    const afterFilter = document.getElementById("after").value;
    const beforeFilter = document.getElementById("before").value;
    const fromFilter = document.getElementById("sortoption").value;
    const isDutch = document.getElementById("dutch").checked;

    const jsonBody = {
        "keyword": searchFilter,
        "fromDate": afterFilter,
        "toDate": beforeFilter,
        "sort": fromFilter,
        "isDutch": isDutch,
    };

    return jsonBody;
}

const sendSearchRequest = () => {
    const urlString = window.location.href;
    const index = urlString.substring("http://localhost:8080/".length).indexOf("/") + "http://localhost:8080/".length;
    const xhr = new XMLHttpRequest(), url = urlString.substring(0, index).concat("/rest/files/filter");
    
    const method = "POST";

    const jsonBody = getFormData();

    xhr.onreadystatechange = function () {
        if (this.readyState === XMLHttpRequest.DONE) {
            if (this.status === 200) {
                let jsonResponse = JSON.parse(xhr.responseText);
                displayData(jsonResponse);
            } else if (this.status === 401) { // Unauthorized
                alert("401");
            }
        }
    };

    const token = getCookie("sessionToken");
    xhr.open(method, url.concat(`?token=${token}`), true);
    xhr.setRequestHeader("Content-Type", "application/json");
    xhr.send(JSON.stringify(jsonBody));
}

const clearDiv = (div) => {
    for (let i = div.childNodes.length - 1; i >= 0; i--) {
        div.removeChild(div.childNodes[i]);
    }
}

const isImageExtension = (fileName) => {
    return fileName.includes(".jpg") || fileName.includes(".png");
}

const displayData = (jsonResponse) => {
    const attachmentsDiv = document.getElementById("files");
    clearDiv(attachmentsDiv);

    const options = {
        day: "numeric",
        month: "long",
        year: "numeric"
    }

    let tmp = "";
    jsonResponse.attachments.forEach(a => {
        let path = "";
        if (isImageExtension(a.name)) {
            path = "../files/".concat(a.name);
        } else {
            path = "../images/previews/".concat(a.name.split(".")[0].concat(".jpg"));
        }

        tmp = tmp.concat(`<div class="file-container"><a href=${"../files/".concat(a.name)} class="file dissapearing-background">
                    <img src="${path}" style="max-width: 100%; max-height: 100%; object-fit: contain;">
                    <div class="file-info top-left">${a.name}</div>
                    <div class="file-info bottom-right">${new Date(a.sendAt).toLocaleString("en-GB", options)}</div>
                </a></div>`);
    });
    attachmentsDiv.insertAdjacentHTML("afterbegin", tmp);
    if (jsonResponse.numberOfAttachments === 0) {
        attachmentsDiv.insertAdjacentHTML("afterbegin", `<div style="text-align center; color: grey;">No results</div>`);
    }
}

const drawPdf = (path, id) => {
    pdfjsLib.getDocument(path).then((pdf) => {
        myState.pdf = pdf;
        myState.pdf.getPage(myState.currentPage).then((page) => {
            var canvas = document.getElementById(`pdf_${id}`);
            var ctx = canvas.getContext('2d');

            var scale = canvas.height / page.getViewport(1).height;
            var viewport = page.getViewport(scale);
            ctx.translate((canvas.width - viewport.width) / 2, 0);

            page.render({
                canvasContext: ctx,
                viewport: viewport
            });
        });
    });
}