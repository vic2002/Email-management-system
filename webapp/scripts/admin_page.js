window.onload = () => {
    if (document.getElementById("association") != null) {
        document.getElementById("association").innerText = getCookie("organizationName");
    }
    search();
}

const search = () => {
    const urlParams = new URLSearchParams(window.location.search);
    const resultsAmount = 20;
    const userPage = urlParams.get("userPage") ? urlParams.get("userPage") : 1;
    const orgPage = urlParams.get("orgPage") ? urlParams.get("orgPage") : 1;
    const eventPage = urlParams.get("eventPage") ? urlParams.get("eventPage") : 1;
    const messagePage = urlParams.get("messagePage") ? urlParams.get("messagePage") : 1;
    load(resultsAmount, orgPage, eventPage, userPage, messagePage);
}

const load = (resultsAmount, orgPage, eventPage, userPage, messagePage) => {
    const orgTable = document.getElementById("organizations-table");
    const eventTable = document.getElementById("events-table");
    const userTable = document.getElementById("users-table");
    const messageTable = document.getElementById("users-table");
    const orgPagination = document.getElementById("organization-pagination");
    const eventPagination = document.getElementById("event-pagination");
    const userPagination = document.getElementById("user-pagination");
    //const messagePegination = document.getElementById("message-pagination");

    requestOrgData(getOrgFormData(), resultsAmount, orgTable, orgPagination, orgPage);
    requestEventData(getEventFormData(), resultsAmount, eventTable, eventPagination, eventPage);
    requestUserData(getUserFormData(), resultsAmount, userTable, userPagination, userPage);
    //requestMessageData(getMessageFormData(), resultsAmount, messageTable, messagePegination, messagePage)
}

/* A function responsible for buttons behaviour. */
const select = (option) => {
    const orgDiv = document.getElementById("organizations");
    const userDiv = document.getElementById("users");
    const eventDiv = document.getElementById("events");
    const messageDiv = document.getElementById("messages");
    const orgBtn = document.getElementById("organizations-btn");
    const userBtn = document.getElementById("users-btn");
    const eventBtn = document.getElementById("events-btn");
    const messageBtn = document.getElementById("messages-btn")
    if (option === "organizations") {
        setActive(orgDiv, orgBtn, userDiv, userBtn, eventDiv, eventBtn, messageDiv, messageBtn);
    } else if (option === "users") {
        setActive(userDiv, userBtn, orgDiv, orgBtn, eventDiv, eventBtn, messageDiv, messageBtn);
    } else if (option === "events") {
        setActive(eventDiv, eventBtn, orgDiv, orgBtn, userDiv, userBtn, messageDiv, messageBtn);
    } else if (option === "messages") {
        setActive(messageDiv, messageBtn, eventDiv, eventBtn, orgDiv, orgBtn, userDiv, userBtn);
    }
}

const setActive = (active, activeBtn, deactive1, deactive1Btn, deactive2, deactive2Btn, deactive3, deactive3Btn) => {
    active.style.display = "block";
    activeBtn.classList.add("green");
    deactive1.style.display = "none";
    deactive1Btn.classList.remove("green");
    deactive2.style.display = "none";
    deactive2Btn.classList.remove("green");
    deactive3.style.display = "none";
    deactive3Btn.classList.remove("green");
}

//////////////////////////////////////////////////////////////////////////////////

/* Selectors. */

const getOrgFormData = () => {
    const organizationSearch = document.getElementById("search-organization").value;
    return organizationSearch;
}

const getEventFormData = () => {
    const eventsSearch = document.getElementById("search-event").value;
    return eventsSearch;
}

const getUserFormData = () => {
    const usersSearch = document.getElementById("search-user").value;
    return usersSearch;
}

//////////////////////////////////////////////////////////////////////////////////

const getMessageFormData = () => {
    const messagesSearch = document.getElementById("search-message").value;
    const afterFilter = document.getElementById("after").value;
    const beforeFilter = document.getElementById("before").value;
    const isDutch = document.getElementById("dutch").checked;
    const sort = document.getElementById("sortoption").value;
    const jsonBody = {
        "keyword": messagesSearch,
        "fromDate": afterFilter,
        "toDate": beforeFilter,
        "sort": sort,
        "isDutch": isDutch,
    };
    return jsonBody;
}

