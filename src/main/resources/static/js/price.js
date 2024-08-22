/**
 * price.html에 추가
 */


 document.addEventListener('DOMContentLoaded',()=>{
    
    
    
    const inputs = document.querySelectorAll('#modifyForm input[type="text"]');
    const modifiedInputs = new Set(); // 수정된 input 요소들을 저장하는 Set

    inputs.forEach(input => {
        input.addEventListener('input', (event) => {
            //change는// 사용자가 input을 수정한 경우, 포커스 이동을 해야 적용됨
            // 사용자가 input을 수정하는 즉시 호출됨
            modifiedInputs.add(event.target);
            event.target.style.backgroundColor = '#e0f7fa'; // 수정된 요소에 배경색을 추가 (체크용)
        });
    });
    
    
    const btnApply = document.querySelector('button#btnApply');
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
    
    
    
 });