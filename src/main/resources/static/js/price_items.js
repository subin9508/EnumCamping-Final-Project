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

	// 적용하기 버튼 클릭 시 모든 변경 사항을 서버로 전송
		document.getElementById('btnApplyItems').addEventListener('click', () => {
		    console.log('Apply Items button clicked'); // 버튼 클릭 이벤트 확인

		    const modifyForm = document.querySelector('form#modifyFormItems');

			// 모든 체크박스를 순회하면서 hidden input 설정
			    checkboxes.forEach(checkbox => {
			        const hiddenInput = document.createElement('input');
			        hiddenInput.type = 'hidden';
			        hiddenInput.name = checkbox.name;
			        hiddenInput.value = checkbox.checked ? '1' : '0';  // 체크 상태에 따라 값 설정
			        modifyForm.appendChild(hiddenInput);

			        // 체크박스가 해제된 경우 서버에 업데이트 요청을 보냄
			        if (!checkbox.checked) {
			            const itemId = checkbox.name.split('_')[1]; // itemId 추출
			            updateSpecialEndDateAndInsertRecord(itemId); // 서버 요청 함수 호출
			        }
			    });
				
				    if (confirm('변경 내용을 저장할까요?')) {
				        modifyForm.submit();
				    }
				});
		
		// 서버에 end_date를 업데이트하고 새로운 레코드를 삽입하는 요청 함수
		function updateSpecialEndDateAndInsertRecord(itemId) {
		    fetch('/enumcamping/admin/updateSpecialAndInsertRecord', {
		        method: 'POST',
		        headers: {
		            'Content-Type': 'application/json',
		        },
		        body: JSON.stringify({ itemId: itemId }) // itemId만 전송
		    })
		    .then(response => {
		        if (!response.ok) {
		            throw new Error('Network response was not ok');
		        }
		        return response.json(); // JSON 형식으로 응답을 파싱
		    })
		    .then(data => {
		        console.log('End date updated and new record inserted:', data);
		    })
		    .catch(error => {
		        console.error('Error updating end date and inserting new record:', error);
		    });
		}
	

		
		
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

