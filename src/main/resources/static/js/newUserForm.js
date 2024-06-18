function checkPasswords() {
    const password = document.getElementById('userPassword');
    const confirm = document.getElementById('confirmPassword');
    const message = document.getElementById('message');

    if (password.value === confirm.value) {
        message.textContent = 'Passwords match';
        message.style.color = 'green';
    } else {
        message.textContent = 'Passwords do not match';
        message.style.color = 'red';
    }
}

function validatePasswordMatch() {
    const password = document.getElementById('userPassword');
    const confirm = document.getElementById('confirmPassword');

    if (password.value !== confirm.value) {
        alert('Passwords do not match!');
        return false;
    }
    return true;
}

