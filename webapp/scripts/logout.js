/* Performs logout by sending to the server a current cookie to be deleted.
In case of success a user is redirected to the home page. */
const logout = () => {
    const current_url = window.location.href;
    const index = current_url.substring("http://localhost:8080/".length).indexOf("/") + "http://localhost:8080/".length;
    const url = current_url.substring(0, index).concat("/rest/authentication/logout");

    const xmlHttp = new XMLHttpRequest();
    xmlHttp.onreadystatechange = function () {
        if (this.readyState === XMLHttpRequest.DONE) {
            window.location.href = "../index.html";
        }
    }
    const token = getCookie("sessionToken");
    xmlHttp.open("GET", url.concat(`?token=${token}`), true);
    xmlHttp.send();
}