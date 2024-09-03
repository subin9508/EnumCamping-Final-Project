document.addEventListener('DOMContentLoaded', function() {
    // 결제 버튼을 DOM에서 찾아서 변수에 저장
    const btnPayment = document.querySelector('button#btnPayment');
    // totalAmount 값을 가져와서 정수로 변환
    const totalAmount = parseInt(document.getElementById('totalAmount').textContent.trim(), 10);
    
    
    //결제 버튼에 클릭 이벤트 리스너 연결	
    if (btnPayment) {
        btnPayment.addEventListener('click', handlePaymentClick);
    }
	
	// 결제 버튼 클릭 시 실행되는 비동기 함수
    async function handlePaymentClick() {
		//입력된 예약 ID를 가져옴
        const resId = document.querySelector("input[name=resId]").value;
        
        //예약 ID 로깅
        console.log(resId);
        
        //예약 ID가 없으면 경고 메시지 표시 후 함수 종로
        if (!resId) {
            alert("예약 ID가 필요합니다.");
            return;
        }
        
        try {
            const paymentInfo = await getPaymentInfo(resId);
            console.log("결제 정보:", paymentInfo);

                requestPayment(paymentInfo, resId);
                console.log("결제 금액 확인",totalAmount);

        } catch (error) {
            console.error("결제 정보 조회 중 오류:", error);
            alert('결제 정보를 가져오는 중 오류가 발생했습니다. 다시 시도해 주세요.');
        }
    }

	// 결제 요청 함수
    function requestPayment(paymentInfo, resId) {
		//I'mport 결제 라이브러리 초기화
        const IMP = window.IMP;
        IMP.init('imp53143455');
        
        IMP.request_pay({ //요청 전문양식을 그대로 가지고와서 우리 입맛에 맞게 넣음

            pg: 'html5_inicis', // pg사
            pay_method: 'card',
            merchant_uid: 'merchant_' + new Date().getTime(), //우리 상점 주문 번호
            name: paymentInfo.name, //상품 이름
            amount: totalAmount, //상품 총가격
            buyer_email: paymentInfo.email, //구매자 이메일
            buyer_name: paymentInfo.buyerName, //구매자 이름
            buyer_tel: paymentInfo.phoneNumber //구매자 핸드폰 번호

        }, function(rsp) {
            handlePaymentResponse(rsp, resId); //응답함수를 실행
        });
    }
    
    

	// 결제 응답 처리 비동기 함수
	async function handlePaymentResponse(rsp, resId) {
    // 결제 응답 로깅
    console.log('Payment Response:', rsp);
    
     // 결제 응답 정보를 로컬 스토리지에 저장
    localStorage.setItem('paymentResponse', JSON.stringify(rsp));

    if (rsp.success) {
        console.log("결제 성공, 검증 시작");
        try {
            // 결제 검증 및 정보 저장 함수 호출
            const result = await verifyAndSavePayInfo(rsp.imp_uid, resId);
            console.log("검증 결과:", result);
            if (result && (result.status === "paid")) {
                alert('추가 결제가 완료되었습니다.');
                // resId 로깅
 
                console.log("이동할 예약 ID:", resId);
                window.location.href = `/enumcamping/mypage/reservation_update_successed/${resId}`; // 결제 성공 페이지로 이동
            } else {
                throw new Error("서버 검증 실패" + (result.fail_reason || "알 수 없는 오류"));
            }
        } catch (error) {
            console.error("결제 검증 및 저장 중 오류:", error);
            alert('결제 검증 중 오류가 발생했습니다. 오류 내용: ' + error.message);
        }
    } else {
        console.log('Payment Failed:', rsp);
        alert('결제에 실패했습니다. 다시 시도해 주세요.');
    }
}

//결제 정보 조회함수
    async function getPaymentInfo(resId) {
        try {
			
			//서버에 결제 정보 조회 요청
            const response = await $.ajax({
                type: "GET",
                url: "/enumcamping/reservation/paymentInfo/" + resId,
                dataType: 'json'
            });
            return response;
        } catch (error) {
            console.error("결제 정보 조회 실패:", error);
            throw error;
        }
    }
    
//결제 검증 및 정보 저장 함수
    async function verifyAndSavePayInfo(imp_uid, resId) { //결제 검증, 저장 
        try {
            const response = await $.ajax({ //서버에 요청
                type: "POST",
                url: "/enumcamping/additional_payment/verifyIamport/" + imp_uid + "?resId=" + resId,
                dataType: 'json'
            });arguments
            //서버 응답 로깅
            console.log("서버 응답:", response);
            return response;
            
            
        } catch (error) {
            console.error("결제 정보 저장 실패:", error);
            throw error;
        }
    }
});