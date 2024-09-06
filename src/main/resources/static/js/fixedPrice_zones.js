/**
 * 
 */

document.addEventListener('DOMContentLoaded', () => {
    // 가격 입력 필드를 모두 선택
    const priceInputs = document.querySelectorAll('input[type="text"][name^="price_"]');

    // 각 입력 필드에 대한 이벤트 리스너를 추가
    priceInputs.forEach(input => {
        input.addEventListener('input', () => {
            // 사용자가 입력을 시작하면 배경색을 변경
            input.style.backgroundColor = '#e0f7fa';
        });
    });

    // 적용하기 버튼의 클릭 이벤트 처리
    const applyButton = document.getElementById('btnApplyZones');
    applyButton.addEventListener('click', () => {
        // 폼을 제출하기 전에 확인 메시지 표시
        if (confirm('변경 내용을 저장할까요?')) {
            // 폼 객체를 가져와서 제출
            const form = document.getElementById('modifyFormZones');
            form.submit();
        }
    });
});