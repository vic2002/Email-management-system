window.onload = () => {
    if (document.getElementById("association") != null) {
        document.getElementById("association").innerText = getCookie("organizationName");
    }
    sendSearchRequest();
};

const clearDiv = (div) => {
    for (let i = div.childNodes.length - 1; i >= 0; i--) {
        div.removeChild(div.childNodes[i]);
    }
}

const getFormData = () => {
    const searchFilter = document.getElementById("search").value;
    const afterFilter = document.getElementById("after").value;
    const beforeFilter = document.getElementById("before").value;
    const fromFilter = document.getElementById("sortoption").value;
    const isDutch = document.getElementById("dutch").checked;
    const readFilter = document.getElementById("statusoption").value;

    const jsonBody = {
        "keyword": searchFilter,
        "fromDate": afterFilter,
        "toDate": beforeFilter,
        "sort": fromFilter,
        "isDutch": isDutch,
        "status": readFilter,
    };

    return jsonBody;
}

const sendSearchRequest = () => {
    const urlParams = new URLSearchParams(window.location.search);
    const resultsAmount = urlParams.get("show") ? urlParams.get("show") : 5;
    const page = urlParams.get("page") ? urlParams.get("page") : 1;
    createShowOptions(resultsAmount);
    const urlString = window.location.href;
    const index = urlString.substring("http://kick-in-3.paas.hosted-by-previder.com/".length).indexOf("/") + "http://kick-in-3.paas.hosted-by-previder.com/".length;
    const xhr = new XMLHttpRequest(), url = urlString.substring(0, index).concat("/rest/messages");
    const method = "POST";

    const jsonBody = getFormData();

    xhr.onreadystatechange = function () {
        if (this.readyState === XMLHttpRequest.DONE && this.status === 200) {
            let jsonResponse = JSON.parse(xhr.response);
            displayData(jsonResponse, resultsAmount, page);
        } else if (this.readyState = XMLHttpRequest.DONE && this.status === 401) { // Unauthorized
            const messagesDiv = document.getElementById("messages");
            const text = `<div style="color: red; text-align: center;">Not logged in or you don't have access to this page.</div>`
            messagesDiv.insertAdjacentHTML('beforeend', text);
        }
    };
    const token = getCookie("sessionToken");
    xhr.open(method, url.concat(`?token=${token}`), true);
    xhr.setRequestHeader("Content-Type", "application/json");
    xhr.send(JSON.stringify(jsonBody));
}

const displayData = (jsonResponse, resultsAmount, page) => {
    const messagesDiv = document.getElementById("messages");
    const paginationDiv = document.getElementById("pagination");

    clearDiv(messagesDiv);
    clearDiv(paginationDiv);
    
    let tmp = "";
    const options = {
        day: "numeric",
        month: "long",
        year: "numeric"
    }

    jsonResponse.messages.slice(resultsAmount * (page - 1)).slice(0, resultsAmount).forEach(message => {
        tmp = tmp.concat(`<a href="message.html?id=${message.id}"><div class="message">
                        <div><b>From: ${message.sender} (${message.senderEmail})</b></div>
                        <div><b>Subject: ${message.subject}</b></div>
                        <div>Sent at: ${new Date(message.sendAt).toLocaleString("en-GB", options)}</div>
                        </div></a>`);
    });
    loadPagination(page, Math.ceil(jsonResponse.messages.length / resultsAmount));
    messagesDiv.insertAdjacentHTML('beforeend', tmp);
}

const loadPagination = (page, pagesCount) => {
    const div = document.getElementById("pagination");
    let tmp = "";
    for (let i = 1; i <= pagesCount; i++) {
        if (i == page) {
            tmp = tmp.concat(`<a class='pointer' href="#">${i}</a>`);
        } else {
            tmp = tmp.concat(`<a class='pointer' onclick='setPage(${i})'><span>${i}</span></a>`);
        }
    }
    div.insertAdjacentHTML('beforeend', tmp);
}

const setPage = (page) => {
    const urlParams = new URLSearchParams(window.location.search);
    urlParams.set("page", page);
    window.location.href = window.location.origin.concat(window.location.pathname.concat("?".concat(urlParams.toString())));
}

const createShowOptions = (i) => {
    const div = document.getElementById("display-amount");
    clearDiv(div);
    const result = `Show: ${i == 5 ? "<span class='pointer'>5</span>" : "<a class='pointer' onclick='setShow(5)'>5</a>"}
                | ${i == 10 ? "<span class='pointer'>10</span>" : "<a class='pointer' onclick='setShow(10)'>10</a>"}
                | ${i == 15 ? "<span class='pointer'>15</span>" : "<a class='pointer' onclick='setShow(15)'>15</a>"}`;
    div.insertAdjacentHTML('beforeend', result);
}

const setShow = (i) => {
    const urlParams = new URLSearchParams(window.location.search);
    urlParams.set("show", i);
    window.location.href = window.location.origin.concat(window.location.pathname.concat("?".concat(urlParams.toString())));
}