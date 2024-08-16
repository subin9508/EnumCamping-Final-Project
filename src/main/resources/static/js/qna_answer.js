document.addEventListener('DOMContentLoaded', () => {
    const btnRegisterAnswer = document.querySelector('#btnRegisterAnswer');
    
    if (btnRegisterAnswer) {
        btnRegisterAnswer.addEventListener('click', registerAnswer);
    }

    // 답변 등록 함수
    function registerAnswer() {
        // 관리자 권한 확인
        if (parseInt(userRole) !== 0) {
            alert('답변 등록은 관리자만 가능합니다.');
            return;
        }

        const id = document.querySelector('#id').value;
        const content = document.querySelector('#answerContent').value.trim();

        if (content === '') {
            alert('답변 내용을 입력하세요.');
            return;
        }

        // 서버로 답변 등록 요청
        axios.post('/api/QnAAnswer', { id, content, userId: signedInUser })
            .then(response => {
                if (response.status === 200) {
                    alert('답변이 등록되었습니다.');
                    document.querySelector('#answerContent').value = '';
                    getAllAnswers(); // 답변 목록 갱신
                }
            })
            .catch(error => {
                console.error('Error registering answer:', error);
            });
    }

    // 모든 답변을 가져오는 함수
    function getAllAnswers() {
        const id = document.querySelector('#id').value;

        axios.get(`/api/QnAAnswer/all/${id}`)
            .then(response => {
                makeAnswerElements(response.data.content); // response.data.content로 Page에서 데이터 추출
            })
            .catch(error => {
                console.error('Error fetching answers:', error);
            });
    }

    // 답변을 HTML로 생성하는 함수
    function makeAnswerElements(data) {
        const divAnswers = document.querySelector('#answersContainer');
        divAnswers.innerHTML = data.map(answer => `
            <div class="card card-body my-1">
                <div style="font-size: 0.825rem;">
                    <span>${answer.id}</span>
                    <span class="fw-bold">${answer.userId}</span>
                    <span class="text-secondary">${new Date(answer.modifiedTime).toLocaleString()}</span>
                </div>
                <div>${answer.content}</div>
                <div>
                    <button class="btn btn-sm btn-danger" onclick="deleteAnswer(${answer.id})">삭제</button>
                    <button class="btn btn-sm btn-warning" onclick="showAnswerModal(${answer.id})">수정</button>
                </div>
            </div>
        `).join('');
    }

    // 페이지가 로드될 때 모든 답변을 가져옴
    getAllAnswers();
});