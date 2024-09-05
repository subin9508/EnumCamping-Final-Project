/**
 * 
 */

document.addEventListener('DOMContentLoaded', function() {
    const btnPayCancel = document.querySelector('button#btnPayCancel'); // 결제 취소버튼 요소 선택

	// 서버에서 결제 ID를 가져오는 함수, axios를 사용하여 get 요청을 보냄
    const getPayIdByResId = (resId) => {
        return axios.get(`/enumcamping/mypage/reservation_details/getPayId/${resId}`)
            .then(response => response.data) // 응답 데이터 반환
            .catch(error => {
                console.error("결제 ID 조회 실패:", error);
                throw error; // 오류를 다시 던져서 호출자에게 전달
            });
    };
	
	
	
	    // 체크인 날짜와 현재 날짜를 비교하여 환불 비율을 결정하는 함수
    	const getRefundRate = (checkinDateStr) => {
        const checkinDate = new Date(checkinDateStr); // 체크인 날짜를 Date 객체로 변환
        const today = new Date(); // 현재 날짜

        // 체크인 날짜와 현재 날짜의 차이 계산 (밀리초 -> 일수로 변환)
        const timeDifference = checkinDate.getTime() - today.getTime();
        const daysBeforeCheckin = Math.ceil(timeDifference / (1000 * 60 * 60 * 24));

        // 환불 비율 결정
        if (daysBeforeCheckin >= 7) {
            return { rate: 1.0, message: '예약 금액의 100%가 환불됩니다.' };
        } else if (daysBeforeCheckin >= 4 && daysBeforeCheckin <= 6) {
            return { rate: 0.5, message: '체크인 날짜 4일 전 ~ 6일 전이므로 예약 금액의 50%만 환불됩니다.' };
        } else {
            return { rate: 0.0, message: '체크인 날짜가 임박하여 환불이 불가능합니다.' };
        }
    };
	
    // 결제 취소 버튼 이벤트 리스너
     btnPayCancel?.addEventListener('click', (e) => {
		e.preventDefault(); // 기본 버튼 동작(폼 제출 등)을 방지
		
        const resId = document.querySelector("input[name=resId]").value; // 예약 ID 입력값 가져오기
        const userId = document.querySelector("input[name=userId]").value; // userId 입력값 가져오기
		const checkinDate = document.querySelector("input#resCheckIn").value; // 체크인 날짜 가져오기

	
        if (!resId) {
            alert("예약 아이디가 필요합니다."); // 예약 ID 없는 경우 알림
            return;
        }
        
        
        // 체크인 날짜에 따른 환불 비율 및 메시지 결정
        const { rate, message } = getRefundRate(checkinDate);

        // 환불 비율이 0인 경우 알림만 띄우고 취소 처리 중지
        if (rate === 0.0) {
            alert(message);
            return;
        }
	
		// 예약 ID로 결제 ID를 조회하는 함수 호출
        getPayIdByResId(resId).then(payId => {
            console.log("결제 취소 시도: payId=" + payId);

            if (!payId) {
                alert("결제 아이디가 필요합니다.");
                return;
            }
			
			// 결제 취소 확인 팝업
            if (!confirm(message + " 계속하시겠습니까?")) {
                console.log("결제 취소 확인 단계에서 사용자가 취소함");
                return; // 사용자가 취소를 확인하지 않은 경우, 함수 실행을 중지
            }

			// 결제 ID로 결제 취소 요청을 POST로 전송
            axios.post(`/enumcamping/mypage/reservation_details/cancel/${payId}`)
                .then(response => {
                    console.log("결제 취소 성공: ", response.data);
                    alert('결제가 성공적으로 취소되었습니다.');
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