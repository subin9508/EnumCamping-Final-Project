
document.addEventListener('DOMContentLoaded', () => {
    let currentPageNo = 0; // 현재 댓글 목록의 페이지 번호
    //-> getAllComments() 함수에서 Ajax 요청을 보내고, 정상 응답이 오면 현재 페이지 번호가 바뀜.
    //-> currentPageNo의 값은 [더보기] 버튼에서 사용.
    
    // bootstrap 라이브러리의 Collapse 객체를 생성:
    const bsCollapse = new bootstrap.Collapse('div#collapseQnAAnswers', {toggle: false});
    
    // [댓글 보기] 버튼을 찾아서, 클릭 이벤트 리스너를 설정.
    const btnToggle = document.querySelector('button#btnToggle');
    btnToggle.addEventListener('click', () => {
        bsCollapse.toggle(); // Collapse 객체를 보기/감추기 토글.
        
        const toggle = btnToggle.getAttribute('data-toggle');
        if (toggle === 'collapse') {
            btnToggle.innerHTML = '댓글 감추기';
            btnToggle.setAttribute('data-toggle', 'unfold');
            
            // 댓글 목록 불러오기:
            getAllQnAAnswers(0);
        } else {
            btnToggle.innerHTML = '댓글 보기';
            btnToggle.setAttribute('data-toggle', 'collapse');
        }
    });
    
    // 댓글 [등록] 버튼을 찾아서, 클릭 이벤트 리스너를 설정:
    const btnRegisterQnAAnswers = document.querySelector('button#btnRegisterQnAAnswers');
    btnRegisterQnAAnswers.addEventListener('click', registerQnAAnswers);
    
    // 댓글 [더보기] 버튼을 찾아서, 클릭 이벤트 리스너를 설정:
    const btnMoreQnAAnswers = document.querySelector('button#btnMoreQnAAnswers');
    btnMoreQnAAnswers.addEventListener('click', () => getAllQnAAnswers(currentPageNo + 1));
    
    
    //----- 함수 정의(선언) -----
    function registerQnAAnswers() {
        // 댓글이 달린 포스트의 아이디
        const id = document.querySelector('input#id').value;
        // 댓글 내용
        const contents = document.querySelector('textarea#QnAAnswersText').value;
        // 댓글 작성자
        const userId = document.querySelector('input#QnAAnswersUserId').value;
        
        if (contents.trim() === '') {
            alert('댓글 내용을 입력하세요.');
            return;
        }
        
        // Ajax 요청에서 보낼 데이터
        const data = { id, contents, userId };
        
        // Ajax POST 방식 요청을 보냄고, 응답/에러 처리 콜백 등록.
        axios.post('/api/qnAAnswers', data)
            .then((response) => {
                console.log(response.data);
                alert('댓글 등록 성공!');
                
                // 댓글 내용 입력란을 비움.
                document.querySelector('textarea#QnAAnswersText').value = '';
                
                // 댓글 목록 갱신
                getAllQnAAnswers(0);
            })
            .catch((error) => console.log(error));
    }

    function getAllQnAAnswers(pageNo) {
        // 댓글들이 달린 포스트 아이디:
        const id = document.querySelector('input#id').value;
        
        // Ajax 요청을 보낼 주소:
        // path variable: 댓글이 달린 포스트 아이디. request param: 페이지 번호.
        const uri = `/api/qnAAnswers/all/${id}?p=${pageNo}`;
		console.log("Requesting URI:", uri);  // 요청 경로 출력
        
        // Ajax 요청을 보내고, 성공/실패 콜백 설정:
        axios.get(uri)
            .then((response) => {
                console.log(response);
                currentPageNo = response.data.number;
                
                // 현재 페이지 번호보다 페이지 개수가 더 많으면 댓글 [더보기] 버튼을 보여줌.
                const divBtnMore = document.querySelector('div#divBtnMore');
//                if (currentPageNo + 1 < response.data.totalPages) {
                if (!response.data.last) {
                    divBtnMore.classList.remove('d-none');
                } else {
                    divBtnMore.classList.add('d-none');
                }
                
                makeQnAAnswersElements(response.data.contents, response.data.number);
            })
            .catch((error) => console.log(error));
    }

    function makeQnAAnswersElements(data, pageNo) {
        // 로그인 사용자 정보 -> 댓글 삭제/업데이트 버튼을 만들 지 여부를 결정.
        const authUser = document.querySelector('span#authenticatedUser').innerText;
        // console.log(`authUser = ${authUser}`);
        
        // 댓글 목록을 추가할 div 요소
        const divQnAAnswers = document.querySelector('div#divQnAAnswers');
        
        let htmlStr = ''; // div에 삽입할 html 코드(댓글 목록)
        for (let qnaanswers of data) {
            // console.log(comment);
            htmlStr += `
            <div class="card card-body mt-2">
                <div class="mt-2">
                    <span class="fw-bold">${qnaanswers.userId}</span>
                    <span class="text-secondary">${qnaanswers.modifiedTime}</span>
                </div>
                <div class="mt-2">
                    <div class="mt-2">
                        <textarea class="commentText form-control" data-id="${qnaanswers.id}">${qnaanswers.contents}</textarea>
                    </div>
            `;
            // 로그인 사용자와 댓글 작성자가 같은 경우에만 삭제/업데이트 버튼을 만듦.
            if (authUser === qnaanswers.userId) {
                htmlStr += `
                    <div class="mt-2">
                            <button class="btnDelete btn btn-outline-danger btn-sm" data-id="${qnaanswers.id}">삭제</button>
                            <button class="btnUpdate btn btn-outline-primary btn-sm" data-id="${qnaanswers.id}">수정</button>
                    </div>
                </div>
            </div>
            `;
            } else {
                htmlStr += `
                    </div>
                </div>
                `;
            }
        }
        
        if (pageNo === 0) {
            // 댓글 목록 첫번째 페이지이면, 기존 내용을 다 지우고 새로 작성.
            divQnAAnswers.innerHTML = htmlStr;
        } else {
            // 댓글 목록에서 첫번째 페이지가 아니면, 기존 내용 밑에 추가(append)
            divQnAAnswers.innerHTML += htmlStr;
        }
        
        // 댓글 [삭제], [수정] 버튼들의 이벤트 리스너는 버튼들이 생겨난 이후에 등록!!
        // 모든 button.btnDelete 버튼들을 찾아서 클릭 이벤트 리스너를 등록.
        const btnDeletes = document.querySelectorAll('button.btnDelete');
        btnDeletes.forEach((btn) => {
            btn.addEventListener('click', deleteQnAAnswers);
        });
        
        const btnUpdates = document.querySelectorAll('button.btnUpdate');
        btnUpdates.forEach((btn) => {
            btn.addEventListener('click', updateQnAAnswers);
        });
        
    }
    
    function deleteQnAAnswers(event) {
//        console.log(event);
//        console.log(event.target);
        if (!confirm('정말 삭제할까요?')) {
            return;
        }
        
        const id = event.target.getAttribute('data-id'); // 삭제할 댓글 아이디
        const uri = `/api/qnAAnswers/${id}`; // 삭제 Ajax 요청을 보낼 주소
        axios.delete(uri)
            .then((response) => {
                console.log(response);
                alert(`댓글 #${id} 삭제 성공`);
                getAllQnAAnswers(0); // 댓글 목록 갱신
            })
            .catch((error) => console.log(error));
    }

    function updateQnAAnswers(event) {
//        console.log(event.target);
        const id = event.target.getAttribute('data-id'); // 업데이트할 댓글 아이디
        
        const textarea = document.querySelector(`textarea.QnAAnswersText[data-id="${id}"]`);
//        console.log(textarea);

        const contents = textarea.value; // 업데이트할 댓글 내용
        if (contents.trim() === '') {
            alert('댓글 내용은 반드시 입력해야 합니다.');
            return;
        }
        
        if (!confirm('변경된 댓글을 저장할까요?')) {
            return;
        }
        
        const uri = `/api/qnAAnswers/${id}`; // Ajax 요청을 보낼 주소
        const data = { id, contents }; // 업데이트 요청 데이터. {id: id, ctext: ctext}
        axios.put(uri, data)
            .then((response) => {
                console.log(response);
                alert(`댓글 #${id} 업데이트 성공!`);
                getAllQnAAnswers(0); // 댓글 목록 갱신
            })
            .catch((error) => console.log(error));
    }

});