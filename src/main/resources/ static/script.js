// src/main/resources/static/js/script.js

document.addEventListener('DOMContentLoaded', function() {

    // Auto dismiss alerts after 5 seconds
    const alerts = document.querySelectorAll('.alert:not(.alert-permanent)');

    alerts.forEach(function(alert) {

        setTimeout(function() {

            alert.classList.add('fade');

            setTimeout(function() {
                alert.remove();
            }, 500);

        }, 5000);

    });


    const startDateInput = document.getElementById("startDate");
    const endDateInput = document.getElementById("endDate");


    // Check if booking form exists
    if (startDateInput && endDateInput) {


        // Get today's date
        const today = new Date().toISOString().split("T")[0];


        // Start date cannot be before today
        startDateInput.min = today;


        // End date cannot be before today
        endDateInput.min = today;



        // When start date changes
        startDateInput.addEventListener("change", function() {


            // End date must be same or after start date
            endDateInput.min = this.value;



            // Clear invalid end date
            if (endDateInput.value < this.value) {

                endDateInput.value = "";

            }


        });


    }


});

function confirmDelete(message) {

    return confirm(message || 'Are you sure you want to delete this item?');

}

function formatCurrency(amount) {

    return '$' + parseFloat(amount).toFixed(2);

}