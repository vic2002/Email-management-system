window.onload = () => {
    const urlParams = new URLSearchParams(window.location.search);
    const orgId = urlParams.get("id");
    loadData(orgId);
}

/* Automatically fills current data about the organization. */
const loadData = (organizationId) => {
    const current_url = window.location.href;
    const index = current_url.substring("http://localhost:8080/".length).indexOf("/") + "http://localhost:8080/".length;
    const url = current_url.substring(0, index).concat(`/rest/organization/${organizationId}`);

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

/* Displays an organization on the page. */
const displayFormData = (json) => {
    const nameElement = document.getElementById("name");
    const emailElement = document.getElementById("email");
    const websiteElement = document.getElementById("website");
    nameElement.value = json.name;
    emailElement.value = json.email;
    websiteElement.value= json.website;
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
    const url = currentUrl.substring(0, index).concat(`/rest/organization/edit/${orgId}`);
    
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
    const email = DOMPurify.sanitize(document.getElementById("email").value);
    const website = DOMPurify.sanitize(document.getElementById("website").value);

    const json = {
        "name": name,
        "email": email,
        "website": website,
    };

    return json;
}