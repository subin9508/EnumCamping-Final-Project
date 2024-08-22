/**
 * price.html에 추가
 */


 document.addEventListener('DOMContentLoaded',()=>{
    
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