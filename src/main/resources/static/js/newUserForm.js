// Testing onChange() does not work
// todo: fix bug password validation does not work
function onChange() {
    const password = document.querySelector('input[name=userPassword]');
    const confirm = document.querySelector('input[name=confirmPassword]');
    if (confirm.value === password.value) {
        confirm.setCustomValidity('');
    } else {
        confirm.setCustomValidity('Passwords do not match');
    }
}

var check = function() {
    const password = document.getElementById("userPassword");
    const confirm = document.getElementById("confirmPassword");

    if (password.value === confirm.value) {
        document.getElementById("message").style.color = "green";
        document.getElementById("message").innerHTML = "Passwords match!"
    } else {
        document.getElementById("message").style.color = "red";
        document.getElementById("message").innerHTML = "Passwords do not match!"
    }
}

