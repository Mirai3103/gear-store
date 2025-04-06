document.addEventListener('DOMContentLoaded', () => {
    const slides = document.querySelectorAll('#customSlider .slider-slide');
    let currentSlide = 0;
    const delay = 3000; // 3 seconds

    if (slides.length > 1) {
        setInterval(() => {
            slides[currentSlide].classList.remove('active');
            currentSlide = (currentSlide + 1) % slides.length;
            slides[currentSlide].classList.add('active');
        }, delay);
    }
});