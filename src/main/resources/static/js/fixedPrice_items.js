/**
 * 
 */

document.addEventListener('DOMContentLoaded', () => {
    const normalPriceInputs = document.querySelectorAll('form.modifyForm input[name^="price_"]');

    normalPriceInputs.forEach(input => {
        input.addEventListener('input', () => {
            input.style.backgroundColor = '#e0f7fa';
        });
    });

    const descriptionInputs = document.querySelectorAll('form.modifyForm input[name^="desc_"]');
    descriptionInputs.forEach(input => {
        input.addEventListener('input', () => {
            input.style.backgroundColor = '#e0f7fa';
        });
    });

    document.getElementById('btnApplyItems').addEventListener('click', () => {
        const form = document.querySelector('form#modifyFormItems');
        if (confirm('변경 내용을 저장할까요?')) {
            form.submit();
        }
    });

    document.getElementById('btnApplyPlus').addEventListener('click', () => {
        applyPercentageChange(1);
    });

    document.getElementById('btnApplyMinus').addEventListener('click', () => {
        applyPercentageChange(-1);
    });

    function applyPercentageChange(direction) {
        const percent = parseFloat(document.getElementById('percent').value);
        if (!isNaN(percent)) {
            const factor = direction === 1 ? (1 + percent / 100) : (1 - percent / 100);
            if (confirm(`가격을 ${percent}% ${direction === 1 ? '인상' : '인하'}할까요?`)) {
                adjustPrices(normalPriceInputs, factor);
            }
        } else {
            alert("퍼센트 값을 올바르게 입력해주세요.");
        }
    }

    function adjustPrices(inputs, factor) {
        inputs.forEach(input => {
            const originalPrice = parseFloat(input.value);
            if (!isNaN(originalPrice)) {
                let newPrice = Math.round(originalPrice * factor);
                input.value = newPrice;
                input.style.backgroundColor = '#e0f7fa';
            }
        });
    }
});
