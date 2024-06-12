window.onload = function() {
    // Set initial value
    updateAvailableDays();

    let leaveType = document.getElementById("leaveType");
    leaveType.addEventListener("change", function (event) {
        updateAvailableDays(event.target);
    });
}

function updateAvailableDays() {
    var leaveType = document.getElementById("leaveType").value;
    var medicalDays = document.getElementById("medicalDays").value;
    var annualDays = document.getElementById("annualDays").value;
    var compensationDays = document.getElementById("compensationDays").value;
    var compassionateDays = document.getElementById("compassionateDays").value;

    var availableDays= 0;

    if (leaveType === 'MEDICAL') {
        availableDays = medicalDays;
    } else if (leaveType === 'ANNUAL') {
        availableDays = annualDays;
    } else if (leaveType === 'COMPENSATION') {
        availableDays = compensationDays;
    } else if (leaveType === 'COMPASSIONATE') {
        availableDays = compassionateDays + " per event";
    } else if (leaveType === 'UNPAID') {
        availableDays = "";
    }
    document.getElementById("availableDays").innerText = availableDays;
}


