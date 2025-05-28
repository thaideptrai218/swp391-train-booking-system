

class ChangePasswordForm {
  constructor() {
    this.form = document.getElementById('changePasswordForm');
    this.loadingOverlay = document.getElementById('loadingOverlay');
    this.successMessage = document.getElementById('successMessage');
    
    this.fields = {
      oldPassword: document.getElementById('oldPassword'),
      newPassword: document.getElementById('newPassword'),
      confirmPassword: document.getElementById('confirmPassword')
    };
    
    this.errorElements = {
      oldPassword: document.getElementById('oldPasswordError'),
      newPassword: document.getElementById('newPasswordError'),
      confirmPassword: document.getElementById('confirmPasswordError')
    };
    
    this.submitButton = this.form.querySelector('.submit-button');
    this.homeButton = document.querySelector('.home-button');
    
    this.init();
  }
  
  /**
   * Initialize event listeners and form setup
   */
  init() {
    this.bindEvents();
    this.setupAccessibility();
  }
  
  /**
   * Bind all event listeners
   */
  bindEvents() {
    // Form submission
    this.form.addEventListener('submit', this.handleSubmit.bind(this));
    
    // Real-time validation
    Object.keys(this.fields).forEach(fieldName => {
      const field = this.fields[fieldName];
      field.addEventListener('blur', () => this.validateField(fieldName));
      field.addEventListener('input', () => this.clearFieldError(fieldName));
    });
    
    // Home button navigation
    this.homeButton.addEventListener('click', this.handleHomeNavigation.bind(this));
    
    // Keyboard navigation
    document.addEventListener('keydown', this.handleKeyboardNavigation.bind(this));
  }
  
  /**
   * Setup accessibility features
   */
  setupAccessibility() {
    // Add ARIA labels and descriptions
    Object.keys(this.fields).forEach(fieldName => {
      const field = this.fields[fieldName];
      const errorElement = this.errorElements[fieldName];
      
      field.setAttribute('aria-describedby', errorElement.id);
      field.setAttribute('aria-invalid', 'false');
    });
  }
  
  /**
   * Handle form submission
   * @param {Event} event - Form submit event
   */
  async handleSubmit(event) {
    event.preventDefault();
    
    if (!this.validateForm()) {
      return;
    }
    
    try {
      this.showLoading();
      await this.submitPasswordChange();
      this.showSuccess();
    } catch (error) {
      this.handleSubmissionError(error);
    } finally {
      this.hideLoading();
    }
  }
  
  /**
   * Validate entire form
   * @returns {boolean} - True if form is valid
   */
  validateForm() {
    let isValid = true;
    
    // Validate each field
    Object.keys(this.fields).forEach(fieldName => {
      if (!this.validateField(fieldName)) {
        isValid = false;
      }
    });
    
    return isValid;
  }
  
  /**
   * Validate individual field
   * @param {string} fieldName - Name of field to validate
   * @returns {boolean} - True if field is valid
   */
  validateField(fieldName) {
    const field = this.fields[fieldName];
    const value = field.value.trim();
    let errorMessage = '';
    
    switch (fieldName) {
      case 'oldPassword':
        errorMessage = this.validateOldPassword(value);
        break;
      case 'newPassword':
        errorMessage = this.validateNewPassword(value);
        break;
      case 'confirmPassword':
        errorMessage = this.validateConfirmPassword(value);
        break;
    }
    
    this.setFieldError(fieldName, errorMessage);
    return !errorMessage;
  }
  
  /**
   * Validate old password
   * @param {string} value - Password value
   * @returns {string} - Error message or empty string
   */
  validateOldPassword(value) {
    if (!value) {
      return 'Vui lòng nhập mật khẩu cũ';
    }
    if (value.length < 6) {
      return 'Mật khẩu phải có ít nhất 6 ký tự';
    }
    return '';
  }
  
