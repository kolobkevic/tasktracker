const hostApi = 'http://localhost:8080';
const registrationUrl = '/auth/registration';
const authenticationUrl = '/auth/authentication';
const logoutUrl = '/logout';

let signUpForm = document.getElementById('signUpForm');
let signInForm = document.getElementById('signInForm');
let signUpError = document.getElementById('signUpError');
let signInError = document.getElementById('signInError');
let usernameElem = document.getElementById('username');
let signOutButton = document.getElementById('signOutButton');
let user;

signUpForm.addEventListener('submit', submitForm);
signInForm.addEventListener('submit', submitForm);
signOutButton.addEventListener('click', logout);

function submitForm(event, form) {
    event.preventDefault()
    form = event.currentTarget
    let formData = new FormData(form)
    let json = {};
    formData.forEach(function (value, key) {
        json[key] = value;
    });

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
            user = getUsernameAndTokenFromJwt(response.token)
            showUsername()

            console.log(user)
            console.log(user.token)
            console.log(user.username)
            showUsername()
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

function getUsernameAndTokenFromJwt(token) {
    user = {username: parseJwt(token).username, "token": token};
    console.log("User:")
    console.log(user.username)
    console.log(user.token)
    return user
}

function isUserLoggedIn() {
    return user;
}

function showUsername() {
    usernameElem.textContent = user.username
}

function logout(event) {
    user = {}
    console.log("Trying to log out")

    $.ajax({
        url: hostApi + logoutUrl,
        type: "post",
        timeout: 10000,
    })
        .done(function (response) {
            console.log(response)
            console.log(user.username)
            usernameElem.textContent = ""
        })
}