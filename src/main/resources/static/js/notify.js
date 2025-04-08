function showNotification() {
    let notification = document.getElementById("cart-notification");
    notification.classList.remove("hidden");
    notification.classList.add("!opacity-100");

    // Auto-hide after 3 seconds
    setTimeout(() => {
        hideNotification();
    }, 3000);
}

function hideNotification() {
    let notification = document.getElementById("cart-notification");
    notification.classList.add("hidden");
    notification.classList.remove("!opacity-100");
}
