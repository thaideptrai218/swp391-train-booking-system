// This script assumes a global variable 'contextPath' is defined in the JSP
// e.g., <script>var contextPath = '${pageContext.request.contextPath}';</script>

document.addEventListener('DOMContentLoaded', function() {
  const staffForm = document.getElementById('staffForm');
  if (staffForm) {
    staffForm.addEventListener('submit', function(event) {
      const phoneNumberInput = document.getElementById('phoneNumber');
      const idCardNumberInput = document.getElementById('idCardNumber');
      const action = document.getElementById('formAction').value;

      // Validate Phone Number: must be 10 digits
      if (phoneNumberInput.value && !/^\d{10}$/.test(phoneNumberInput.value)) {
        alert('Phone Number must be exactly 10 digits.');
        phoneNumberInput.focus();
        event.preventDefault(); // Prevent form submission
        return;
      }

      // Validate ID Card Number: must be 12 digits (only if a value is entered)
      // ID card might be optional, so only validate if not empty.
      // If it's mandatory, the 'required' attribute should be on the input in JSP.
      // The current JSP does not have 'required' for idCardNumber.
      if (idCardNumberInput.value && !/^\d{12}$/.test(idCardNumberInput.value)) {
        alert('ID Card Number must be exactly 12 digits.');
        idCardNumberInput.focus();
        event.preventDefault(); // Prevent form submission
        return;
      }
    });
  }

  // Optional: Handle sidebar collapse/expand to adjust content margin
  // This depends on how your sidebar.js handles collapse events.
  // Example: if sidebar.js adds a 'collapsed' class to the sidebar
  const sidebar = document.querySelector(".sidebar");
  const contentContainer = document.querySelector(".content-container");

  if (sidebar && contentContainer) {
    const observer = new MutationObserver(function (mutations) {
      mutations.forEach(function (mutation) {
        if (mutation.attributeName === "class") {
          if (sidebar.classList.contains("collapsed")) {
            contentContainer.style.marginLeft = "60px"; // Collapsed width
            contentContainer.style.width = "calc(100% - 60px)";
          } else {
            contentContainer.style.marginLeft = "250px"; // Expanded width
            contentContainer.style.width = "calc(100% - 250px)";
          }
        }
      });
    });
    observer.observe(sidebar, { attributes: true });

    // Initial check
    if (sidebar.classList.contains("collapsed")) {
      contentContainer.style.marginLeft = "60px";
      contentContainer.style.width = "calc(100% - 60px)";
    }
  }
});

function openAddModal() {
  $("#staffModalLabel").text("Add New Staff");
  $("#formAction").val("add");
  $("#userID").val("");
  $("#staffForm")[0].reset(); // Reset form fields
  $("#passwordFieldContainer").show(); // Show password field for add
  $("#password").attr("placeholder", "Enter password for new staff").prop("required", true); // Make password required for add
  $("#isActive").prop("checked", true); // Default to active
  $("#role").val("Staff"); // Default role
  $("#staffModal").modal("show");
}

function openEditModal(userID) {
  $("#staffModalLabel").text("Edit Staff");
  $("#formAction").val("edit");
  $("#userID").val(userID);
  $("#passwordFieldContainer").hide(); // Hide password field for edit
  $("#password").removeAttr("required"); // Remove required attribute for edit

  // Fetch staff data using AJAX
  const staffDataUrl =
    contextPath + "/manageStaffs?action=getStaff&userID=" + userID;
  fetch(staffDataUrl)
    .then((response) => {
      if (!response.ok) {
        // If response is not OK, try to get text error message
        return response.text().then(text => {
          throw new Error(
            "Network response was not ok. Status: " + response.status + ". Message: " + text
          );
        });
      }
      // Check content type before parsing as JSON
      const contentType = response.headers.get("content-type");
      if (contentType && contentType.indexOf("application/json") !== -1) {
        return response.json();
      } else {
        return response.text().then(text => {
          throw new Error("Expected JSON, but got " + contentType + ". Response: " + text);
        });
      }
    })
    .then((data) => {
      $("#fullName").val(data.fullName);
      $("#email").val(data.email);
      $("#phoneNumber").val(data.phoneNumber);
      $("#idCardNumber").val(data.idCardNumber);
      $("#role").val(data.role);
      $("#isActive").prop("checked", data.active);
      // Password field is hidden for edit, so no need to set its value or placeholder here.
      // $("#password").val(""); // Clear password field - not necessary as it's hidden
      $("#staffModal").modal("show");
    })
    .catch((error) => {
      console.error("Error fetching staff data:", error);
      alert(
        "Error fetching staff data: " + error.message
      );
    });
}

function deleteStaff(userID) {
  if (
    confirm(
      "Are you sure you want to delete this staff user? This action cannot be undone."
    )
  ) {
    const form = document.createElement("form");
    form.method = "post";
    form.action = contextPath + "/manageStaffs";

    const actionInput = document.createElement("input");
    actionInput.type = "hidden";
    actionInput.name = "action";
    actionInput.value = "delete";
    form.appendChild(actionInput);

    const userIDInput = document.createElement("input");
    userIDInput.type = "hidden";
    userIDInput.name = "userID";
    userIDInput.value = userID;
    form.appendChild(userIDInput);

    document.body.appendChild(form);
    form.submit();
  }
}
