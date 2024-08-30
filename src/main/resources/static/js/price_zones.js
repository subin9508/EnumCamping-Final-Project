/**
 * price.html에 추가
 */


 document.addEventListener('DOMContentLoaded',()=>{
    
    
    const inputs = document.querySelectorAll('form.modifyForm input[type="text"]');
    const modifiedInputs = new Set(); // 수정된 input 요소들을 저장하는 Set


    inputs.forEach(input => {
        input.addEventListener('input', (event) => {
            //change는// 사용자가 input을 수정한 경우, 포커스 이동을 해야 적용됨
            // 사용자가 input을 수정하는 즉시 호출됨
            modifiedInputs.add(event.target);
            event.target.style.backgroundColor = '#e0f7fa'; // 수정된 요소에 배경색을 추가 (체크용)
        });
    });
    
    const btnApplyPlus = document.querySelector('button#btnApplyPlus');
    btnApplyPlus.addEventListener('click', () => {
        let percent = document.querySelector('input#percent').value.trim(); // 입력된 퍼센트 값 가져오기
        percent = parseFloat(percent);
        if (isNaN(percent)) {
            alert("퍼센트 값을 입력해주세요.");
        } else if (confirm(percent + '%를 인상할까요?')) {
            console.log(percent+'인상');
            inputs.forEach(input => {
                let currentValue = parseFloat(input.value);
                console.log(currentValue);
                if (!isNaN(currentValue)) {
                    input.value =  Math.round(currentValue * (1+percent*0.01)); 
                    input.dispatchEvent(new Event('input')); //없어도 잘 저장되긴 하는데 있으면 바뀐게 보여서 좋음
                }
            });
        }
    });
        
    const btnApplyMinus = document.querySelector('button#btnApplyMinus');
        btnApplyMinus.addEventListener('click', () => {
            let percent = document.querySelector('input#percent').value.trim(); // 입력된 퍼센트 값 가져오기
            percent = parseFloat(percent);
            if (isNaN(percent)) {
                alert("퍼센트 값을 입력해주세요.");
            } else if (confirm(percent + '%를 인하할까요?')) {
                console.log(percent+'인하');
                inputs.forEach(input => {
                    let currentValue = parseFloat(input.value);
                    console.log(currentValue);
                    if (!isNaN(currentValue)) {
                        input.value =  Math.round(currentValue * (1-percent*0.01)); 
                        input.dispatchEvent(new Event('input'));
                    }
                });
            }
        });
    
    
	const btnApplyZones = document.querySelector('button#btnApplyZones');
	    btnApplyZones.addEventListener('click', () => {
	        const modifyForm = document.querySelector('form#modifyFormZones');
	        if (confirm('변경 내용을 저장할까요?')) {
	            modifyForm.submit();
	        }
	    });
    
	});
	
	
	document.getElementById('btnApplyAllCheck').addEventListener('click', function() {
	    // 'name' 속성이 'select_'로 시작하는 모든 체크박스를 찾아서 반복문으로 처리
	    document.querySelectorAll('input[type="checkbox"][name^="select_"]').forEach(function(checkbox) {
	        checkbox.checked = true; // 체크박스를 선택
	    });
	});
	
	document.getElementById('btnApplyAllUnCheck').addEventListener('click', function() {
	    // 'name' 속성이 'select_'로 시작하는 모든 체크박스를 찾아서 반복문으로 처리
	    document.querySelectorAll('input[type="checkbox"][name^="select_"]').forEach(function(checkbox) {
	        checkbox.checked = false; // 체크박스의 선택을 해제
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