let organizations = "";

window.onload = () => {
    getOrganizations();
}

/* To be called when a button is pressed. */
const create = () => {
    const current_url = window.location.href;
    const index = current_url.substring("http://localhost:8080/".length).indexOf("/") + "http://localhost:8080/".length;
    const url = current_url.substring(0, index).concat("/rest/users/create");

    const data = getFormData();
    sendRequest(data, url);
}

const getOrganizations = () => {
    const current_url = window.location.href;
    const index = current_url.substring("http://localhost:8080/".length).indexOf("/") + "http://localhost:8080/".length;
    const url = current_url.substring(0, index).concat("/rest/organizations/all");

    const xmlHttp = new XMLHttpRequest();
    xmlHttp.onreadystatechange = function () {
        if (this.readyState === XMLHttpRequest.DONE) {
            if (this.status === 200) { // OK
                const json = JSON.parse(this.responseText);
                json.organizations.forEach(organization => {
                    organizations = organizations.concat(`<option value="${organization.id}">${organization.name}</option>`);
                });
                document.getElementById("organization").insertAdjacentHTML('beforeend', organizations);
            } else if (this.status === 401) { // Unauthorized
                alert("You don't have permission to do that!");
                organizations.location.href = "../html/home.html";
            }
        }
    }
    const token = getCookie("sessionToken");
    xmlHttp.open("GET", url.concat(`?token=${token}`), true);
    xmlHttp.send();
}

/* Sends a request to the server. Data is assumed to be already formed at this point. */
const sendRequest = (data, url) => {
    const xmlHttp = new XMLHttpRequest();
    xmlHttp.onreadystatechange = function () {
        if (this.readyState === XMLHttpRequest.DONE && this.status === 200) {
            alert("User is created!");
            window.location.href = "../html/admin_page.html";
        }
    }
    const token = getCookie("sessionToken");
    xmlHttp.open("POST", url.concat(`?token=${token}`), true);
    xmlHttp.setRequestHeader("Content-Type", "application/json");
    xmlHttp.send(JSON.stringify(data));
}

/* Makes a JSON object from user input in HTML form. */
const getFormData = () => {
    const name = DOMPurify.sanitize(document.getElementById("name").value);
    const email = DOMPurify.sanitize(document.getElementById("email").value);
    const plaintext_password = DOMPurify.sanitize(document.getElementById("plaintext_password").value);
    const organization = DOMPurify.sanitize(document.getElementById("organization").value);

    const json = {
        "name": name,
        "email": email,
        "plaintext_password": plaintext_password,
    };

    return json;
}