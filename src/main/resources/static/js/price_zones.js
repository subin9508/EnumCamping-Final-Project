document.addEventListener('DOMContentLoaded', () => {
    const specialPriceInputs = document.querySelectorAll('form.modifyForm input[name^="specialPrice_"]');
    const normalPriceInputs = document.querySelectorAll('form.modifyForm input[name^="price_"]');
    const checkboxes = document.querySelectorAll('input[type="checkbox"][name^="select_"]');

    function updateSpecialPriceField(checkbox, specialPriceInput) {
        if (checkbox.checked) {
            specialPriceInput.style.backgroundColor = '#e0f7fa';
        } else {
            specialPriceInput.style.backgroundColor = '';
        }
    }

    checkboxes.forEach(checkbox => {
        const storedState = localStorage.getItem(checkbox.name);
        checkbox.checked = storedState === "true";
        const itemId = checkbox.name.split('_')[1];
        const specialPriceInput = document.querySelector(`input[name='specialPrice_${itemId}']`);
        updateSpecialPriceField(checkbox, specialPriceInput);

        checkbox.addEventListener('change', () => {
            updateSpecialPriceField(checkbox, specialPriceInput);
            localStorage.setItem(checkbox.name, checkbox.checked);
            if (!checkbox.checked) {
                specialPriceInput.value = '';
            }
        });
    });

    specialPriceInputs.forEach(input => {
        input.addEventListener('input', () => {
            const itemId = input.name.split('_')[1];
            const checkbox = document.querySelector(`input[name='select_${itemId}']`);
            checkbox.checked = true;
            localStorage.setItem(checkbox.name, checkbox.checked);
            input.style.backgroundColor = '#e0f7fa';
        });
    });

    document.getElementById('btnApplyZones').addEventListener('click', () => {
        const modifyForm = document.querySelector('form#modifyFormZones');
        checkboxes.forEach(checkbox => {
            const hiddenInput = document.createElement('input');
            hiddenInput.type = 'hidden';
            hiddenInput.name = checkbox.name;
            hiddenInput.value = checkbox.checked ? '1' : '0';
            modifyForm.appendChild(hiddenInput);
        });

        if (confirm('변경 내용을 저장할까요?')) {
            modifyForm.submit();
        }
    });

    document.getElementById('btnApplyAllCheck').addEventListener('click', () => {
        checkboxes.forEach(checkbox => {
            checkbox.checked = true;
            localStorage.setItem(checkbox.name, true);
            const itemId = checkbox.name.split('_')[1];
            const specialPriceInput = document.querySelector(`input[name='specialPrice_${itemId}']`);
            updateSpecialPriceField(checkbox, specialPriceInput);
        });
    });

    document.getElementById('btnApplyAllUnCheck').addEventListener('click', () => {
        checkboxes.forEach(checkbox => {
            checkbox.checked = false;
            localStorage.setItem(checkbox.name, false);
            const itemId = checkbox.name.split('_')[1];
            const specialPriceInput = document.querySelector(`input[name='specialPrice_${itemId}']`);
            updateSpecialPriceField(checkbox, specialPriceInput);
        });
    });

    function adjustPrices(inputs, factor) {
        inputs.forEach((input, index) => {
            const originalPrice = parseFloat(input.value);
            if (!isNaN(originalPrice)) {
                let newPrice = Math.round(originalPrice * factor);
                const correspondingSpecialInput = specialPriceInputs[index];
                correspondingSpecialInput.value = newPrice;
                correspondingSpecialInput.dispatchEvent(new Event('input'));
            }
        });
    }

    document.getElementById('btnApplyPlus').addEventListener('click', () => {
        let percent = parseFloat(document.getElementById('percent').value);
        if (!isNaN(percent)) {
            if (confirm(`${percent}% 인상할까요?`)) {
                adjustPrices(normalPriceInputs, 1 + percent / 100);
            }
        } else {
            alert("퍼센트 값을 입력해주세요.");
        }
    });

    document.getElementById('btnApplyMinus').addEventListener('click', () => {
        let percent = parseFloat(document.getElementById('percent').value);
        if (!isNaN(percent)) {
            if (confirm(`${percent}% 인하할까요?`)) {
                adjustPrices(normalPriceInputs, 1 - percent / 100);
            }
        } else {
            alert("퍼센트 값을 입력해주세요.");
        }
    });
});
