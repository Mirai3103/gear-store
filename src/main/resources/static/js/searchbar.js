document.addEventListener('DOMContentLoaded', function() {
    const searchBar = document.getElementById('searchbar');
    const searchBtn = document.getElementById('search-btn');
    const overlay = document.getElementById('searchOverlay');

    if (searchBar && searchBtn && overlay) {
        const toggleSearch = () => {
            // Toggle search bar
            searchBar.classList.toggle('translate-y-0');
            searchBar.classList.toggle('-translate-y-full');
            
            // Toggle overlay
            overlay.classList.toggle('opacity-0');
            overlay.classList.toggle('invisible');
            overlay.classList.toggle('opacity-100');
            overlay.classList.toggle('visible');
        };

        searchBtn.addEventListener('click', (e) => {
            e.preventDefault();
            toggleSearch();
        });

        // Close when clicking overlay
        overlay.addEventListener('click', (e) => {
            if (e.target === overlay) {
                toggleSearch();
            }
        });

        // Close with ESC key
        document.addEventListener('keydown', (e) => {
            if (e.key === 'Escape') {
                searchBar.classList.add('-translate-y-full');
                searchBar.classList.remove('translate-y-0');
                overlay.classList.add('opacity-0', 'invisible');
                overlay.classList.remove('opacity-100', 'visible');
            }
        });
    }
});