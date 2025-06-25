document.addEventListener("DOMContentLoaded", function () {
  const sidebarToggle = document.getElementById("sidebarToggle");
  const sidebar = document.getElementById("sidebar");
  const mainContent = document.querySelector(".main-content"); // Make sure this selector matches your HTML
  const body = document.body;

  // Function to apply sidebar state
  function applySidebarState(isOpen) {
    if (isOpen) {
      sidebar.classList.add("open");
      if (mainContent) mainContent.classList.add("shifted");
      body.classList.add("sidebar-is-open");
    } else {
      sidebar.classList.remove("open");
      if (mainContent) mainContent.classList.remove("shifted");
      body.classList.remove("sidebar-is-open");
    }
  }

  // Check localStorage for saved state
  // Default to 'false' (closed) if no item is found or if it's not 'true'
  let isSidebarOpen = localStorage.getItem("sidebarOpen") === "true";
  applySidebarState(isSidebarOpen);

  if (sidebarToggle && sidebar) {
    sidebarToggle.addEventListener("click", function () {
      isSidebarOpen = !isSidebarOpen; // Toggle the state
      applySidebarState(isSidebarOpen);
      localStorage.setItem("sidebarOpen", isSidebarOpen);
    });
  } else {
    if (!sidebarToggle) console.error("Sidebar toggle button not found.");
    if (!sidebar) console.error("Sidebar element not found.");
    // mainContent check is implicitly handled by applySidebarState if it exists
  }
});