/* Obtains an information about organizations from back end and displays it on a page. */
const requestOrgData = (reqData, resultsAmount, table, pagination, page) => {
    const method = "GET";
    const urlString = window.location.href;
    const index = urlString.substring("http://kick-in-3.paas.hosted-by-previder.com/".length).indexOf("/") + "http://kick-in-3.paas.hosted-by-previder.com/".length;
    const xhr = new XMLHttpRequest(), url = urlString.substring(0, index).concat("/rest/organizations/filtered");

    xhr.onreadystatechange = function () {
        if (this.readyState === XMLHttpRequest.DONE && this.status === 200) {
            let jsonResponse = JSON.parse(xhr.response);
            const num = jsonResponse.numberOfOrganizations;
            displayData(jsonResponse.organizations, num, resultsAmount, table, pagination, page, getOrgString, "setOrgPage");
        }
    };

    const token = getCookie("sessionToken");
    xhr.open(method, url.concat(`?filter=${reqData}&token=${token}`), true);
    xhr.send();
}

/* Obtains an information about events from back end and displays it on a page. */
const requestEventData = (reqData, resultsAmount, table, pagination, page) => {
    const method = "GET";
    const urlString = window.location.href;
    const index = urlString.substring("http://kick-in-3.paas.hosted-by-previder.com/".length).indexOf("/") + "http://kick-in-3.paas.hosted-by-previder.com/".length;
    const xhr = new XMLHttpRequest(), url = urlString.substring(0, index).concat("/rest/events/filtered");

    xhr.onreadystatechange = function () {
        if (this.readyState === XMLHttpRequest.DONE && this.status === 200) {
            let jsonResponse = JSON.parse(xhr.response);
            const num = jsonResponse.numberOfEvents;
            displayData(jsonResponse.events, num, resultsAmount, table, pagination, page, getEventString, "setEventPage");
        }
    };

    const token = getCookie("sessionToken");
    xhr.open(method, url.concat(`?filter=${reqData}&token=${token}`), true);
    xhr.send();
}

/* Obtains an information about users from back end and displays it on a page. */
const requestUserData = (reqData, resultsAmount, table, pagination, page) => {
    const method = "GET";
    const urlString = window.location.href;
    const index = urlString.substring("http://kick-in-3.paas.hosted-by-previder.com/".length).indexOf("/") + "http://kick-in-3.paas.hosted-by-previder.com/".length;
    const xhr = new XMLHttpRequest(), url = urlString.substring(0, index).concat("/rest/users/filtered");

    xhr.onreadystatechange = function () {
        if (this.readyState === XMLHttpRequest.DONE && this.status === 200) {
            let jsonResponse = JSON.parse(xhr.response);
            const num = jsonResponse.numberOfUsers;
            displayData(jsonResponse.users, num, resultsAmount, table, pagination, page, getUserString, "setUserPage");
        }
    };

    const token = getCookie("sessionToken");
    xhr.open(method, url.concat(`?filter=${reqData}&token=${token}`), true);
    xhr.send();
}

/* Obtains an information about messages from back end and displays it on a page. */
const requestMessageData = (reqData, resultsAmount, table, pagination, page) => {
    const method = "POST";
    const urlString = window.location.href;
    const index = urlString.substring("http://kick-in-3.paas.hosted-by-previder.com/".length).indexOf("/") + "http://kick-in-3.paas.hosted-by-previder.com/".length;
    const xhr = new XMLHttpRequest(), url = urlString.substring(0, index).concat("/rest/messages/filtered");

    xhr.onreadystatechange = function () {
        if (this.readyState === XMLHttpRequest.DONE && this.status === 200) {
            let jsonResponse = JSON.parse(xhr.response);
            const num = jsonResponse.numberOfMessages;
            displayData(jsonResponse.messages, num, resultsAmount, table, pagination, page, getMessageString, "setMessagePage");
        }
    };
    const token = getCookie("sessionToken");
    xhr.open(method, url.concat(`?token=${token}`), true);
    xhr.setRequestHeader("Content-Type", "application/json");
    xhr.send(JSON.stringify(reqData));
}

const displayData = (list, num, resultsAmount, table, pagination, page, getMethodString, method) => {
    clearTable(table);
    loadPagination(pagination, page, Math.ceil(num / resultsAmount), method);
    if (num != 0) {
        table.firstElementChild.insertAdjacentHTML('beforeend', getMethodString(list, resultsAmount, page));
    } else {
        table.firstElementChild.insertAdjacentHTML('beforeend', `<div style="color: #ff0000; text-align: center">No results</div>`);
    }
}

//////////////////////////////////////////////////////////////////////////////////

/* Functions to prepare HTML representation of the objects. */

const getOrgString = (organizations, resultsAmount, page) => {
    let tmp = ""//`<tr><th>name</th><th>Email</th><th>Website</th><th></th></tr >`;
    let i = 0;
    organizations.slice(resultsAmount * (page - 1)).slice(0, resultsAmount).forEach(org => {
        tmp = tmp.concat(`<tr ${i % 2 == 0 ? "class='green'" : ""}">
                            <td>${org.name}</td>
                            <td>${org.email}</td>
                            <td><a href=${org.website}>${org.website}</a></td>
                            <td>
                                <button class="edit ${i % 2 == 0 ? "" : "green"}" onclick="editOrganization(${org.id})">Edit</button>
                                <button class="delete bg-danger" onclick="deleteOrganization(${org.id})">Delete</button>
                            </td>
                        </tr>`);
        i++;
    });
    return tmp;
}

