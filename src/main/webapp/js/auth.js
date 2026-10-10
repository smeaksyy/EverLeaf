document.addEventListener('DOMContentLoaded', () => {
    // Password visibility toggle
    const toggleButtons = document.querySelectorAll('.toggle-password');
    toggleButtons.forEach(btn => {
        btn.addEventListener('click', function() {
            const input = this.previousElementSibling;
            if (input.type === 'password') {
                input.type = 'text';
                this.innerHTML = '<svg class="eye-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"></path><line x1="1" y1="1" x2="23" y2="23"></line></svg>';
            } else {
                input.type = 'password';
                this.innerHTML = '<svg class="eye-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path><circle cx="12" cy="12" r="3"></circle></svg>';
            }
        });
    });

    // Check URL parameters for messages
    const urlParams = new URLSearchParams(window.location.search);
    const errorMsg = urlParams.get('error');
    const successMsg = urlParams.get('success');

    const errorDiv = document.getElementById('error-message');
    if (errorMsg && errorDiv) {
        errorDiv.textContent = errorMsg;
        errorDiv.style.display = 'block';
    }

    const successDiv = document.getElementById('success-message');
    if (successMsg && successDiv) {
        successDiv.textContent = successMsg;
        successDiv.style.display = 'block';
    }

    // Password strength for register
    const passwordInput = document.getElementById('password');
    const registerForm = document.getElementById('register-form');
    
    if (passwordInput && registerForm) {
        const strengthBar = document.querySelector('.strength-bar');
        const strengthText = document.querySelector('.strength-text');
        const confirmPassword = document.getElementById('confirm_password');
        const matchMsg = document.getElementById('password-match-msg');

        passwordInput.addEventListener('input', () => {
            const val = passwordInput.value;
            let strength = 0;
            
            if (val.length >= 8) strength += 1;
            if (val.match(/[a-z]/) && val.match(/[A-Z]/)) strength += 1;
            if (val.match(/[0-9]/) || val.match(/[^a-zA-Z0-9]/)) strength += 1;

            strengthBar.className = 'strength-bar';
            if (val.length === 0) {
                strengthText.textContent = '';
            } else if (strength === 1) {
                strengthBar.classList.add('strength-weak');
                strengthText.textContent = 'Weak';
            } else if (strength === 2) {
                strengthBar.classList.add('strength-medium');
                strengthText.textContent = 'Medium';
            } else if (strength === 3) {
                strengthBar.classList.add('strength-strong');
                strengthText.textContent = 'Strong';
            }
            
            checkPasswordsMatch();
        });

        confirmPassword.addEventListener('input', checkPasswordsMatch);

        function checkPasswordsMatch() {
            const p1 = passwordInput.value;
            const p2 = confirmPassword.value;
            if (p2.length > 0) {
                if (p1 !== p2) {
                    matchMsg.textContent = 'Passwords do not match';
                    confirmPassword.setCustomValidity('Passwords do not match');
                    return false;
                } else {
                    matchMsg.textContent = '';
                    confirmPassword.setCustomValidity('');
                    return true;
                }
            } else {
                matchMsg.textContent = '';
                confirmPassword.setCustomValidity('');
                return false;
            }
        }
    }

    // Fetch and populate CSRF token
    function fetchCsrfToken() {
        return fetch('/everleaf/csrf')
            .then(res => {
                if (res.ok) return res.json();
                throw new Error('CSRF fetch failed');
            })
            .then(data => {
                if (data.token) {
                    document.querySelectorAll('input[name="csrf_token"], input[name="csrfToken"]').forEach(el => {
                        el.value = data.token;
                    });
                    return data.token;
                }
            })
            .catch(err => {
                console.warn('CSRF warning:', err);
                return null;
            });
    }

    fetchCsrfToken();

    // Ensure CSRF token is present on submit
    const loginForm = document.getElementById('login-form');
    if (loginForm) {
        loginForm.addEventListener('submit', async (e) => {
            const csrfInput = document.getElementById('csrf_token');
            if (!csrfInput || !csrfInput.value) {
                e.preventDefault();
                const token = await fetchCsrfToken();
                if (token && csrfInput) {
                    csrfInput.value = token;
                    loginForm.submit();
                } else {
                    alert('Unable to secure session. Please reload the page.');
                }
            }
        });
    }

    if (registerForm) {
        registerForm.addEventListener('submit', async (e) => {
            const p1 = document.getElementById('password')?.value;
            const p2 = document.getElementById('confirm_password')?.value;
            const matchMsg = document.getElementById('password-match-msg');
            if (p1 !== p2) {
                e.preventDefault();
                if (matchMsg) matchMsg.textContent = 'Passwords do not match';
                return;
            }

            const csrfInput = document.getElementById('csrf_token');
            if (!csrfInput || !csrfInput.value) {
                e.preventDefault();
                const token = await fetchCsrfToken();
                if (token && csrfInput) {
                    csrfInput.value = token;
                    registerForm.submit();
                }
            }
        });
    }
});