  /**
   * Validate new password
   * @param {string} value - Password value
   * @returns {string} - Error message or empty string
   */
  validateNewPassword(value) {
    if (!value) {
      return 'Vui lòng nhập mật khẩu mới';
    }
    if (value.length < 8) {
      return 'Mật khẩu mới phải có ít nhất 8 ký tự';
    }
    if (!/(?=.*[a-z])(?=.*[A-Z])(?=.*\d)/.test(value)) {
      return 'Mật khẩu phải chứa ít nhất 1 chữ hoa, 1 chữ thường và 1 số';
    }
    if (value === this.fields.oldPassword.value.trim()) {
      return 'Mật khẩu mới phải khác mật khẩu cũ';
    }
    return '';
  }
  
  /**
   * Validate confirm password
   * @param {string} value - Password value
   * @returns {string} - Error message or empty string
   */
  validateConfirmPassword(value) {
    if (!value) {
      return 'Vui lòng xác nhận mật khẩu mới';
    }
    if (value !== this.fields.newPassword.value.trim()) {
      return 'Mật khẩu xác nhận không khớp';
    }
    return '';
  }
  
  /**
   * Set error message for field
   * @param {string} fieldName - Field name
   * @param {string} message - Error message
   */
  setFieldError(fieldName, message) {
    const field = this.fields[fieldName];
    const errorElement = this.errorElements[fieldName];
    
    errorElement.textContent = message;
    
    if (message) {
      field.classList.add('error');
      field.setAttribute('aria-invalid', 'true');
    } else {
      field.classList.remove('error');
      field.setAttribute('aria-invalid', 'false');
    }
  }
  
  /**
   * Clear error for field
   * @param {string} fieldName - Field name
   */
  clearFieldError(fieldName) {
    const field = this.fields[fieldName];
    const errorElement = this.errorElements[fieldName];
    
    if (field.classList.contains('error')) {
      errorElement.textContent = '';
      field.classList.remove('error');
      field.setAttribute('aria-invalid', 'false');
    }
  }
  
  /**
   * Submit password change to server
   * @returns {Promise} - Promise that resolves when submission is complete
   */
  async submitPasswordChange() {
    const formData = {
      oldPassword: this.fields.oldPassword.value.trim(),
      newPassword: this.fields.newPassword.value.trim(),
      confirmPassword: this.fields.confirmPassword.value.trim()
    };
    
    // Simulate API call - replace with actual endpoint
    return new Promise((resolve, reject) => {
      setTimeout(() => {
        // Simulate random success/failure for demo
        if (Math.random() > 0.2) {
          resolve({ success: true, message: 'Mật khẩu đã được thay đổi thành công' });
        } else {
          reject(new Error('Mật khẩu cũ không chính xác'));
        }
      }, 2000);
    });
    
    // Actual implementation would be:
    /*
    const response = await fetch('/api/change-password', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'X-Requested-With': 'XMLHttpRequest'
      },
      body: JSON.stringify(formData)
    });
    
    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.message || 'Có lỗi xảy ra khi thay đổi mật khẩu');
    }
    
    return await response.json();
    */
  }
  
  /**
   * Handle submission error
   * @param {Error} error - Error object
   */
  handleSubmissionError(error) {
    console.error('Password change error:', error);
    
    // Check if it's a specific field error
    if (error.message.includes('mật khẩu cũ')) {
      this.setFieldError('oldPassword', error.message);
    } else {
      // Show general error
      alert('Có lỗi xảy ra: ' + error.message);
    }
  }
  
  /**
   * Show loading state
   */
  showLoading() {
    this.loadingOverlay.style.display = 'flex';
    this.submitButton.disabled = true;
    
    // Disable all form fields
    Object.values(this.fields).forEach(field => {
      field.disabled = true;
    });
  }
  
  /**
   * Hide loading state
   */
  hideLoading() {
    this.loadingOverlay.style.display = 'none';
    this.submitButton.disabled = false;
    
    // Re-enable all form fields
    Object.values(this.fields).forEach(field => {
      field.disabled = false;
    });
  }
  
