let attachmentsCount = 0;
let receiversCount = 1;
let receivers = '';
let emailId = -1;
let confirmationsReceived = 0;

window.onload = () => {
    getReceivers();
    getEvents();
}

/* To be called when the button "Add attachment" is pressed. */
const addAttachment = () => {
    attachmentsCount++;
    const attachmentButton = document.getElementById("add-attachment-button");
    //const attachmentField = `<div style="width: 100%;"><input class="att" id="attachment${attachmentsCount}" type="file" name="attachment${attachmentsCount}"></div>`;
    const attachmentField = `<div style="width: 100%;"><input class="att" id="attachment${attachmentsCount}" type="text" name="attachment${attachmentsCount}"></div>`;
    attachmentButton.insertAdjacentHTML('beforebegin', attachmentField);
}

/* To be called when the button "Add receiver" is pressed. */
const addReceiver = () => {
    receiversCount++;
    const receiverButton = document.getElementById("add-receiver-button");
    const receiverField = `<div style="width: 100%;"><select class="select" name="receiver${receiversCount}" id="receiver${receiversCount}">${receivers}</select></div>`;
    receiverButton.insertAdjacentHTML('beforebegin', receiverField);
}

/* When a page is loaded, it automatically fills in some data about receivers by getting 
possible receivers from the database. */
const getReceivers = () => {
    const current_url = window.location.href;
    const index = current_url.substring("http://localhost:8080/".length).indexOf("/") + "http://localhost:8080/".length;
    const url = current_url.substring(0, index).concat("/rest/organizations/all");

    const xmlHttp = new XMLHttpRequest();
    xmlHttp.onreadystatechange = function () {
        if (this.readyState === XMLHttpRequest.DONE) {
            if (this.status === 200) { // OK
                const json = JSON.parse(this.responseText);
                json.organizations.forEach(organization => {
                    receivers = receivers.concat(`<option value="${organization.id}">${organization.name}</option>`);
                });
                document.getElementById("receiver1").insertAdjacentHTML('beforeend', receivers);
            } else if (this.status === 401) { // Unauthorized
                alert("You don't have permission to do that!");
                window.location.href = "../html/home.html";
            }
        }
    }
    const token = getCookie("sessionToken");
    xmlHttp.open("GET", url.concat(`?token=${token}`), true);
    xmlHttp.send();
}

const getEvents = () => {
    const current_url = window.location.href;
    const index = current_url.substring("http://localhost:8080/".length).indexOf("/") + "http://localhost:8080/".length;
    const url = current_url.substring(0, index).concat("/rest/events/all");

    const xmlHttp = new XMLHttpRequest();
    let events = "";
    xmlHttp.onreadystatechange = function () {
        if (this.readyState === XMLHttpRequest.DONE) {
            if (this.status === 200) { // OK
                const json = JSON.parse(this.responseText);
                json.events.forEach(e => {
                    events = events.concat(`<option value="${e.id}">${e.name}</option>`);
                });
                document.getElementById("event").insertAdjacentHTML('beforeend', events);
            } else if (this.status === 401) { // Unauthorized
                alert("You don't have permission to do that!");
                window.location.href = "../html/home.html";
            }
        }
    }
    const token = getCookie("sessionToken");
    xmlHttp.open("GET", url.concat(`?token=${token}`), true);
    xmlHttp.send();
}

/* To be called when a button is pressed. */
const create = () => {
    const data = getFormData();
    sendRequest(data);
}

/* Sends a request to the server. Data is assumed to be already formed at this point. */
const sendRequest = (data) => {
    const current_url = window.location.href;
    const index = current_url.substring("http://localhost:8080/".length).indexOf("/") + "http://localhost:8080/".length;
    const url = current_url.substring(0, index).concat("/rest/message/create");

    const xmlHttp = new XMLHttpRequest();
    xmlHttp.onreadystatechange = function () {
        if (this.readyState === XMLHttpRequest.DONE) {
            if (this.status === 200) { // OK
                emailId = xmlHttp.response;
                handleAtt();
                //handleIsFor();
            } else if (this.status === 401) { // UNAUTHORIZED

            }
        }
    }
    const token = getCookie("sessionToken");
    xmlHttp.open("POST", url.concat(`?token=${token}`), true);
    xmlHttp.setRequestHeader("Content-Type", "application/json");
    xmlHttp.send(JSON.stringify(data));
}

