// source / ref: https://getbootstrap.com/docs/5.3/forms/validation/#browser-defaults
document.addEventListener('DOMContentLoaded', function () {
    'use strict';

    // select the form
    const form = document.querySelector('.needs-validation');
    form.addEventListener('submit', function (event) {
        if (!validateFields()) {
            event.preventDefault();
            event.stopPropagation();
        }
        form.classList.add('was-validated');
    });

    function validateFields() {
        let isValid = true;
        if (!validateUsername()) isValid = false;
        if (!validatePassword()) isValid = false;
        return isValid;
    }

    function validateUsername() {
        const input1 = document.getElementById('inputUsername');

        if (input1.value.trim() === '') {
            showError(input1, 'Username is required.');
            return false;
        } else if (input1.value.length < 3 || input1.value.length > 20) {
            showError(input1, 'Username must be 3-20 characters.');
            return false;
        } else {
            showSuccess(input1);
            return true;
        }
    }

    function validatePassword() {
        const input1 = document.getElementById('inputPassword');

        if (input1.value.trim() === '') {
            showError(input1, 'Password is required.');
            return false;
        } else if (input1.value.length < 6 || input1.value.length > 20) {
            showError(input1, 'Password must be 6-20 characters.');
            return false;
        } else {
            showSuccess(input1);
            return true;
        }
    }

    function showError(input, message) {
        // get the parent
        const formControl = input.parentElement;

        // add message to the .invalid-feedback div
        const msg = formControl.querySelector('.invalid-feedback');
        msg.textContent = message;

        // add the bootstrap style
        input.classList.add('is-invalid');
        input.classList.remove('is-valid');
    }

    function showSuccess(input) {
        input.classList.remove('is-invalid');
        input.classList.add('is-valid');
    }
});