  /**
   * Show success message
   */
  showSuccess() {
    this.successMessage.style.display = 'block';
    
    // Clear form
    this.form.reset();
    
    // Clear any existing errors
    Object.keys(this.fields).forEach(fieldName => {
      this.clearFieldError(fieldName);
    });
    
    // Hide success message after 5 seconds
    setTimeout(() => {
      this.successMessage.style.display = 'none';
    }, 5000);
    
    // Focus on first field
    this.fields.oldPassword.focus();
  }
  
  /**
   * Handle home button navigation
   */
  handleHomeNavigation() {
    if (confirm('Bạn có muốn quay về trang chủ? Các thay đổi chưa lưu sẽ bị mất.')) {
      // Replace with actual home page URL
      window.location.href = '/home';
    }
  }
  
  /**
   * Handle keyboard navigation
   * @param {KeyboardEvent} event - Keyboard event
   */
  handleKeyboardNavigation(event) {
    // ESC key to close success message
    if (event.key === 'Escape') {
      if (this.successMessage.style.display === 'block') {
        this.successMessage.style.display = 'none';
      }
    }
    
    // Enter key on home button
    if (event.key === 'Enter' && event.target === this.homeButton) {
      this.handleHomeNavigation();
    }
  }
}

/**
 * Utility functions
 */
const Utils = {
  /**
   * Debounce function to limit function calls
   * @param {Function} func - Function to debounce
   * @param {number} wait - Wait time in milliseconds
   * @returns {Function} - Debounced function
   */
  debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
      const later = () => {
        clearTimeout(timeout);
        func(...args);
      };
      clearTimeout(timeout);
      timeout = setTimeout(later, wait);
    };
  },
  
  /**
   * Check if password meets security requirements
   * @param {string} password - Password to check
   * @returns {Object} - Security check results
   */
  checkPasswordSecurity(password) {
    return {
      hasMinLength: password.length >= 8,
      hasUpperCase: /[A-Z]/.test(password),
      hasLowerCase: /[a-z]/.test(password),
      hasNumber: /\d/.test(password),
      hasSpecialChar: /[!@#$%^&*(),.?":{}|<>]/.test(password)
    };
  },
  
  /**
   * Generate password strength score
   * @param {string} password - Password to score
   * @returns {number} - Strength score (0-5)
   */
  getPasswordStrength(password) {
    const checks = this.checkPasswordSecurity(password);
    return Object.values(checks).filter(Boolean).length;
  }
};

/**
 * Initialize application when DOM is loaded
 */
document.addEventListener('DOMContentLoaded', () => {
  // Initialize the change password form
  const changePasswordForm = new ChangePasswordForm();
  
  // Add any additional initialization here
  console.log('Change Password form initialized');
  
  // Set focus to first field
  if (changePasswordForm.fields.oldPassword) {
    changePasswordForm.fields.oldPassword.focus();
  }
});

/**
 * Handle page visibility changes
 */
document.addEventListener('visibilitychange', () => {
  if (document.hidden) {
    // Page is hidden - could pause any timers or animations
    console.log('Page hidden');
  } else {
    // Page is visible - could resume any paused operations
    console.log('Page visible');
  }
});

/**
 * Handle page unload
 */
window.addEventListener('beforeunload', (event) => {
  // Check if form has unsaved changes
  const form = document.getElementById('changePasswordForm');
  if (form) {
    const hasChanges = Array.from(form.elements).some(element => {
      return element.type !== 'submit' && element.value.trim() !== '';
    });
    
    if (hasChanges) {
      event.preventDefault();
      event.returnValue = 'Bạn có thay đổi chưa được lưu. Bạn có chắc muốn rời khỏi trang?';
      return event.returnValue;
    }
  }
});

// Export for potential module usage
if (typeof module !== 'undefined' && module.exports) {
  module.exports = { ChangePasswordForm, Utils };
}
