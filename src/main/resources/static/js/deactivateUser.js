/**
 * 
 */

 document.addEventListener("DOMContentLoaded", function () {
    const deactivateButton = document.getElementById("deactivateButton");

    deactivateButton.addEventListener("click", function () {
//        const form = document.getElementById("deactivateForm");
//        const formData = new FormData(form);
//        const id = formData.get("id");
//        const password = formData.get("password");
//        const confirmPassword = formData.get("confirmPassword");
		
		const id = document.getElementById("id").value;
        const password = document.getElementById("password").value;
        const confirmPassword = document.getElementById("confirmPassword").value;
		
		
		console.log("아디/비번 확인", { id, password, confirmPassword });
        if (password !== confirmPassword) {
            alert("비밀번호가 일치하지 않습니다.");
            return;
        }
	
		console.log("Sending request with:", { userId: id, userPassword: password });
		
        axios.post('/enumcamping/user/deactivateUser', {
            userId: id,
            userPassword: password
        })
        .then(function (response) {
            alert("계정이 성공적으로 비활성화되었습니다.");
            window.location.href = "/enumcamping"; // 탈퇴 후 메인 페이지로 이동
        })
        .catch(function (error) {
			console.error("Error response:", error.response);
            alert("오류가 발생했습니다.");
        });
    });
});