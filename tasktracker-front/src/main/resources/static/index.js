const hostApi = 'http://localhost:8080';
const registrationUrl = '/auth/registration';
const authenticationUrl = '/auth/authentication';
const tasksUrl = '/api/v1/tasks';
const bearerHeader = 'Bearer '

let signUpForm = document.getElementById('signUpForm');
let signInForm = document.getElementById('signInForm');
let signUpError = document.getElementById('signUpError');
let signInError = document.getElementById('signInError');
let createTaskModalError = document.getElementById('createTaskModalError');
let editTaskModalError = document.getElementById('editTaskModalError');
let usernameElem = document.getElementById('username');
let signOutButton = document.getElementById('signOutButton');
let deleteForm = document.getElementById('deleteForm');
let editForm = document.getElementById('editForm');
let createForm = document.getElementById('createForm');
let createButton = document.getElementById('createButton');
let signInButton = document.getElementById('signInButton')
let signUpButton = document.getElementById('signUpButton')
let inWorkTaskContainer = document.getElementById('inWorkTasksContainer')
let doneWorkTaskContainer = document.getElementById('doneTasksContainer')
let deleteButton;
let editButton;

signUpForm.addEventListener('submit', submitForm);
signInForm.addEventListener('submit', submitForm);
signOutButton.addEventListener('click', logout);
deleteForm.addEventListener('submit', deleteTask);
editForm.addEventListener('submit', editTask);
createForm.addEventListener('submit', createTask)

$(document).ready(function () {
    if (isUserLoggedIn()) {
        showTasks()
        showAuthorizeElements()
    } else {
        showUnAuthorizeElements()
    }
})

function submitForm(event, form) {
    event.preventDefault()
    form = event.currentTarget

    let json = getJsonFromForm(form);
    let url;
    let error;
    if (form === signUpForm) {
        error = signUpError
        url = hostApi + registrationUrl
        console.log("signUpForm, url = " + url)
    } else if (form === signInForm) {
        error = signInError
        url = hostApi + authenticationUrl
        console.log("signInForm, url = " + url)
    }

    $.ajax({
        url: url,
        type: "post",
        contentType: "application/json",
        dataType: "json",
        data: JSON.stringify(json),
        timeout: 10000,
    })
        .done(function (response) {
            localStorage.webUser = JSON.stringify({
                username: parseJwt(response.token).username,
                token: response.token,
                expAt: parseJwt(response.token).exp
            })

            showTasks()
            showAuthorizeElements()

            form.reset();
            $('#modalSignIn').modal('hide');
            $('#modalSignUp').modal('hide');
        })
        .fail(function (errResponse) {
                error.textContent = errResponse.responseJSON.message
            }
        )
}

function parseJwt(token) {
    let base64Url = token.split('.')[1];
    let base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    let jsonPayload = decodeURIComponent(window.atob(base64).split('').map(function (c) {
        return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
    }).join(''));
    return JSON.parse(jsonPayload);
}

function isUserLoggedIn() {
    return !!(localStorage.webUser && !isTokenExpired());
}

function logout(event) {
    if (isUserLoggedIn()) {
        console.log("Trying to log out")
        localStorage.clear()
        showUnAuthorizeElements()
    } else {
        console.log("No user is logged in")
    }
}

function showTasks() {
    if (isTokenExpired()) {
        console.log("Token is expired")
        logout()
        return
    }
    $.ajax({
        url: hostApi + tasksUrl,
        type: "get",
        headers: {Authorization: bearerHeader + JSON.parse(localStorage.webUser).token}
    })
        .done(function (response) {
            let tasks = response
            console.log(response)

            const inWorkTaskContainer = $("#inWorkTasksContainer").empty()
            const doneTaskContainer = $("#doneTasksContainer").empty()
            $.each(tasks, function (i, task) {
                const card = createTaskCard(task)
                if (task.status === "DONE") {
                    doneTaskContainer.append(card)
                } else {
                    inWorkTaskContainer.append(card)
                }
            })

            deleteButton = document.getElementsByClassName('btn btn-link delete-button')
            $.each(deleteButton, function (i, btn) {
                btn.addEventListener('click', fillCardModal);
            })

            editButton = document.getElementsByClassName('btn btn-link edit-button')
            $.each(editButton, function (i, btn) {
                btn.addEventListener('click', fillCardModal);
            })
        })
}

