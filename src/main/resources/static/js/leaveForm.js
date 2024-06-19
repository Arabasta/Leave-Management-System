window.onload = function() {
    // Set initial value
    updateAvailableDays();

    let leaveType = document.getElementById("leaveType");
    leaveType.addEventListener("change", function (event) {
        updateAvailableDays(event.target);
    });



    // Get the public holiday data from the server and map to an Date array
    const publicHolidaysDataInput = document.getElementById("publicHolidays");
    const publicHolidaysData = publicHolidaysDataInput.value.replace(/[\[\]]/g, '').split(',');
    const publicHolidays = publicHolidaysData.map(date => new Date(date));

    // Check if a date is a weekend
    function isWeekend(date) {
        const day = date.getDay();
        return day === 0 || day === 6; // 0 = Sunday, 6 = Saturday
    }

    // Check if a date is a public holiday
    function isPublicHoliday(date) {
        const localDate = new Date(date.getFullYear(), date.getMonth(), date.getDate());
        const formattedDate = localDate.toISOString().slice(0, 10);
        const matchingHoliday = publicHolidays.find(holiday => {
            const holidayDate = new Date(holiday.getFullYear(), holiday.getMonth(), holiday.getDate());
            return holidayDate.toISOString().slice(0, 10) === formattedDate;
        });
        return !!matchingHoliday;
    }

// Count the number of days between two dates. >14 will be calendar days, otherwise working days
    function countDays(startDate, endDate) {
        const oneDay = 24 * 60 * 60 * 1000; // Hours * Minutes * Seconds * Milliseconds
        const diffDays = Math.round(Math.abs((endDate - startDate) / oneDay)) + 1; // +1 to include same day leave

        if (diffDays > 14) {
            return diffDays;
        } else {
            let count = 0;
            const currentDate = startDate;

            while (currentDate <= endDate) {
                if (!isWeekend(currentDate) && !isPublicHoliday(currentDate)) {
                    count++;
                }
                currentDate.setDate(currentDate.getDate() + 1);
            }
            return count;
        }
    }

    // Count leave days that will be consumed based on dates selected
    const startDateInput = document.getElementById("startDate");
    const endDateInput = document.getElementById("endDate");

    startDateInput.addEventListener("change", function (event) {
        calculateDays(event.target);
    });
    endDateInput.addEventListener("change", function (event) {
        calculateDays(event.target);
    });

    function calculateDays() {
        const startDate = new Date(startDateInput.value);
        const endDate = new Date(endDateInput.value);
        if (startDate && endDate && endDate >= startDate) {
            const days = countDays(startDate, endDate);
            document.getElementById("numOfLeaveDaysRequired").innerText = days;
        }
        else{
            document.getElementById("numOfLeaveDaysRequired").innerText = '';
        }
    }
}

// Check and display current leave balance
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


