const sidebar = document.getElementById("sidebar");
const toggleBtn = document.getElementById("toggle-btn");
const logoutForm = document.getElementById("logout-form");

if (toggleBtn) {
    document.addEventListener("DOMContentLoaded", function () {
        toggleBtn.addEventListener("click", function () {
            sidebar.classList.toggle("collapsed");
        });
    });
}

function logout() {
    logoutForm.submit();
}