function createTaskCard(task) {
    const card = $('<div>').addClass('card');

    const title = $('<div>').addClass('card_title').append($('<h5>').text(task.title));
    const content = $('<div>').addClass('card_content').append($('<p>').text(task.content));
    const status = $('<div>').addClass('card_status').append($('<p>').text(task.status));

    const created = $('<div>').addClass('card_created');
    created.append($('<p>').addClass('card_created_title').text('created:'));
    created.append($('<p>').addClass('card_created_date').text(convertDateToString(task.createdAt)));

    const buttons = $('<div>').addClass('card_buttons');
    buttons.append($('<div>').addClass('btn btn-link delete-button')
        .attr("data-bs-target", "#deleteModal").attr("data-bs-toggle", "modal")
        .text('Delete'));
    buttons.append($('<div>').addClass('btn btn-link edit-button')
        .attr("data-bs-target", "#editModal").attr("data-bs-toggle", "modal")
        .text('Edit'));

    card.append($('<div>').addClass('taskId').text(task.id));
    card.append(title);
    card.append(content);
    card.append(status);
    card.append(created);
    card.append(buttons);

    return card;
}

function fillCardModal(event, btn) {
    event.preventDefault()
    btn = event.currentTarget.className;

    const id = $(event.target).closest(".card").find(".taskId").text();
    const title = $(event.target).closest(".card").find(".card_title h5").text();
    const content = $(event.target).closest(".card").find(".card_content p").text();
    const status = $(event.target).closest(".card").find(".card_status p").text();

    $(".taskId input").attr("value", id);

    if (btn.includes('edit')) {
        $("#editModalTitle").attr("value", title);
        console.log("Title: " + title)
        $("#editModalContent").text(content);
        $("#editModalStatus").attr("value", status)
    } else if (btn.includes('delete')) {
        $("#deleteModalTitle").attr("value", title);
        $("#deleteModalContent").text(content);
        $("#deleteModalStatus").attr("value", status)
    }
}

function deleteTask(event) {
    event.preventDefault()

    let formData = new FormData(deleteForm)
    let id = formData.get('id')

    if (isTokenExpired()) {
        console.log("Token is expired")
        logout()
        return
    }

    $.ajax({
        url: hostApi + tasksUrl + `/${id}`,
        type: "delete",
        contentType: "application/json",
        timeout: 10000,
        headers: {Authorization: bearerHeader + JSON.parse(localStorage.webUser).token}
    })
        .done(function (response) {
            deleteForm.reset()
            showTasks()
            $('#deleteModal').modal('hide');
        })
        .fail(function (errResponse) {
                error.textContent = errResponse.responseJSON.message
            }
        )
}

function editTask(event) {
    event.preventDefault()
    let json = getJsonFromForm(editForm)

    if (isTokenExpired()) {
        console.log("Token is expired")
        logout()
        return
    }

    $.ajax({
        url: hostApi + tasksUrl + '/edit',
        type: "put",
        contentType: "application/json",
        dataType: "json",
        data: JSON.stringify(json),
        timeout: 10000,
        headers: {Authorization: bearerHeader + JSON.parse(localStorage.webUser).token}
    })
        .done(function (response) {
            editForm.reset()
            showTasks()
            $('#editModal').modal('hide');
        })
        .fail(function (errResponse) {
                editTaskModalError.textContent = errResponse.responseJSON.message
            }
        )
}

function createTask(event) {
    event.preventDefault()
    let json = getJsonFromForm(createForm)

    if (isTokenExpired()) {
        console.log("Token is expired")
        logout()
        return
    }

    $.ajax({
        url: hostApi + tasksUrl + '/create',
        type: "post",
        contentType: "application/json",
        dataType: "json",
        data: JSON.stringify(json),
        timeout: 10000,
        headers: {Authorization: bearerHeader + JSON.parse(localStorage.webUser).token}
    })
        .done(function (response) {
            createForm.reset()
            showTasks()
            $('#createModal').modal('hide');
        })
        .fail(function (errResponse) {
                createTaskModalError.textContent = errResponse.responseJSON.message
            }
        )
}

function getJsonFromForm(form) {
    let formData = new FormData(form)
    let json = {};
    formData.forEach(function (value, key) {
        json[key] = value;
    });

    return json
}

function showAuthorizeElements() {
    createButton.hidden = false
    signOutButton.hidden = false
    signInButton.hidden = true
    signUpButton.hidden = true
    usernameElem.textContent = JSON.parse(localStorage.webUser).username
}

function showUnAuthorizeElements() {
    createButton.hidden = true
    signOutButton.hidden = true
    signInButton.hidden = false
    signUpButton.hidden = false
    inWorkTaskContainer.replaceChildren()
    doneWorkTaskContainer.replaceChildren()
    usernameElem.textContent = ""
}

function convertDateToString(date) {
    const currentTimeZone = Intl.DateTimeFormat().resolvedOptions().timeZone
    const currentLocale = Intl.DateTimeFormat().resolvedOptions().locale
    const options = {
        year: 'numeric',
        month: 'long',
        day: 'numeric',
        hour: 'numeric',
        minute: 'numeric',
        second: 'numeric',
        timeZone: currentTimeZone
    };

    return Intl.DateTimeFormat(currentLocale, options).format(new Date(date))
}

function isTokenExpired() {
    let expiredAt = JSON.parse(localStorage.webUser).expAt
    return expiredAt < (Date.now() / 1000).toFixed();
}