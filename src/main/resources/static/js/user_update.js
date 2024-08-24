document.addEventListener('DOMContentLoaded', () => {
    const updateForm = document.querySelector('form#updateForm');
    const inputUserId = document.querySelector('input#userId');
    const inputUserPassword = document.querySelector('input#userPassword');
    const inputUserPasswordConfirm = document.querySelector('input#userPasswordConfirm');
    const inputUserPhone = document.querySelector('input#userPhone');
    const inputUserEmail = document.querySelector('input#userEmail');
    const inputUserName = document.querySelector('input#userName');
    const deleteProfileImageButton = document.getElementById('deleteProfileImage');
	
	
	// 서버에서 기존 비밀번호를 가져온다고 가정 (기존 비밀번호를 meta 태그를 통해 전달 받음)
	const oldPassword = document.querySelector('meta[name="oldPassword"]').getAttribute('content');
    
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
        } else if (this.value === oldPassword) {
			showError(this, '새 비밀번호는 기존 비밀번호와 다르게 설정해야 합니다.');
		} else {
            clearError(this);
        }
    });

	inputUserPasswordConfirm.addEventListener('input', function() {
		if (this.value === '') {
			clearError(this); // 공란일 때는 에러 메시지를 제거
		} else if (this.value !== inputUserPassword.value) {
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

        if (validateForm()) {
            // 업데이트 내용 저장 확인
            const result = confirm('입력하신 내용으로 저장할까요?');
			if (result) {
				const formData = new FormData(updateForm);

                // action 값 설정 (기본값은 'updateProfile')
                if (!formData.get('action')) {
                    formData.set('action', 'updateProfile');
                }

                console.log('Sending data:', Object.fromEntries(formData)); // 디버깅용
				
				
				const serverUrl = '/enumcamping/mypage/user_update';

				let headers = {};
				const csrfToken = document.querySelector('meta[name="_csrf"]');
				const csrfHeader = document.querySelector('meta[name="_csrf_header"]');

				if (csrfToken && csrfHeader) {
					headers[csrfHeader.getAttribute('content')] = csrfToken.getAttribute('content');
				}
                
                

				fetch(serverUrl, {
					method: 'POST',
					body: formData,
					headers: headers

				})
					.then(response => {
						console.log('Received response:', response);
						return response.text().then(text => {
							try {
								return JSON.parse(text);
							} catch (e) {
								throw new Error('서버 응답을 파싱할 수 없습니다: ' + text);
							}
						});
					})
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
                    alert('정보 수정 중 오류가 발생했습니다: ' + error.message);
                });
            }
        }
    });

    // 프로필 이미지 삭제 로직
    if (deleteProfileImageButton) {
        deleteProfileImageButton.addEventListener('click', function() {
            if (confirm('프로필 이미지를 삭제하시겠습니까?')) {
                const formData = new FormData(updateForm);
                formData.set('deleteProfileImage', 'true');
                formData.set('action', 'deleteImage');

                fetch(updateForm.action, {
                    method: 'POST',
                    body: formData
                })
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        alert(data.message);
                        window.location.reload();
                    } else {
                        alert('이미지 삭제에 실패했습니다: ' + data.message);
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                    alert('이미지 삭제 중 오류가 발생했습니다.');
                });
            }
        });
    }

    function validateForm() {
        let isValid = true;

        // 비밀번호 검증
        if (!inputUserPassword.value.match(/^(?=.*[A-Za-z])(?=.*\d).{8,}$/)) {
            showError(inputUserPassword, '비밀번호는 8자리 이상이며, 영문과 숫자를 포함해야 합니다.');
            isValid = false;
        } else if(inputUserPassword.value === oldPassword) {
			showError(inputUserPassword, '새 비밀번호는 기존 비밀번호와 다르게 설정해야 합니다.');
			isValid = false;
		} else {
            clearError(inputUserPassword);
        }

        // 비밀번호 확인 검증
        if (inputUserPassword.value !== inputUserPasswordConfirm.value) {
            showError(inputUserPasswordConfirm, '비밀번호가 일치하지 않습니다.');
            isValid = false;
        } else {
            clearError(inputUserPasswordConfirm);
        }

        // 전화번호 검증
        if (!inputUserPhone.value.match(/^01[0-9]-\d{3,4}-\d{4}$/)) {
            showError(inputUserPhone, '전화번호 형식이 올바르지 않습니다. 예: 010-1234-5678');
            isValid = false;
        } else {
            clearError(inputUserPhone);
        }

        return isValid;
    }
});

