/**
 * details에 추가
 */

 document.addEventListener('DOMContentLoaded', ()=>{
    
    const inputId = document.querySelector('input#id');
    const btnDelete = document.querySelector('button#btnDelete');
        
    btnDelete.addEventListener('click', () => {
        const result = confirm('정말 삭제할까요?');
        if (result) { //사용자가 [확인]을 선택햇을 때
            //get방식의 delete 요청을 서버로 보냄
            location.href = `delete?id=${inputId.value}`;

        }
    });
    
    
    
    
 });
 
 
 