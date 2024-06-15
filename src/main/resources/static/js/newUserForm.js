
window.onload = function() {
    onChange();
}


function onChange() {
    const password = document.querySelector('input[name=userPassword]');
    const confirm = document.querySelector('input[name=confirmPassword]');
    if (confirm.value === password.value) {
        confirm.setCustomValidity('');
    } else {
        confirm.setCustomValidity('Passwords do not match');
    }
}

