function is_euqal () {
    url = "" //TODO
    pass1 = document.getElementById("pass1").value;
    pass2 = document.getElementById("pass2").value;
    if (pass1 != pass2) {
        alert("Passwords do not match!");
        return;
    } else {
        xmlhttp = new XMLHttpRequest();
        xml.onreadystatechange = function () {
            if (this.readyState === XMLHttpRequest.DONE && this.status === 204) {
                alert("Password change is successful!");
            }
        }
        xmlhttp.open();
        xmlhttp.send("POST", url, true);
    }
}