const getEventString = (events, resultsAmount, page) => {
    let tmp = ""//`<tr><th>Name</th><th>Created By</th><th>Created At</th><th>Happening On</th><th></th></tr>`;
    let i = 0;

    const options = {
        day: "numeric",
        month: "long",
        year: "numeric"
    }
    events.slice(resultsAmount * (page - 1)).slice(0, resultsAmount).forEach(e => {
        tmp = tmp.concat(`<tr ${i % 2 == 0 ? "class='green'" : ""}">
                            <td>${e.name}</td>
                            <td>${e.createdBy}</td>
                            <td>${new Date(e.createdAt).toLocaleString("en-GB", options)}</td>
                            <td>${new Date(e.startingOn).toLocaleString("en-GB", options)}</td>
                            <td>${new Date(e.endingOn).toLocaleString("en-GB", options)}</td>
                            <td>
                                <button class="edit ${i % 2 == 0 ? "" : "green"}" onclick="editEvent(${e.id})">Edit</button>
                                <button class="delete bg-danger" onclick="deleteEvent(${e.id})">Delete</button>
                            </td>
                        </tr>`);
        i++;
    });
    return tmp;
}

const getUserString = (users, resultsAmount, page) => {
    let tmp = "";//`<tr><th>Name</th><th>Created By</th><th>Created At</th><th>Happening On</th><th></th></tr>`;
    let i = 0;
    users.slice(resultsAmount * (page - 1)).slice(0, resultsAmount).forEach(user => {
        tmp = tmp.concat(`<tr ${i % 2 == 0 ? "class='green'" : ""}">
                            <td>${user.username}</td>
                            <td>${user.email}</td>
                            <td>${user.role}</td>
                            <td>${user.organization}</td>
                            <td>
                                <button class="edit ${i % 2 == 0 ? "" : "green"}" onclick="editUser(${user.email})">Edit</button>
                                <button class="delete bg-danger" onclick="deleteUser(${user.email})">Delete</button>
                            </td>
                        </tr>`);
        i++;
    });
    return tmp;
}

const getMessageString = (messages, resultsAmount, page) => {
    let tmp = "";//`<tr><th>Sender</th><th>Sender Email</th><th>Subject</th><th>Number Of Attachments</th><th>Sent To (&#8470; of people)</th><th>Read By (&#8470; of people)</th><th></th></tr>`;
    let i = 0;
    console.log("messages: ", messages);
    messages.messages.slice(resultsAmount * (page - 1)).slice(0, resultsAmount).forEach(message => {
        tmp = tmp.concat(`<tr ${i % 2 == 0 ? "class='green'" : ""}">
                            <td>${message.sender}</td>
                            <td>${message.senderEmail}</td>
                            <td>${message.subject}</td>
                            <td>${message.numberOfAttachments}</td>
                            <td>${message.sentTo}</td>
                            <td>${message.readBy}</td>
                            <td>
                                <button class="edit ${i % 2 == 0 ? "" : "green"}" onclick="editMessage(${message.id})">Edit</button>
                                <button class="delete bg-danger" onclick="deleteMessage(${message.id})">Delete</button>
                            </td>
                        </tr>`);
        i++;
    });
    return tmp;
}

//////////////////////////////////////////////////////////////////////////////////

const loadPagination = (div, page, pagesCount, method) => {
    let tmp = "";
    clearDiv(div);
    for (let i = 1; i <= pagesCount; i++) {
        if (i == page) {
            tmp = tmp.concat(`<a class='pointer' href="#">${i}</a>`);
        } else {
            tmp = tmp.concat(`<a class='pointer' onclick='${method}(${i})'><span>${i}</span></a>`);
        }
    }
    div.insertAdjacentHTML('beforeend', tmp);
}

const clearDiv = (div) => {
    for (let i = div.childNodes.length - 1; i >= 0; i--) {
        div.removeChild(div.childNodes[i]);
    }
}

const setEventPage = (page) => {
    const urlParams = new URLSearchParams(window.location.search);
    urlParams.set("eventPage", page);
    window.location.href = window.location.origin.concat(window.location.pathname.concat("?".concat(urlParams.toString())));
}

const setOrgPage = (page) => {
    const urlParams = new URLSearchParams(window.location.search);
    urlParams.set("orgPage", page);
    window.location.href = window.location.origin.concat(window.location.pathname.concat("?".concat(urlParams.toString())));
}

