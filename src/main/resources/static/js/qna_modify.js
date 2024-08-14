/**
 * /mypage/qna_modify.html 파일에 포함.
 */
document.addEventListener('DOMContentLoaded', () => {
    // 삭제 버튼을 찾고, 클릭 이벤트 리스너를 설정.
    
    // 업데이트 버튼을 찾고, 클릭 이벤트 리스너를 설정.
    const btnDelete = document.querySelector('button#btnDelete');
    const btnUpdate = document.querySelector('button#btnUpdate');
    
    // 삭제 버튼의 클릭 이벤트 리스너:
    btnDelete.addEventListener('click', () => {
        const check = confirm('정말 삭제할까요?');
        if (check) { // 사용자가 [확인]을 선택했을 때
            // GET 방식의 delete 요청을 서버로 보냄.
            const id = document.querySelector('input#id').value;
			const userId = document.querySelector('input#userId').value;
            location.href = `delete?id=${id}&userId=${userId}`;
        }
    });
    
    // 업데이트 버튼의 클릭 이벤트 리스너:
    btnUpdate.addEventListener('click', () => {
        const title = document.querySelector('input#title').value.trim();
        const content = document.querySelector('textarea#content').value.trim();
        // trim(): 문자열 시작과 끝에 있는 모든 공백을 제거. "  abd def  ".trim() -> "abc def"
        // 제목과 내용이 비어있는 지 체크:
        if (title === '' || content === '') {
            alert('제목과 내용은 반드시 입력해야 합니다.')
            return;
        }        
        
        // 업데이트 내용 저장 확인:
        const result = confirm('변경 내용을 저장할까요?');
        if (result) {
            const updateForm = document.querySelector('form#updateForm');
            updateForm.submit(); // 폼 양식 데이터 제출(서버로 요청 보냄).   
        }        
    });
    
});