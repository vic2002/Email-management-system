/* When the page is loaded, make a request to back end about message content. */
window.onload = () => {
    if (document.getElementById("association") != null) {
        document.getElementById("association").innerText = getCookie("organizationName");
    }
    const urlParams = new URLSearchParams(window.location.search); //URI of the page
    const id = urlParams.get("id");
    const urlString = window.location.href;
    const index = urlString.substring("http://kick-in-3.paas.hosted-by-previder.com/".length).indexOf("/") + "http://kick-in-3.paas.hosted-by-previder.com/".length;
    const xhr = new XMLHttpRequest(), url = urlString.substring(0, index).concat("/rest/message/".concat(id)), method = "GET";

    xhr.onreadystatechange = function() {
        if (this.readyState === XMLHttpRequest.DONE && this.status === 200) {
            let message = JSON.parse(xhr.responseText);
            displayData(message);
        }
    };
    const token = getCookie("sessionToken")
    xhr.open(method, url.concat(`?token=${token}`), true);
    xhr.send();
    getReadOrgs();
};

/* Displays a message on the page. Takes a message in JSON format. */
const displayData = (message) => {
    const subjectDiv = document.getElementById("subject");
    subjectDiv.insertAdjacentHTML("beforeend", `<h1>${message.subject}</h1>`);
    const senderDiv = document.getElementById("sender");
    senderDiv.innerText = `${message.sender} (${message.senderEmail})`;
    const options = {
        day: "numeric",
        month: "long",
        year: "numeric",
    };
    const sendAtDiv = document.getElementById("sendAt");
    sendAtDiv.innerText = `${new Date(message.sendAt).toLocaleString("en-GB", options)}`;
    const messageDiv = document.getElementById("message");
    messageDiv.insertAdjacentHTML("beforeend", message.content);
    const attachemntsDiv = document.getElementById("attachments");
    let tmp = "";
    message.attachments.attachments.forEach(attachment => {
        let path = "";
        if (isImageExtension(attachment.name)) {
            path = "../files/".concat(attachment.name);
        } else {
            path = "../images/previews/".concat(attachment.name.split(".")[0].concat(".jpg"));
        }

        tmp = tmp.concat(`<div class="attachment-container"><a href=${"../files/".concat(attachment.name)} class="attachment dissapearing-background">
                    <img src="${path}" style="max-width: 100%; max-height: 100%; object-fit: contain;">
                    <div class="attachment-info top-left">${attachment.name}</div>
                </a></div>`);
    });
    attachemntsDiv.insertAdjacentHTML('afterbegin', tmp);
}

/* An expression which checks whether a file has image attachments. */
const isImageExtension = (fileName) => {
    return fileName.includes(".jpg") || fileName.includes(".jpeg") || fileName.includes(".png");
}

//TODO
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

/* Displays which organizations have read an email. */
const getReadOrgs = () => {
    const urlParams = new URLSearchParams(window.location.search); //URI of the page
    const id = urlParams.get("id");
    const urlString = window.location.href;
    const index = urlString.substring("http://kick-in-3.paas.hosted-by-previder.com/".length).indexOf("/") + "http://kick-in-3.paas.hosted-by-previder.com/".length;
    const xhr = new XMLHttpRequest();
    const url = urlString.substring(0, index).concat("/rest/read/".concat(id));
    const method = "GET";

    xhr.onreadystatechange = function() {
        if (this.readyState === XMLHttpRequest.DONE && this.status === 200) {
            let orgs = JSON.parse(xhr.responseText);
            orgs = orgs.orgs;
            if (orgs.length != 0) {
                let message = '<p><b><span style="color:green"> Read by: </span></b>';
                orgs.forEach((org) => {
                    message += `<b>${org.name}</b> (${org.email}),`;
                })
                message = message.substr(0, message.length - 1).concat('.');
                message += `</p>`;
                document.getElementById("attachments").innerHTML += message;
            }
        }
    };
    const token = getCookie("sessionToken");
    xhr.open(method, url.concat(`?token=${token}`), true);
    xhr.send();
}