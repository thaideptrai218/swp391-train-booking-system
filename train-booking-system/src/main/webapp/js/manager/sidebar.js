document.addEventListener("DOMContentLoaded", function () {
  const sidebarToggle = document.getElementById("sidebarToggle");
  const sidebar = document.getElementById("sidebar");
  // Get the main content area to shift it
  const mainContent = document.querySelector('.main-content'); // Make sure this selector matches your HTML

  if (sidebarToggle && sidebar && mainContent) { // Ensure mainContent also exists
    sidebarToggle.addEventListener("click", function () {
      sidebar.classList.toggle("open");
      // Toggle class on main content to shift it
      mainContent.classList.toggle('shifted');
    });
  } else {
    if (!sidebarToggle) console.error("Sidebar toggle button not found.");
    if (!sidebar) console.error("Sidebar element not found.");
    if (!mainContent) console.error("Main content element not found. Check selector '.main-content'.");
  }
});
