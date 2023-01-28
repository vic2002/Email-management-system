window.onload = () => {
    if (document.getElementById("association") != null) {
        document.getElementById("association").innerText = getCookie("organizationName");
    }
    const isAdmin = (getCookie("isAdmin") === 'true');
    const buttonsDiv = document.getElementById("buttons");
    if (isAdmin) {
        const btn = `<button class="button" onclick="window.location='admin_page.html'"> <img src="../images/admin-logo.png" alt="admin logo" class="logo"> Admin </button>`
        buttonsDiv.insertAdjacentHTML('beforeend', btn);
    }
}