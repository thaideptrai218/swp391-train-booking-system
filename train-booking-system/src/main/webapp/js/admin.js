document.addEventListener("DOMContentLoaded", function () {
  const sidebarToggle = document.getElementById("sidebarToggle");
  const sidebar = document.getElementById("sidebar");
  const mainContent = document.querySelector(".main-content");
  const body = document.body;

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

  let isSidebarOpen = localStorage.getItem("sidebarOpen") === "true";
  applySidebarState(isSidebarOpen);

  if (sidebarToggle && sidebar) {
    sidebarToggle.addEventListener("click", function () {
      isSidebarOpen = !isSidebarOpen;
      applySidebarState(isSidebarOpen);
      localStorage.setItem("sidebarOpen", isSidebarOpen);
    });
  } else {
    if (!sidebarToggle) console.error("Sidebar toggle button not found.");
    if (!sidebar) console.error("Sidebar element not found.");
  }
});