const handleAtt = () => {
    console.log("wa");
    const current_url = window.location.href;
    const index = current_url.substring("http://localhost:8080/".length).indexOf("/") + "http://localhost:8080/".length;
    const url = current_url.substring(0, index).concat("/rest/file/create");
    const token = getCookie("sessionToken");
    const json = {
        "attachmentNames": [],
        "emailId": emailId,
    };

    for (let i = 1; i <= attachmentsCount; i++) {
        const attachment = DOMPurify.sanitize(document.getElementById(`attachment${i}`).value);
        json["attachmentNames"].push(attachment);
    }

    const xmlHttp = new XMLHttpRequest();
    xmlHttp.onreadystatechange = function () {
        if (this.readyState === XMLHttpRequest.DONE) {
            console.log("Handle attachments ", this.status);
            if (this.status === 200) { // OK
                handleIsFor();
                //window.location.href = "../html/admin_page.html";
            } 
        }
    }
    xmlHttp.open("POST", url.concat(`?token=${token}`), true);
    xmlHttp.setRequestHeader("Content-Type", "application/json");
    xmlHttp.send(JSON.stringify(json));
}

const handleAttachments = () => {
    const current_url = window.location.href;
    const index = current_url.substring("http://localhost:8080/".length).indexOf("/") + "http://localhost:8080/".length;
    const url = current_url.substring(0, index).concat("/rest/file/create");
    const token = getCookie("sessionToken");

    for (let i = 1; i <= attachmentsCount; i++) {
        const formData = new FormData();
        const attachment = DOMPurify.sanitize(document.getElementById(`attachment${i}`).files[0]);
        formData.append(`file`, attachment);

        const xmlHttp = new XMLHttpRequest();
        xmlHttp.onreadystatechange = function () {
            if (this.readyState === XMLHttpRequest.DONE) {
                if (this.status === 200 && confirmationsReceived === attachmentsCount) { // OK
                    confirmationsReceived = 0;
                    handleIsFor();
                } else if (this.status === 200) {
                    confirmationsReceived++;
                }
            }
        }
        xmlHttp.open("POST", url.concat(`?token=${token}&emailId=${emailId}`), true);
        xmlHttp.send(formData);
    }
}

const handleIsFor = () => {
    const current_url = window.location.href;
    const index = current_url.substring("http://localhost:8080/".length).indexOf("/") + "http://localhost:8080/".length;
    const url = current_url.substring(0, index).concat("/rest/isfor/create");
    const token = getCookie("sessionToken");
    const json = {
        "organizationIds": [],
        "emailId": emailId,
    };

    for (let i = 1; i <= receiversCount; i++) {
        const receiver = DOMPurify.sanitize(document.getElementById(`receiver${i}`).value);
        json["organizationIds"].push(receiver);
    }

    const xmlHttp = new XMLHttpRequest();
    xmlHttp.onreadystatechange = function () {
        if (this.readyState === XMLHttpRequest.DONE) {
            console.log("Handle isFor ", this.status);
            if (this.status === 200) { // OK
                window.location.href = "../html/admin_page.html";
            }
        }
    }
    xmlHttp.open("POST", url.concat(`?token=${token}`), true);
    xmlHttp.setRequestHeader("Content-Type", "application/json");
    xmlHttp.send(JSON.stringify(json));
}

/* Makes a JSON object from user input in HTML form. */
const getFormData = () => {
    const subject = DOMPurify.sanitize(document.getElementById("subject").value);
    const content = DOMPurify.sanitize(document.getElementById("content").value);
    const eventId = DOMPurify.sanitize(document.getElementById("event").value);
    const json = {
        "subject": subject,
        "content": content,
        "eventId": eventId,
    };
    return json;
}
