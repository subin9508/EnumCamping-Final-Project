/**
 * price.html에 추가
 */


document.addEventListener('DOMContentLoaded', () => {
    const specialPriceInputs = document.querySelectorAll('form.modifyForm input[name^="specialPrice_"]');
    const checkboxes = document.querySelectorAll('input[type="checkbox"][name^="select_"]');

    // 특가 가격 필드에 변화가 있을 때마다 배경색을 변경하고 체크박스 상태를 동기화
    specialPriceInputs.forEach(input => {
        input.addEventListener('input', (event) => {
            const itemId = event.target.name.split('_')[1];
            const checkbox = document.querySelector(`input[name='select_${itemId}']`);
            event.target.style.backgroundColor = '#e0f7fa';
            checkbox.checked = true; // 특가 가격이 변경되면 체크박스를 자동으로 체크
        });
    });

    // 체크박스의 상태에 따라 특가 가격 필드의 스타일 변경
    checkboxes.forEach(checkbox => {
        checkbox.addEventListener('change', function () {
            const itemId = this.name.split('_')[1];
            const specialPriceInput = document.querySelector(`input[name='specialPrice_${itemId}']`);
            if (this.checked) {
                specialPriceInput.style.backgroundColor = '#e0f7fa';
            } else {
                specialPriceInput.style.backgroundColor = ''; // 원래대로 복원
				// 입력 필드를 빈칸으로 설정
				specialPriceInput.value = '';
            }
        });
    });

    // 퍼센트 인상 버튼
    const btnApplyPlus = document.querySelector('button#btnApplyPlus');
    btnApplyPlus.addEventListener('click', () => {
        let percent = parseFloat(document.querySelector('input#percent').value.trim()); // 입력된 퍼센트 값 가져오기
        if (isNaN(percent)) {
            alert("퍼센트 값을 입력해주세요.");
        } else if (confirm(percent + '%를 인상할까요?')) {
            console.log(percent + ' 인상');
            specialPriceInputs.forEach(input => {
                let currentValue = parseFloat(input.value);
                if (!isNaN(currentValue)) {
                    input.value = Math.round(currentValue * (1 + percent * 0.01));
                    input.dispatchEvent(new Event('input')); // 변경된 내용을 보여주기 위해 이벤트 트리거
                }
            });
        }
    });

    // 퍼센트 인하 버튼
    const btnApplyMinus = document.querySelector('button#btnApplyMinus');
    btnApplyMinus.addEventListener('click', () => {
        let percent = parseFloat(document.querySelector('input#percent').value.trim()); // 입력된 퍼센트 값 가져오기
        if (isNaN(percent)) {
            alert("퍼센트 값을 입력해주세요.");
        } else if (confirm(percent + '%를 인하할까요?')) {
            console.log(percent + ' 인하');
            specialPriceInputs.forEach(input => {
                let currentValue = parseFloat(input.value);
                if (!isNaN(currentValue)) {
                    input.value = Math.round(currentValue * (1 - percent * 0.01));
                    input.dispatchEvent(new Event('input'));
                }
            });
        }
    });

    // 특가 적용 버튼
    const btnApplyZones = document.querySelector('button#btnApplyZones');
    btnApplyZones.addEventListener('click', () => {
		console.log('Apply Zones button clicked'); // 버튼 클릭 이벤트 확인
		
        const modifyForm = document.querySelector('form#modifyFormZones');
		
		// 모든 체크박스를 순회하면서 hidden input 설정
		document.querySelectorAll('input[type="checkbox"]').forEach(checkbox => {
		    const hiddenInput = document.createElement('input');
		    hiddenInput.type = 'hidden';
		    hiddenInput.name = checkbox.name;
		    hiddenInput.value = checkbox.checked ? '1' : '0';  // 체크 상태에 따라 값 설정
		    modifyForm.appendChild(hiddenInput);
		});

        if (confirm('변경 내용을 저장할까요?')) {
            modifyForm.submit();
        }
    });

    // 모든 체크박스를 체크
    document.getElementById('btnApplyAllCheck').addEventListener('click', function () {
        document.querySelectorAll('input[type="checkbox"][name^="select_"]').forEach(function (checkbox) {
            checkbox.checked = true;
        });
    });

    // 모든 체크박스 체크 해제
    document.getElementById('btnApplyAllUnCheck').addEventListener('click', function () {
        document.querySelectorAll('input[type="checkbox"][name^="select_"]').forEach(function (checkbox) {
            checkbox.checked = false;
        });
    });

	// 서버에서 최신 가격 정보를 가져오는 버튼
	document.getElementById('btnApplyCallPrice').addEventListener('click', function () {
	    fetch('/enumcamping/admin/getLatestPriceWithSpecialZero')
	    .then(response => response.json())
	    .then(data => {
	        console.log(data); // 결과 로깅
	        // 필요에 따라 결과를 DOM에 반영
	    })
	    .catch(error => {
	        console.error('Error:', error);
	    });
	});

    // 체크박스 상태를 로컬 스토리지에 저장 및 복원
    checkboxes.forEach(function (checkbox) {
        const storedState = localStorage.getItem(checkbox.name);
        checkbox.checked = storedState === "true";

        checkbox.addEventListener('change', function () {
            localStorage.setItem(checkbox.name, checkbox.checked);
        });
    });
});


	  
/*    
const modifyForm = document.querySelector('#modifyForm');
	btnApply.addEventListener('click',()=>{
        // 업데이트 내용 저장 확인
        const result = confirm('변경 내용을 저장할까요?');
        if (result){
            modifyForm.action = 'update'; //요청주소
            modifyForm.method = 'post'; //요청방식
            modifyForm.submit(); //폼 양식 데이터 제출
        }
        
       // alert('변경사항이 적용되었습니다.');
    });
    
    
    
 });*/