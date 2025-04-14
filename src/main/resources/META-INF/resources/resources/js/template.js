const sidebar = document.getElementsByClassName("sidebar")[0];
const toggleBtn = document.getElementById("toggle-btn");
const logoutForm = document.getElementById("logout-form");

if (toggleBtn && sidebar) {
    document.addEventListener("DOMContentLoaded", function () {
        toggleBtn.addEventListener("click", function () {
            sidebar.classList.toggle("collapsed");
        });
    });
}

function logout() {
    logoutForm.submit();
}