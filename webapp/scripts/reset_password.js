function reset () {
    const current_url = window.location.href;
    const index = current_url.substring("http://localhost:8080/".length).indexOf("/") + "http://localhost:8080/".length;
    const url = current_url.substring(0, index).concat("/rest/authentication/reset");

    var email = DOMPurify.sanitize(document.getElementById("password").value);
    var password = DOMPurify.sanitize(document.getElementById("password").value);

    console.log(email);

    const xmlHttp = new XMLHttpRequest();
    xmlHttp.onreadystatechange = function () {
        if (this.readyState === XMLHttpRequest.DONE) {
            console.log("done");
        }
    }
    xmlHttp.open("GET", url.concat(`?login=${email}&password=${password}`), true);
    xmlHttp.send();
}