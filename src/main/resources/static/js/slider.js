document.addEventListener('DOMContentLoaded', () => {
    const sliderWrapper = document.querySelector('.sliding-slider-wrapper');
    const slides = document.querySelectorAll('.sliding-slider-slide');
    const totalSlides = slides.length;
    let currentIndex = 0;
  
    // Clone the first slide and append it to the end for a seamless transition
    const firstClone = slides[0].cloneNode(true);
    sliderWrapper.appendChild(firstClone);
  
    function changeSlide() {
        currentIndex++;
  
        sliderWrapper.style.transition = 'transform 0.7s ease-in-out';
        sliderWrapper.style.transform = `translateX(-${currentIndex * 100}%)`;
  
        // When reaching the cloned slide, reset position instantly
        if (currentIndex === totalSlides) {
            setTimeout(() => {
                sliderWrapper.style.transition = 'none';
                sliderWrapper.style.transform = `translateX(0)`;
                currentIndex = 0;
            }, 700); // Delay must match transition duration
          }
      }
  
      setInterval(changeSlide, 4000); // Auto-slide every 4 seconds
});