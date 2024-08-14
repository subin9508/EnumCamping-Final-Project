/**
 * 
 */

 document.addEventListener('DOMContentLoaded',()=>{
    
    //const btnDelete = document.querySelector('button#btnDelete');
    const btnUpdate = document.querySelector('button#btnUpdate');
    const modifyForm = document.querySelector('form#modifyForm');
    const inputTitle = document.querySelector('input#title');
    const textContent = document.querySelector('textarea#content');
    
    btnUpdate.addEventListener('click',()=>{
        // 제목과 내용이 비어있는 지 체크
        if (inputTitle.value === '' || textContent.value===''){
            alert('제목과 내용은 반드시 입력하세요');
            return;
        }
        // 업데이트 내용 저장 확인
        const result = confirm('변경 내용을 저장할까요?');
        if (result){
            modifyForm.action = 'update'; //요청주소
            modifyForm.method = 'post'; //요청방식
            modifyForm.submit(); //폼 양식 데이터 제출
        }
        
    });
    
    
    
 });