document.addEventListener('DOMContentLoaded', function() {
    const menuToggle = document.querySelector('.menu-toggle');
    const sidebar = document.querySelector('.sidebar');
    const dashboardContainer = document.querySelector('.dashboard-container');

    if (menuToggle && sidebar && dashboardContainer) {
        menuToggle.addEventListener('click', function() {
            sidebar.classList.toggle('collapsed');
            dashboardContainer.classList.toggle('collapsed');
        });
    }

    const deleteButtons = document.querySelectorAll('a.delete-user');
    deleteButtons.forEach(button => {
        button.addEventListener('click', function(event) {
            event.preventDefault();
            const confirmed = confirm('Bạn có chắc chắn muốn xóa người dùng này không?');
            if (confirmed) {
                const url = this.href;
                const form = document.createElement('form');
                form.method = 'post';
                form.action = url;
                document.body.appendChild(form);
                form.submit();
            }
        });
    });
});
