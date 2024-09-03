/**
 * price.html에 추가
 */


document.addEventListener('DOMContentLoaded', () => {
    const specialPriceInputs = document.querySelectorAll('form.modifyForm input[name^="specialPrice_"]');
    const checkboxes = document.querySelectorAll('input[type="checkbox"][name^="select_"]');

	    // 체크박스 상태를 로컬 스토리지에서 복원하고, 이벤트 핸들러 등록
	    checkboxes.forEach(checkbox => {
	        const storedState = localStorage.getItem(checkbox.name);
	        checkbox.checked = storedState === "true";

	        const itemId = checkbox.name.split('_')[1];
	        const specialPriceInput = document.querySelector(`input[name='specialPrice_${itemId}']`);

	        // 로컬 스토리지 상태에 따라 특가 가격 필드의 스타일 설정
	        updateSpecialPriceField(checkbox, specialPriceInput);

	        // 체크박스 상태 변경 이벤트 핸들러
	        checkbox.addEventListener('change', function () {
	            updateSpecialPriceField(this, specialPriceInput);
	            localStorage.setItem(this.name, this.checked); // 체크박스 상태를 로컬 스토리지에 저장
				
				// 서버에 특가 여부 해제 또는 설정 요청
				if (!this.checked) {
				                // 체크박스를 해제할 때 특가 가격 필드를 빈칸으로 설정
				                specialPriceInput.value = '';
				                // 서버로 end_date 업데이트 요청 전송
				                updateSpecialEndDateAndInsertNewRecord(itemId);
				            } else {
				                // 체크박스를 다시 체크한 경우 로컬 스토리지에서 값 유지
				                localStorage.setItem(this.name, true);
				            }
	        });
	    });

	    // 특가 가격 필드 입력 이벤트 핸들러
	    specialPriceInputs.forEach(input => {
	        input.addEventListener('input', (event) => {
	            const itemId = event.target.name.split('_')[1];
	            const checkbox = document.querySelector(`input[name='select_${itemId}']`);

	            event.target.style.backgroundColor = '#e0f7fa'; // 입력 시 배경색 변경
	            checkbox.checked = true; // 특가 가격이 변경되면 체크박스를 자동으로 체크

	            // 체크박스 상태를 로컬 스토리지에 저장
	            localStorage.setItem(checkbox.name, checkbox.checked);
	        });
	    });

	    // 체크박스와 특가 가격 필드의 스타일을 업데이트하는 함수
	    function updateSpecialPriceField(checkbox, specialPriceInput) {
	        if (checkbox.checked) {
	            specialPriceInput.style.backgroundColor = '#e0f7fa';
	        } else {
	            specialPriceInput.style.backgroundColor = '';
	        }
	    }
	
	
	// 적용하기 버튼 클릭 시 모든 변경 사항을 서버로 전송
	document.getElementById('btnApplyZones').addEventListener('click', () => {
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
	
	// 서버에 end_date를 업데이트하고 새로운 레코드를 삽입하는 요청 함수
	function updateSpecialEndDateAndInsertNewRecord(itemId) {
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
	
	
/*    // 특가 적용 버튼
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
    });*/


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


// 모든 체크박스를 체크
document.getElementById('btnApplyAllCheck').addEventListener('click', function () {
    checkboxes.forEach(checkbox => {
        checkbox.checked = true;
        localStorage.setItem(checkbox.name, true);
    });
});

// 모든 체크박스 체크 해제
document.getElementById('btnApplyAllUnCheck').addEventListener('click', function () {
    checkboxes.forEach(checkbox => {
        checkbox.checked = false;
        localStorage.setItem(checkbox.name, false);
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