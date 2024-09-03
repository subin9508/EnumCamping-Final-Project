/**
 * price.html에 추가
 */


document.addEventListener('DOMContentLoaded', () => {
    // 특가 가격 인풋 요소만 선택
    const specialPriceInputs = document.querySelectorAll('form.modifyForm input[name^="specialPrice_"]');
    const modifiedInputs = new Set(); // 수정된 인풋 요소들을 저장하는 Set

    // 인풋 필드가 변경될 때마다 배경색을 변경하여 시각적으로 확인
    specialPriceInputs.forEach(input => {
        input.addEventListener('input', (event) => {
            modifiedInputs.add(event.target);
            event.target.style.backgroundColor = '#e0f7fa'; // 수정된 요소에 배경색을 추가 (체크용)
        });
    });

    // 퍼센트 인상 버튼
    const btnApplyPlus = document.querySelector('button#btnApplyPlus');
    btnApplyPlus.addEventListener('click', () => {
        let percent = document.querySelector('input#percent').value.trim(); // 입력된 퍼센트 값 가져오기
        percent = parseFloat(percent);
        if (isNaN(percent)) {
            alert("퍼센트 값을 입력해주세요.");
        } else if (confirm(percent + '%를 인상할까요?')) {
            console.log(percent + ' 인상');
            specialPriceInputs.forEach(input => {
                let currentValue = parseFloat(input.value);
                console.log(currentValue);
                if (!isNaN(currentValue)) {
                    input.value = Math.round(currentValue * (1 + percent * 0.01)); 
                    input.dispatchEvent(new Event('input')); // 없애도 잘 저장되긴 하는데 있으면 바뀐게 보여서 좋음
                }
            });
        }
    });

    // 퍼센트 인하 버튼
    const btnApplyMinus = document.querySelector('button#btnApplyMinus');
    btnApplyMinus.addEventListener('click', () => {
        let percent = document.querySelector('input#percent').value.trim(); // 입력된 퍼센트 값 가져오기
        percent = parseFloat(percent);
        if (isNaN(percent)) {
            alert("퍼센트 값을 입력해주세요.");
        } else if (confirm(percent + '%를 인하할까요?')) {
            console.log(percent + ' 인하');
            specialPriceInputs.forEach(input => {
                let currentValue = parseFloat(input.value);
                console.log(currentValue);
                if (!isNaN(currentValue)) {
                    input.value = Math.round(currentValue * (1 - percent * 0.01)); 
                    input.dispatchEvent(new Event('input'));
                }
            });
        }
    });

    // 특가 적용 버튼
    const btnApplyItems = document.querySelector('button#btnApplyItems');
    btnApplyItems.addEventListener('click', () => {
        console.log('버튼 클릭됨');
        const modifyForm = document.querySelector('form#modifyFormItems');
        if (confirm('변경 내용을 저장할까요?')) {
            modifyForm.submit();
        }
    });

    // 모든 체크박스 가져오기
    const checkboxes = document.querySelectorAll('input[type="checkbox"][name^="select_"]');
    checkboxes.forEach(checkbox => {
        checkbox.addEventListener('change', function () {
            const itemId = this.name.split('_')[1]; // item ID 추출
            const specialPriceInput = document.querySelector(`input[name='specialPrice_${itemId}']`);

            if (this.checked) {
                // 체크박스가 체크된 경우
                specialPriceInput.style.backgroundColor = '#e0f7fa';
            } else {
                // 체크박스가 체크 해제된 경우
                specialPriceInput.style.backgroundColor = ''; // 원래대로 복원
            }
        });
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

    // 체크박스 상태를 저장하고 복원하기 위한 로직
    checkboxes.forEach(function (checkbox) {
        const storedState = localStorage.getItem(checkbox.name);
        checkbox.checked = storedState === "true"; // 로컬 스토리지에서 체크 상태 복원

        checkbox.addEventListener('change', function () {
            localStorage.setItem(checkbox.name, checkbox.checked); // 체크박스 상태를 로컬 스토리지에 저장
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