const setUserPage = (page) => {
    const urlParams = new URLSearchParams(window.location.search);
    urlParams.set("userPage", page);
    window.location.href = window.location.origin.concat(window.location.pathname.concat("?".concat(urlParams.toString())));
}

const clearTable = (table) => {
    for (let i = table.firstElementChild.childNodes.length - 1; i >= 1; i--) {
        table.firstElementChild.removeChild(table.firstElementChild.childNodes[i]);
    }
}

/* Invoke an edit event page. Event id is needed to be passed to this page to know which event to update. */
const editEvent = (eventId) => {
    const index = window.location.href.substring("http://kick-in-3.paas.hosted-by-previder.com/".length).indexOf("/") + "http://kick-in-3.paas.hosted-by-previder.com/".length;
    window.location.href = window.location.href.substring(0, index).concat(`/html/new_details_event.html?id=${eventId}`);
}

/* Invoke an edit organization page. Organization id is needed to be passed to this page to know which organization to update. */
const editOrganization = (organizationId) => {
    const index = window.location.href.substring("http://kick-in-3.paas.hosted-by-previder.com/".length).indexOf("/") + "http://kick-in-3.paas.hosted-by-previder.com/".length;
    window.location.href = window.location.href.substring(0, index).concat(`/html/new_details_organization.html?id=${organizationId}`);
}

/* Delete an organization by a unique organization id. */
const deleteOrganization = (organizationId) => {
    const answer = confirm("Do you actually want to delete this organization?");
    if (answer) {
        const method = "DELETE";
        const urlString = window.location.href;
        const index = urlString.substring("http://kick-in-3.paas.hosted-by-previder.com/".length).indexOf("/") + "http://kick-in-3.paas.hosted-by-previder.com/".length;
        const xhr = new XMLHttpRequest(), url = urlString.substring(0, index).concat(`/rest/organization/delete/${organizationId}`);
        xhr.onreadystatechange = function () {
            if (this.readyState === XMLHttpRequest.DONE) {
                window.location.reload();
                select('organizations');
            }
        }
        const token = getCookie("sessionToken");
        xhr.open(method, url.concat(`?token=${token}`), true);
        xhr.send();
    }
}

/* Delete an event by a unique event id. */
const deleteEvent = (eventId) => {
    const answer = confirm("Do you actually want to delete this event?");
    if (answer) {
        const method = "DELETE";
        const urlString = window.location.href;
        const index = urlString.substring("http://kick-in-3.paas.hosted-by-previder.com/".length).indexOf("/") + "http://kick-in-3.paas.hosted-by-previder.com/".length;
        const xhr = new XMLHttpRequest(), url = urlString.substring(0, index).concat(`/rest/event/delete/${eventId}`);
        xhr.onreadystatechange = function () {
            if (this.readyState === XMLHttpRequest.DONE) {
                window.location.reload();
                select('events');
            }
        }
        const token = getCookie("sessionToken");
        xhr.open(method, url.concat(`?token=${token}`), true);
        xhr.send();
    }
}

/* Delete a user by a provided username. */
const deleteUser = (username) => {
    const answer = confirm("Do you actually want to delete this user?");
    if (answer) {
        const method = "DELETE";
        const urlString = window.location.href;
        const index = urlString.substring("http://kick-in-3.paas.hosted-by-previder.com/".length).indexOf("/") + "http://kick-in-3.paas.hosted-by-previder.com/".length;
        const xhr = new XMLHttpRequest(), url = urlString.substring(0, index).concat(`/rest/user/delete/${username}`);
        xhr.onreadystatechange = function () {
            if (this.readyState === XMLHttpRequest.DONE) {
                window.location.reload();
                select('events');
            }
        }
        const token = getCookie("sessionToken");
        xhr.open(method, url.concat(`?token=${token}`), true);
        xhr.send();
    }
}

/* Delete a message by a unique message id. */
const deleteMessage = (messageId) => {
    const answer = confirm("Do you actually want to delete this message?");
    if (answer) {
        const method = "DELETE";
        const urlString = window.location.href;
        const index = urlString.substring("http://kick-in-3.paas.hosted-by-previder.com/".length).indexOf("/") + "http://kick-in-3.paas.hosted-by-previder.com/".length;
        const xhr = new XMLHttpRequest(), url = urlString.substring(0, index).concat(`/rest/message/delete/${messageId}`);
        xhr.onreadystatechange = function () {
            if (this.readyState === XMLHttpRequest.DONE) {
                window.location.reload();
                select('events');
            }
        }
        const token = getCookie("sessionToken");
        xhr.open(method, url.concat(`?token=${token}`), true);
        xhr.send();
    }
}