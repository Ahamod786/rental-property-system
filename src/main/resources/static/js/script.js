document.addEventListener("DOMContentLoaded", () => {
    const start = document.querySelector("#startDate");
    const end = document.querySelector("#endDate");
    if (!start || !end) {
        return;
    }

    const today = new Date().toISOString().split("T")[0];
    start.min = today;
    end.min = today;
    start.addEventListener("change", () => {
        end.min = start.value || today;
        if (end.value && end.value <= start.value) {
            end.value = "";
        }
    });
});
