window.onload = () => {
    const urlParams = new URLSearchParams(window.location.search);
    const eventId = urlParams.get("id");
    loadData(eventId);
}

/* Automatically fills current data about the event. */
const loadData = (eventId) => {
    const current_url = window.location.href;
    const index = current_url.substring("http://localhost:8080/".length).indexOf("/") + "http://localhost:8080/".length;
    const url = current_url.substring(0, index).concat(`/rest/event/${eventId}`);

    const xmlHttp = new XMLHttpRequest();
    xmlHttp.onreadystatechange = function () {
        if (this.readyState === XMLHttpRequest.DONE) {
            if (this.status === 200) {
                const json = JSON.parse(xmlHttp.response);
                displayFormData(json)
            } else if (this.status === 404) { // NOT FOUND
                alert("Organization not found!");
                window.location.href = "../html/admin_page.html";
            } else if (this.status === 401) { // UNAUTHORIZED
                alert("You don't have persmission to edit this page!")
                window.location.href = "../html/admin_page.html";
            }
        }
    }
    const token = getCookie("sessionToken");
    xmlHttp.open("GET", url.concat(`?token=${token}`), true);
    xmlHttp.send();
}

/* Displays an event on the page. */
const displayFormData = (json) => {
    const nameElement = document.getElementById("name");
    nameElement.value = json.name;
}

/* To be called when a button is pressed. */
const edit = () => {
    const data = getFormData();
    sendRequest(data);
}

/* Sends a request to the server. Data is assumed to be already formed at this point. */
const sendRequest = (data) => {
    const urlParams = new URLSearchParams(window.location.search);
    const orgId = urlParams.get("id");
    const currentUrl = window.location.href;
    const index = currentUrl.substring("http://localhost:8080/".length).indexOf("/") + "http://localhost:8080/".length;
    const url = currentUrl.substring(0, index).concat(`/rest/event/edit/${eventId}`);

    const xmlHttp = new XMLHttpRequest();
    xmlHttp.onreadystatechange = function () {
        if (this.readyState === XMLHttpRequest.DONE && this.status === 200) {
            alert("The details have been updated!");
            window.location.href = "../html/admin_page.html";
        }
    }
    const token = getCookie("sessionToken");
    xmlHttp.open("POST", url.concat(`?token=${token}`), true);
    xmlHttp.setRequestHeader("Content-Type", "application/json");
    xmlHttp.send(JSON.stringify(data));
}

/* Forms JSON object from user input to be sent to the server. */
const getFormData = () => {
    const name = DOMPurify.sanitize(document.getElementById("name").value);

    const json = {
        "name": name,
    };

    return json;
}