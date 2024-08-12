document.addEventListener('DOMContentLoaded', () => {
    const updateForm = document.querySelector('form#updateForm');
    const inputUserPassword = document.querySelector('input#userPassword');
    const inputUserPasswordConfirm = document.querySelector('input#userPasswordConfirm');
    const inputUserPhone = document.querySelector('input#userPhone');

    // 서버에서 반환된 에러 메시지 표시
    const errorMessage = document.querySelector('.alert-danger');
    if (errorMessage) {
        alert(errorMessage.textContent);
    }

    // 서버에서 반환된 성공 메시지 표시
    const successMessage = document.querySelector('.alert-success');
    if (successMessage) {
        alert(successMessage.textContent);
    }

    function showError(input, message) {
        // 기존 에러 메시지 제거
        const existingError = input.nextElementSibling;
        if (existingError && existingError.classList.contains('error-message')) {
            existingError.remove();
        }
        
        // 새 에러 메시지 추가
        const errorDiv = document.createElement('div');
        errorDiv.className = 'error-message';
        errorDiv.style.color = 'red';
        errorDiv.textContent = message;
        input.parentNode.insertBefore(errorDiv, input.nextSibling);
    }

    function clearError(input) {
        const errorDiv = input.nextElementSibling;
        if (errorDiv && errorDiv.classList.contains('error-message')) {
            errorDiv.remove();
        }
    }

    inputUserPassword.addEventListener('input', function() {
        const passwordPattern = /^(?=.*[A-Za-z])(?=.*\d).{8,}$/;
        if (!this.value.match(passwordPattern)) {
            showError(this, '비밀번호는 8자리 이상이며, 영문과 숫자를 포함해야 합니다.');
        } else {
            clearError(this);
        }
    });

    inputUserPasswordConfirm.addEventListener('input', function() {
        if (this.value !== inputUserPassword.value) {
            showError(this, '비밀번호가 일치하지 않습니다.');
        } else {
            clearError(this);
        }
    });

    inputUserPhone.addEventListener('input', function() {
        const phonePattern = /^01[0-9]-\d{3,4}-\d{4}$/;
        if (!this.value.match(phonePattern) || this.value.replace(/-/g, '').length > 11) {
            showError(this, '전화번호 형식이 올바르지 않습니다. 예: 010-1234-5678');
        } else {
            clearError(this);
        }
    });

    // 폼 제출 시 검증
    updateForm.addEventListener('submit', (event) => {
        event.preventDefault(); // 폼의 기본 제출 동작을 막음

        let isValid = true;

        // 비밀번호 검증
        if (!inputUserPassword.value.match(/^(?=.*[A-Za-z])(?=.*\d).{8,}$/)) {
            showError(inputUserPassword, '비밀번호는 8자리 이상이며, 영문과 숫자를 포함해야 합니다.');
            isValid = false;
        }

        // 비밀번호 확인 검증
        if (inputUserPassword.value !== inputUserPasswordConfirm.value) {
            showError(inputUserPasswordConfirm, '비밀번호가 일치하지 않습니다.');
            isValid = false;
        }

        // 전화번호 검증
        if (!inputUserPhone.value.match(/^01[0-9]-\d{3,4}-\d{4}$/)) {
            showError(inputUserPhone, '전화번호 형식이 올바르지 않습니다. 예: 010-1234-5678');
            isValid = false;
        }

        if (isValid) {
            // 업데이트 내용 저장 확인
            const result = confirm('입력하신 내용으로 저장할까요?');
            if (result) {
                const formData = new FormData(updateForm);

                fetch(updateForm.action, {
                    method: 'POST',
                    body: formData
                })
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        alert(data.message);
                        if (data.redirectUrl) {
                            window.location.href = data.redirectUrl;
                        } else {
                            window.location.reload();
                        }
                    } else {
                        alert('정보 수정에 실패했습니다: ' + data.message);
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                    alert('정보 수정 중 오류가 발생했습니다.');
                });
            }
        }
    });
});