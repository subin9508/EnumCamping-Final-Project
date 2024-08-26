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
        const percent = document.querySelector('input#percent').value.trim(); // 입력된 퍼센트 값 가져오기
        if (percent === "") {
            alert("퍼센트 값을 입력해주세요.");
        } else if (confirm(percent + '%를 인상할까요?')) {
            // 사용자가 확인 버튼을 클릭한 경우, 해당 URL로 이동
            window.location.href = `/admin/price/zones/percentupdate?percent=${encodeURIComponent(percent)}`;
        }
    });
        
    const btnApplyMinus = document.querySelector('button#btnApplyMinus');
        btnApplyMinus.addEventListener('click', () => {
        const percent = document.querySelector('input#percent').value;
            if (confirm(percent +'%를 인하할까요?')) {
                console.log(percent+'인하');
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