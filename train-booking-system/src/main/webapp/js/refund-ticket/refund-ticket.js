 const checkboxes = document.querySelectorAll('input[name="ticketInfo"]');
    const continueBtn = document.getElementById('continueBtn');

    checkboxes.forEach(cb => {
        cb.addEventListener('change', () => {
            const anyChecked = Array.from(checkboxes).some(c => c.checked);
            continueBtn.disabled = !anyChecked;
            continueBtn.style.cursor = anyChecked ? 'pointer' : 'not-allowed';
        });
    });