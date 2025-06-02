document.addEventListener('DOMContentLoaded', function() {
    const profileForm = document.getElementById('profileForm');
    const messageDiv = document.getElementById('message');

    if (profileForm) {
        profileForm.addEventListener('submit', function(event) {
            event.preventDefault(); // Prevent default form submission

            // Simulate form data collection
            const formData = new FormData(profileForm);
            const userData = {};
            for (let [key, value] of formData.entries()) {
                userData[key] = value;
            }

            console.log('Profile data submitted:', userData);

            // Simulate an API call or data update
            setTimeout(() => {
                displayMessage('Profile updated successfully!', 'success');
                // In a real application, you would send this data to the server
                // e.g., fetch('/api/updateProfile', { method: 'POST', body: JSON.stringify(userData) })
                // .then(response => response.json())
                // .then(data => {
                //     if (data.success) {
                //         displayMessage('Profile updated successfully!', 'success');
                //     } else {
                //         displayMessage('Failed to update profile: ' + data.error, 'error');
                //     }
                // })
                // .catch(error => {
                //     displayMessage('An error occurred: ' + error.message, 'error');
                // });
            }, 1000);
        });
    }

    function displayMessage(message, type) {
        if (messageDiv) {
            messageDiv.textContent = message;
            messageDiv.className = 'message ' + type; // Add class for styling (e.g., 'success', 'error')
            messageDiv.style.display = 'block';
            setTimeout(() => {
                messageDiv.style.display = 'none';
            }, 3000); // Hide message after 3 seconds
        }
    }

    // Example of loading existing profile data (if any)
    // This would typically come from a server-side render or an API call
    function loadProfileData() {
        // Simulate fetching data
        const existingData = {
            username: 'john.doe',
            email: 'john.doe@example.com',
            // ... other profile fields
        };

        // Populate form fields
        // if (profileForm.elements['username']) profileForm.elements['username'].value = existingData.username;
        // if (profileForm.elements['email']) profileForm.elements['email'].value = existingData.email;
    }

    loadProfileData(); // Call on page load
});
