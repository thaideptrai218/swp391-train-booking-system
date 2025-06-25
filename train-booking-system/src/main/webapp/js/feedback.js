document.addEventListener('DOMContentLoaded', function() {
    const feedbackForm = document.getElementById('feedbackForm');
    const feedbackMessage = document.getElementById('feedbackMessage');

    if (feedbackForm) {
        feedbackForm.addEventListener('submit', function(event) {
            event.preventDefault(); // Prevent default form submission

            const fullName = feedbackForm.querySelector('input[name="fullName"]').value;
            const email = feedbackForm.querySelector('input[name="email"]').value;
            const ticketName = feedbackForm.querySelector('input[name="ticketName"]').value;
            const ticketType = feedbackForm.querySelector('select[name="ticketType"]').value;
            const description = feedbackForm.querySelector('textarea[name="description"]').value;
            const feedbackContent = feedbackForm.querySelector('textarea[name="feedbackContent"]').value;

            // Basic validation
            if (!fullName || !email || !ticketName || !ticketType || !description || !feedbackContent) {
                alert('Please fill in all required fields.');
                return;
            }

            // In a real application, you would send this data to a server
            // using fetch or XMLHttpRequest. For this example, we'll just
            // simulate a successful submission.
            console.log('Feedback Submitted:', {
                fullName,
                email,
                ticketName,
                ticketType,
                description,
                feedbackContent
            });

            // Display a success message
            if (feedbackMessage) {
                feedbackMessage.textContent = 'Thank you for your feedback!';
                feedbackMessage.style.color = 'green';
                feedbackMessage.style.display = 'block';
            }

            // Optionally, clear the form
            feedbackForm.reset();

            // Hide the message after a few seconds
            setTimeout(() => {
                if (feedbackMessage) {
                    feedbackMessage.style.display = 'none';
                }
            }, 5000);
        });
    }
});
