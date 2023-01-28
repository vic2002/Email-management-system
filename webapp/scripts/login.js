/* Reads user input and tries to perform log in. */
function log_in() {
    // Remove index.html from the end
    const current_url = window.location.href;
    const index = current_url.substring("http://kick-in-3.paas.hosted-by-previder.com/".length).indexOf("/") + "http://kick-in-3.paas.hosted-by-previder.com/".length;
    const url = current_url.substring(0, index).concat("/rest/authentication/login");

    var email = DOMPurify.sanitize(document.getElementById("email").value);
    var password = DOMPurify.sanitize(document.getElementById("password").value);

    const xmlHttp = new XMLHttpRequest();
    xmlHttp.onreadystatechange = function () {
        if (this.readyState === XMLHttpRequest.DONE) {
            handleStatus(this.status, xmlHttp.response);
        }
    }
    xmlHttp.open("GET", url.concat(`?login=${email}&password=${password}`), true);
    xmlHttp.send();
}

const handleStatus = (status, response) => {
    const errorDiv = document.getElementById("error-message");
    if (status === 200) {
        /* Here a token arrives. */
        let json = JSON.parse(response);
        setCookie("isAdmin", json.isAdmin, 1);
        setCookie("username", json.username, 1);
        setCookie("sessionToken", json.token, 1);
        setCookie("organizationName", json.orgName, 1);
        const index = window.location.href.substring("http://kick-in-3.paas.hosted-by-previder.com/".length).indexOf("/") + "http://kick-in-3.paas.hosted-by-previder.com/".length;
        window.location.href = window.location.href.substring(0, index).concat("/html/home.html");
    } else if (status === 401) { // Unathorized (Wrong password)
        errorDiv.innerText = "Wrong login credentials!"
    } else if (status === 404) { // Not found (Not registered)
        errorDiv.innerText = "User not registered!";
    }
}