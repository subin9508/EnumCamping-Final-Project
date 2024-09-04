document.addEventListener('DOMContentLoaded', function() {
    const btnPayRefund = document.querySelector('button#btnPayRefund');
    
    const getPayIdByResId = (resId) => {
        return axios.get(`/enumcamping/mypage/reservation_update/getPaymentInfo/${resId}`)
            .then(response => response.data)
            .catch(error => {
                console.error("결제 ID 조회 실패:", error);
                throw error;
            });
    };

    btnPayRefund?.addEventListener('click', async (e) => {
        e.preventDefault();
        
        const resId = document.querySelector("input[name=resId]").value;
        const userId = document.querySelector("input[name=userId]").value;
        const refundAmount = document.querySelector("input[name=refundAmount]").value;
        
        console.log('resId=', resId);
        console.log('refundAmount=', refundAmount);
        
        if (!refundAmount) {
            alert("환불 금액을 입력하세요.");
            return;
        }
        
        if (!resId) {
            alert("예약 아이디가 필요합니다.");
            return;
        }
    
        try {
            const paymentInfo = await getPayIdByResId(resId);
            const payMethod = paymentInfo.payMethod;
            const payId = paymentInfo.payId;
            
            console.log("부분 환불 시도: resId=" + resId);
            console.log("부분 환불 시도: payId=" + payId);
            console.log("부분 환불 시도: payMethod=" + payMethod);

            if (!payId) {
                alert("결제 아이디가 필요합니다.");
                return;
            }

            if (payMethod === 'point') {
                alert("point로 결제 시 변경금액 전체 결제 후, 이전 금액 전체 취소 됩니다.");
                try {
                    await requestAdditionalPayment(resId);
                    await cancelFullPayment(payId, resId);
                } catch (error) {
                    console.error("포인트 결제 처리 중 오류 발생:", error);
                    alert('처리 중 오류가 발생했습니다: ' + error.message);
                }
                return;
            }

            if (!confirm("정말로 이 금액을 환불하시겠습니까? 금액: " + refundAmount + "원")) {
                console.log("부분 환불 확인 단계에서 사용자가 취소함");
                return;
            }

            await axios.post(`/enumcamping/mypage/reservation_update/refund/${payId}`, null, {
                params: { cancelAmount: refundAmount }
            });

            console.log("부분 환불 성공");
            alert('부분환불이 성공적으로 처리되었습니다.');
            window.location.href = `/enumcamping/mypage/reservation_update_successed/${resId}`;

        } catch (error) {
            console.error("결제 처리 과정에서 오류 발생:", error);
            alert('결제 처리 중 오류가 발생했습니다: ' + error.message);
        }
    });

    function requestAdditionalPayment(resId) {
        return new Promise((resolve, reject) => {
            const oldTotalAmount = parseInt(document.getElementById('oldTotalAmount').textContent.trim(), 10);
            const refundAmount = parseInt(document.getElementById('totalAmount').textContent.trim(), 10);
            const totalAmount = Math.max(0, oldTotalAmount + refundAmount);
            
            console.log('oldTotalAmount:', oldTotalAmount);
            console.log('refundAmount:', refundAmount);
            console.log('totalAmount:', totalAmount);
            
            const IMP = window.IMP;
            IMP.init('imp53143455');

            IMP.request_pay({
                pg: 'html5_inicis',
                pay_method: 'card',
                merchant_uid: 'merchant_' + new Date().getTime(),
                name: "추가 결제",
                amount: totalAmount,
                buyer_email: "user@example.com",
                buyer_name: "홍길동",
                buyer_tel: "010-1234-5678"
            }, function(rsp) {
                if (rsp.success) {
                    alert('추가 결제가 완료되었습니다.');
                    resolve();
                } else {
                    alert('추가 결제에 실패했습니다. 다시 시도해 주세요.');
                    reject(new Error('추가 결제 실패'));
                }
            });
        });
    }
      
    function cancelFullPayment(payId, resId) {
        return axios.post(`/enumcamping/mypage/reservation_details/cancel/${payId}`)
            .then(response => {
                console.log("전체 취소 성공: ", response.data);
                alert('이전 결제 금액이 성공적으로 취소되었습니다.');
                window.location.href = `/enumcamping/mypage/reservation_update_successed/${resId}`;
            })
            .catch(error => {
                console.error("전체 취소 실패: ", error);
                throw error;
            });
    }
});