/**
 * /user/signup.jsp 파일에 포함.
 */

document.addEventListener('DOMContentLoaded', () => {
    let useridChecked = false; // 사용자 아이디 중복 체크 결과. true: 사용할 수 있는 아이디.
    let passwordChecked = false; // 비밀번호 필드 작성 여부 체크.
    let confirmpasswordChecked = false; // 비밀번호 확인 필드 작성 여부 체크.
    let emailChecked = false; // 이메일 필드 작성 여부 체크.
    let usernameChecked = false; // 사용자 이름 필드 작성 여부 체크.
    let phoneChecked = false; // 휴대폰 번호 필드 작성 여부 체크.
    
    const inputUserid = document.querySelector('input#user_id');
    const checkUseridButton = document.querySelector('button#checkUseridbutton');
    const checkUseridResult = document.querySelector('div#checkUseridResult');

    checkUseridButton.addEventListener('click', checkUserid);

    // 아이디 입력 필드에 변화가 생겼을 때 중복 체크 결과 초기화
    inputUserid.addEventListener('input', () => {
        useridChecked = false; // 아이디 입력 필드에 변화가 생기면 중복 체크 결과 초기화
        checkUseridResult.innerHTML = '아이디 중복 확인을 해주세요.';
        checkUseridResult.classList.add('text-danger');
        checkUseridResult.classList.remove('text-success');
        changeButtonState();
    });

    const inputPassword = document.querySelector('input#user_password');
    inputPassword.addEventListener('input', checkPassword);

    const inputConfirmpassword = document.querySelector('input#user_confirmpassword');
    inputConfirmpassword.addEventListener('input', checkPasswordMatch);
    
    const inputEmail = document.querySelector('input#user_email');
    inputEmail.addEventListener('change', checkEmail);
    
    const inputUsername = document.querySelector('input#user_name');
    inputUsername.addEventListener('change', checkUsername);
    
    const inputPhone = document.querySelector('input#user_phone');
    inputPhone.addEventListener('change', checkPhone);
    
    const btnSignUp = document.querySelector('button#btnSignUp');

    // 회원가입 버튼 클릭 시 최종 유효성 검사
    btnSignUp.addEventListener('click', (event) => {
        if (validateBeforeSignUp(event)) {
            alert('회원가입을 축하합니다');
        }
    });

    /* -------------------- 함수 선언 -------------------- */
    
    // 회원 가입 버튼 활성화/비활성화
    function changeButtonState() {
        if (useridChecked && passwordChecked && confirmpasswordChecked && emailChecked && usernameChecked && phoneChecked) {
            // 버튼의 class 속성 값들 중 'disabled'를 제거 -> 버튼 활성화.
            btnSignUp.classList.remove('disabled');
        } else {
            // 버튼의 class 속성에 'disabled'를 추가 -> 버튼 비활성화.
            btnSignUp.classList.add('disabled');
        }
    }
    
    // userid 입력 필드의 change 이벤트 리스너
    // 중복 아이디 체크 Ajax 요청을 보내고, 응답을 받았을 때 처리.
    function checkUserid(event) {
        const userid = inputUserid.value; // inputUserid.value
        console.log(userid);
        
        const uri = `./checkid?userId=${userid}`; // 아이디 중복 체크 REST API URI
        axios
        .get(uri)
        .then((response) => {
            const checkUseridResult = document.querySelector('div#checkUseridResult');
            if (response.data === 'Y'){
                useridChecked = true;
                checkUseridResult.innerHTML = '사용 가능한 아이디입니다.';
                checkUseridResult.classList.add('text-success');
                checkUseridResult.classList.remove('text-danger');
            } else {
                useridChecked = false;
                checkUseridResult.innerHTML = '사용할 수 없는 아이디입니다.';
                checkUseridResult.classList.add('text-danger');
                checkUseridResult.classList.remove('text-success');
            }
            
            changeButtonState(); // 버튼 활성화 여부를 변경
        })
        .catch((error) => console.log(error));
        
    }
    
    // 비밀번호 입력 필드의 change 이벤트 리스너
    // input#password 비어 있는 지를 체크
    // TODO: 비밀번호 정규표현식도 추가 해야된다.
    function checkPassword(event) {
        const password = event.target.value;

        // 영문, 숫자, 특수문자(공백 제외)를 각각 최소 한 번 포함하는지 확인하는 함수
        function hasValidCharacters(password) {
            const regex = /^[a-zA-Z0-9!"#$%&'()*+,-./:;<=>?@[\]^_`{|}~]+$/;
            return regex.test(password);
        }

        // 최소 두 가지 이상의 조합을 갖추는지 확인하는 함수
        function hasTwoOrMoreTypes(password) {
            const digitRegex = /\d/;
            const letterRegex = /[a-zA-Z]/;
            const specialRegex = /[!"#$%&'()*+,-./:;<=>?@[\]^_`{|}~]/;

            let typeCount = 0;
            if (digitRegex.test(password)) typeCount++;
            if (letterRegex.test(password)) typeCount++;
            if (specialRegex.test(password)) typeCount++;

            return typeCount >= 2;
        }

        // 조건을 만족하는지 검사하고 결과를 표시하는 함수
        function validatePassword() {
            if (password.length < 8) {
                return "최소 8자 이상이어야 합니다.";
            } else if (!hasValidCharacters(password)) {
                return "영문, 숫자, 특수문자(공백 제외)만 허용됩니다.";
            } else if (!hasTwoOrMoreTypes(password)) {
                return "최소 두 가지 이상의 조합이 필요합니다.";
            } else {
                return "사용 가능합니다.";
            }
        }

        // 검사 결과를 표시하는 부분 업데이트
        const validationMessage = validatePassword();
        const passwordValidationMessage = document.getElementById('passwordValidationMessage');
        passwordValidationMessage.textContent = validationMessage;
        if (validationMessage === "사용 가능합니다.") {
            passwordChecked = true;
            passwordValidationMessage.style.color = "green";
        } else {
            passwordChecked = false;
            passwordValidationMessage.style.color = "red";
        }

        checkPasswordMatch(); // 비밀번호가 변경되면 비밀번호 확인 필드도 함께 검사
        changeButtonState(); // 버튼의 활성화/비활성화 상태를 변경
    }

    // 비밀번호 확인 입력 필드의 change 이벤트 리스너
    // input#confirmpassword 비어 있는 지를 체크
    function checkConfirmpassword(event) {
        if (event.target.value === '') {
            confirmpasswordChecked = false;
        } else {
            confirmpasswordChecked = true;
        }
        
        checkPasswordMatch(); // 비밀번호 확인 필드가 변경되면 비밀번호와 일치하는지 검사
        changeButtonState(); // 버튼의 활성화/비활성화 상태를 변경
    }
    
    // 비밀번호 확인 함수 정의
    function checkPasswordMatch() {
        const password = inputPassword.value;
        const confirmPassword = inputConfirmpassword.value;
        const passwordMatchMessage = document.getElementById('passwordMatchMessage');

        // 비밀번호와 확인 비밀번호가 같은지 확인합니다.
        if (password === confirmPassword && passwordChecked) {
            confirmpasswordChecked = true;
            passwordMatchMessage.textContent = "비밀번호 사용 가능합니다.";
            passwordMatchMessage.style.color = "green";
        } else {
            confirmpasswordChecked = false;
            passwordMatchMessage.textContent = "동일한 비밀번호를 입력하세요.";
            passwordMatchMessage.style.color = "red";
        }

        changeButtonState(); // 버튼의 활성화/비활성화 상태를 변경
    }

    // 유저 이름 입력 필드의 change 이벤트 리스너
    // input#username 비어 있는 지를 체크
    function checkUsername(event) {
        if (event.target.value === '') {
            usernameChecked = false;
        } else {
            usernameChecked = true;
        }
        
        changeButtonState();
    }
    
    // 이메일 입력 필드의 change 이벤트 리스너
    // input#email 비어 있는 지를 체크
    // TODO: 이메일 정규표현식도 추가 해야된다. 중복체크는 아이디 중복체크처럼 하면 된다.
    function checkEmail(event) {
        if (event.target.value === '') {
            emailChecked = false;
        } else {
            emailChecked = true;
        }
        
        changeButtonState(); // 버튼의 활성화/비활성화 상태를 변경
    }
    
    // 이메일 입력 필드의 change 이벤트 리스너
    // 중복 이메일 체크 Ajax 요청을 보내고, 응답을 받았을 때 처리.
    function checkEmail(event) {
        const userEmail = inputEmail.value;
        console.log(userEmail);
        const uri = `./checkemail?userEmail=${userEmail}`; // 이메일 중복 체크 REST API URI

        axios
            .get(uri)
            .then((response) => {
                const checkUserEmailResult = document.querySelector('div#checkUserEmailResult');
                if (response.data === 'Y') {
                    emailChecked = true;
                    checkUserEmailResult.innerHTML = '';
                    checkUserEmailResult.classList.add('text-success');
                    checkUserEmailResult.classList.remove('text-danger');
                } else {
                    emailChecked = false;
                    checkUserEmailResult.innerHTML = '이미 가입된 이메일입니다.';
                    checkUserEmailResult.classList.add('text-danger');
                    checkUserEmailResult.classList.remove('text-success');
                }

                changeButtonState(); // 회원 가입 버튼 활성화 여부를 변경
            })
            .catch((error) => console.log(error));
    }

    // 전화번호 입력 필드의 change 이벤트 리스너
    // input#phone 비어 있는 지를 체크 및 정규식 검사
    function checkPhone(event) {
        const phone = event.target.value;
        const phoneRegex = /^01[016789]-\d{3,4}-\d{4}$/;
        const checkUserPhoneResult = document.querySelector('div#checkUserPhoneResult');

        if (phone === '') {
            phoneChecked = false;
            checkUserPhoneResult.innerHTML = '';
        } else if (phoneRegex.test(phone)) {
            phoneChecked = true;
            checkUserPhoneResult.innerHTML = '사용 가능한 전화번호입니다.';
            checkUserPhoneResult.classList.add('text-success');
            checkUserPhoneResult.classList.remove('text-danger');
        } else {
            phoneChecked = false;
            checkUserPhoneResult.innerHTML = '유효하지 않은 전화번호 형식입니다.';
            checkUserPhoneResult.classList.add('text-danger');
            checkUserPhoneResult.classList.remove('text-success');
        }

        changeButtonState(); // 버튼의 활성화/비활성화 상태를 변경
    }
    
    // 회원가입 버튼 클릭 시 최종 유효성 검사
    function validateBeforeSignUp(event) {
        if (!useridChecked) {
            alert('아이디 중복 확인을 해주세요.');
            return false; // 회원가입 진행 중지
        }
        if (!passwordChecked || !confirmpasswordChecked || !emailChecked || !usernameChecked || !phoneChecked) {
            alert('모든 필드를 올바르게 작성해 주세요.');
            return false; // 회원가입 진행 중지
        }
        // 모든 조건을 만족하면 회원가입 진행
        return true;
    }
   

});