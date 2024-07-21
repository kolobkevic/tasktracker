const hostApi = 'http://localhost:8080';

let signUpForm = document.getElementById('signUpForm');
signUpForm.addEventListener('submit', submitSignUpForm);

function submitSignUpForm(event){
    event.preventDefault()
    const formData = new FormData(signUpForm)
    let json = {};
    formData.forEach(function(value, key){
        json[key] = value;
    });

    $.ajax({
        url: hostApi + "/auth/registration",
        type: "post",
        contentType: "application/json",
        dataType: "json",
        data: JSON.stringify(json),
        timeout: 10000,
    })

}