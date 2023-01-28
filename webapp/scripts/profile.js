/* Immediately fill in a data about a user which is currently logged in. */
window.onload = () => {
    if (document.getElementById("association") != null) {
        document.getElementById("association").innerText = getCookie("organizationName");
    }
    const urlString = window.location.href;
    const index = urlString.substring("http://localhost:8080/".length).indexOf("/") + "http://localhost:8080/".length;
    const xml = new XMLHttpRequest(), url = urlString.substring(0, index).concat("/rest/organization");
    xml.onreadystatechange = function() {
        if (this.readyState === XMLHttpRequest.DONE && this.status === 200) {
            let jsonResponse = JSON.parse(xml.responseText);
            document.getElementById("org-name").innerHTML = "Organisation: " + jsonResponse.name;
            document.getElementById("mail").innerHTML = "Email: " + jsonResponse.email;
            document.getElementById("website").href = jsonResponse.website;
            document.getElementById("association").innerText = jsonResponse.name;
        }
    };

    const token = getCookie("sessionToken");
    xml.open("GET", url.concat(`?token=${token}`), true);
    xml.send();
};