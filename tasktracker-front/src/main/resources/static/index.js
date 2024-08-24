const hostApi = 'http://localhost:8080';
const registrationUrl = '/auth/registration';
const authenticationUrl = '/auth/authentication';
const tasksUrl = '/api/v1/tasks';
const bearerHeader = 'Bearer '

let signUpForm = document.getElementById('signUpForm');
let signInForm = document.getElementById('signInForm');
let signUpError = document.getElementById('signUpError');
let signInError = document.getElementById('signInError');
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
                token: response.token
            })

            showTasks()
            showAuthorizeElements()

            form.reset();
            $('#modalSignIn').modal('hide');
            $('#modalSignUp').modal('hide');
        })
        .fail(function (jqXHR) {
                if (jqXHR.status === 401) {
                    error.textContent = "Wrong credentials";
                } else if (jqXHR.status === 409) {
                    error.textContent("User with such an email already exists")
                } else {
                    error.textContent("Something goes wrong, try again later")
                }
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
    return !!localStorage.webUser;
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
    $.ajax({
        url: hostApi + tasksUrl,
        type: "get",
        headers: {Authorization: bearerHeader + JSON.parse(localStorage.webUser).token}
    })
        .done(function (response) {
            let tasks = response
            console.log(response)

            const taskContainer = $("#inWorkTasksContainer").empty()
            $.each(tasks, function (i, task) {
                const card = createTaskCard(task)
                taskContainer.append(card)
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

    const title = $('<div>').addClass('card_title').append($('<h5>').text(task.head));
    const content = $('<div>').addClass('card_content').append($('<p>').text(task.content));
    const status = $('<div>').addClass('card_status').append($('<p>').text(task.status));

    const created = $('<div>').addClass('card_created');
    created.append($('<p>').addClass('card_created_head').text('created:'));
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
    const head = $(event.target).closest(".card").find(".card_title h5").text();
    const content = $(event.target).closest(".card").find(".card_content p").text();
    const status = $(event.target).closest(".card").find(".card_status p").text();

    $(".taskId input").attr("value", id);

    if (btn.includes('edit')) {
        $("#editModalHead").attr("value", head);
        $("#editModalContent").text(content);
        $("#editModalStatus").attr("value", status)
    } else if (btn.includes('delete')) {
        $("#deleteModalHead").attr("value", head);
        $("#deleteModalContent").text(content);
        $("#deleteModalStatus").attr("value", status)
    }
}

function deleteTask(event) {
    event.preventDefault()

    let formData = new FormData(deleteForm)
    let id = formData.get('id')

    $.ajax({
        url: hostApi + tasksUrl + `/${id}`,
        type: "delete",
        contentType: "application/json",
        timeout: 10000,
        headers: {Authorization: bearerHeader + JSON.parse(localStorage.webUser).token}
    })
        .done(function (response) {
            showTasks()
            $('#deleteModal').modal('hide');
        })
        .fail(function () {
                console.log("Something goes wrong")
            }
        )
}

function editTask(event) {
    event.preventDefault()
    let json = getJsonFromForm(editForm)

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
            showTasks()
            $('#editModal').modal('hide');
        })
        .fail(function () {
                console.log("Something goes wrong")
            }
        )
}

function createTask(event) {
    event.preventDefault()
    let json = getJsonFromForm(createForm)

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
            showTasks()
            $('#createModal').modal('hide');
        })
        .fail(function () {
                console.log("Something goes wrong")
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

function convertDateToString(date){
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