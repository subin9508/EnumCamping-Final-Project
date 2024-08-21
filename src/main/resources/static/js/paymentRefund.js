/**
 * 
 */

document.addEventListener('DOMContentLoaded', function() {
    const btnPayRefund = document.querySelector('button#btnPayRefund'); // 결제 취소버튼 요소 선택
	
	// 서버에서 결제 ID를 가져오는 함수, axios를 사용하여 get 요청을 보냄
    const getPayIdByResId = (resId) => {
        return axios.get(`/enumcamping/mypage/reservation_details/getPayId/${resId}`)
            .then(response => response.data) // 응답 데이터 반환
            .catch(error => {
                console.error("결제 ID 조회 실패:", error);
                throw error; // 오류를 다시 던져서 호출자에게 전달
            });
    };

	
    // 부분 환불 버튼 이벤트 리스너
    btnPayRefund?.addEventListener('click', (e) => {
		e.preventDefault(); // 기본 버튼 동작(폼 제출 등)을 방지
		
        const resId = document.querySelector("input[name=resId]").value; // 예약 ID 입력값 가져오기
        const userId = document.querySelector("input[name=userId]").value; // userId 입력값 가져오기
		const refundAmount = document.querySelector("input[name=refundAmount]").value; // 부분 환불 금액 입력값 가져오기
		
        if (!resId) {
            alert("예약 아이디가 필요합니다."); // 예약 ID 없는 경우 알림
            return;
        }
	
		// 예약 ID로 결제 ID를 조회하는 함수 호출
        getPayIdByResId(resId).then(payId => {
            console.log("부분 환불 시도: payId=" + payId);

            if (!payId) {
                alert("결제 아이디가 필요합니다.");
                return;
            }
			
			// 부분 환불 확인 팝업
			if (!confirm("정말로 이 금액을 환불하시겠습니까? 금액: " + refundAmount + "원")) {
				console.log("부분 환불 확인 단계에서 사용자가 취소함");
				return; // 사용자가 취소를 확인하지 않은 경우, 함수 실행을 중지
			}

			// 결제 ID로 결제 취소 요청을 POST로 전송
            axios.post(`/enumcamping/mypage/reservation_update/refund/${payId}`)
                .then(response => {
                    console.log("부분 환불 성공: ", response.data);
                    alert('부분환불이 성공적으로 처리되었습니다.');
                    // 결제 취소 후 예약 내역 페이지로 리다이렉트
                    window.location.href = `/enumcamping/mypage/reservation_list?userId=${encodeURIComponent(userId)}`; 
                    //window.location.reload(); // 페이지를 새로고침하여 최신 상태를 반영.
                })
                .catch(error => {
                    console.error("서버 응답 오류: ", error);
                    alert('결제 취소 중 오류가 발생했습니다: ' + error.message);
                });
        })
        .catch(error => {
            console.error("결제 ID 조회 과정에서 오류 발생:", error);
            alert('결제 ID 조회 중 오류가 발생했습니다: ' + error.message);
        });
    });
   
});