document.querySelectorAll('[data-modal-target]').forEach(card => {
    card.addEventListener('click', function (event) {
        event.preventDefault();
        let modalId = this.getAttribute('data-modal-target'); // Get the modal ID from data attribute
        let modal = document.getElementById(modalId);
        if (modal) {
            modal.classList.remove('hidden');
        }
    });
});

// Close modals when clicking the close button
document.querySelectorAll('.modal-close-btn').forEach(button => {
    button.addEventListener('click', function () {
        this.closest('.modal-container').classList.add('hidden'); // Hides the closest modal
    });
});
