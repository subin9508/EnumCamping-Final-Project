/**
 * 
 */

document.addEventListener('DOMContentLoaded', () => {
    // 정상가와 설명 입력 필드 선택
    const normalPriceInputs = document.querySelectorAll('form.modifyForm input[name^="price_"]');
    const descriptionInputs = document.querySelectorAll('form.modifyForm input[name^="desc_"]');

    // 가격 입력 필드에 대한 이벤트 리스너 추가
    normalPriceInputs.forEach(input => {
        input.addEventListener('input', () => {
            // 사용자 입력 시 배경색 변경
            input.style.backgroundColor = '#e0f7fa';
        });
    });

    // 설명 입력 필드에 대한 이벤트 리스너 추가
    descriptionInputs.forEach(input => {
        input.addEventListener('input', () => {
            // 사용자 입력 시 배경색 변경
            input.style.backgroundColor = '#e0f7fa';
        });
    });

    // 적용하기 버튼의 이벤트 처리
    document.getElementById('btnApplyItems').addEventListener('click', () => {
        // 폼 객체를 가져와서 서버에 제출
        const form = document.querySelector('form#modifyFormItems');
        if (confirm('변경 내용을 저장할까요?')) {
            form.submit();
        }
    });
	
    // 퍼센트로 인상 및 인하 기능
    document.getElementById('btnApplyPlus').addEventListener('click', () => {
        applyPercentageChange(1);  // 인상
    });

    document.getElementById('btnApplyMinus').addEventListener('click', () => {
        applyPercentageChange(-1);  // 인하
    });

    function applyPercentageChange(direction) {
        const percent = parseFloat(document.getElementById('percent').value);
        if (!isNaN(percent)) {
            const factor = direction === 1 ? (1 + percent / 100) : (1 - percent / 100);
            if (confirm(`가격을 ${percent}% ${direction === 1 ? '인상' : '인하'}할까요?`)) {
                adjustPrices([...normalPriceInputs, ...descriptionInputs], factor);
            }
        } else {
            alert("퍼센트 값을 올바르게 입력해주세요.");
        }
    }

    function adjustPrices(inputs, factor) {
        inputs.forEach(input => {
            const originalValue = parseFloat(input.value);
            if (!isNaN(originalValue)) {
                let newValue = Math.round(originalValue * factor);
                input.value = newValue;
                input.style.backgroundColor = '#e0f7fa';
            }
        });
    }
});
