// This file can be used for JavaScript animations or interactive elements on the admin dashboard.
// For example, you could add logic here to dynamically load data into charts,
// or implement more complex UI interactions.

document.addEventListener('DOMContentLoaded', () => {
    console.log('Admin Dashboard JavaScript loaded.');
    // Example: Add a simple animation on load
    const cards = document.querySelectorAll('.card');
    cards.forEach((card, index) => {
        card.style.opacity = 0;
        card.style.transform = 'translateY(20px)';
        setTimeout(() => {
            card.style.transition = 'opacity 0.6s ease-out, transform 0.6s ease-out';
            card.style.opacity = 1;
            card.style.transform = 'translateY(0)';
        }, index * 100 + 500); // Staggered animation
    });

    const chartsContainer = document.querySelector('.charts-container');
    if (chartsContainer) {
        chartsContainer.style.opacity = 0;
        chartsContainer.style.transform = 'translateY(20px)';
        setTimeout(() => {
            chartsContainer.style.transition = 'opacity 0.8s ease-out, transform 0.8s ease-out';
            chartsContainer.style.opacity = 1;
            chartsContainer.style.transform = 'translateY(0)';
        }, 1000); // After cards
    }
});
