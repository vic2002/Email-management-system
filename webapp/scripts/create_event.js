/* To be called when a button is pressed. */
const create = () => {
    const current_url = window.location.href;
    const index = current_url.substring("http://localhost:8080/".length).indexOf("/") + "http://localhost:8080/".length;
    const url = current_url.substring(0, index).concat("/rest/event/create");

    const data = getFormData();
    sendRequest(data, url);
}

/* Sends a request to the server. Data is assumed to be already formed at this point. */
const sendRequest = (data, url) => {
    const xmlHttp = new XMLHttpRequest();
    xmlHttp.onreadystatechange = function () {
        if (this.readyState === XMLHttpRequest.DONE) {
            handleStatus(this.status);
        }
    }
    const token = getCookie("sessionToken");
    xmlHttp.open("POST", url.concat(`?token=${token}`), true);
    xmlHttp.setRequestHeader("Content-Type", "application/json");
    xmlHttp.send(JSON.stringify(data));
}

const handleStatus = (status) => {
    if (status === 200) { // OK
        window.location.href = "../html/admin_page.html";
    }
}

/* Makes a JSON object from user input in HTML form. */
const getFormData = () => {
    const name = DOMPurify.sanitize(document.getElementById("name").value);
    const startingOn = DOMPurify.sanitize(document.getElementById("starting").value);
    const endingOn = DOMPurify.sanitize(document.getElementById("ending").value);

    const json = {
        "name": name,
        "startingOn": startingOn,
        "endingOn": endingOn,
    };

    return json;
}