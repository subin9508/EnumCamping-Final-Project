window.onload = function() {
    console.log("Window onload 이벤트가 발생했습니다.");
	setTimeout(initQnaAnswers, 100);  // 요소가 렌더링된 후 실행
	getAllQnaAnswers(); // 페이지 로드 시 댓글 목록 불러오기
	// initQnaAnswers();
};

function initQnaAnswers() {
	console.log('signedInUser:', signedInUser);
	console.log('userRoleRaw:', userRoleRaw);
	
    // let userRole = [];
	// let userRole = userRoleRaw;
	
	// userRoleRaw가 올바르게 전달되었는지 확인합니다.
	let userRole = Array.isArray(userRoleRaw) ? userRoleRaw : [];

	
    // userRoleRaw는 이미 객체 배열 형태로 전달되므로 JSON 파싱이 필요 없습니다.
    // userRole = userRoleRaw;

    console.log('signedInUser:', signedInUser);
    console.log('userRole:', userRole);

/*    if (Array.isArray(userRole) && userRole.some(role => role.authority === 'ROLE_ADMIN')) {
        console.log('관리자 권한 확인됨.');
        displayQnaAnswersInput();
    } else {
        console.error('관리자 권한이 아닙니다. 버튼이 숨겨집니다.');
        const btnRegisterQnaAnswers = document.getElementById('btnRegisterQnaAnswers');
        if (btnRegisterQnaAnswers) {
            btnRegisterQnaAnswers.style.display = 'none';
        }
    }*/
	
	// 배열 내부의 객체들을 개별적으로 출력
	userRole.forEach((role, index) => {
	    console.log(`userRole[${index}]:`, role);
	});
	
	    if (userRole.some(role => role.authority  === 'ROLE_ADMIN')) {
	        console.log('관리자 권한 확인됨.');
	        displayQnaAnswersInput();
	    } else {
	        console.error('관리자 권한이 아닙니다. 버튼이 숨겨집니다.');
	        hideQnaAnswersInput();
	    }
	}


function displayQnaAnswersInput() {
    const cardContainer = document.getElementById('qnaAnswerCard');
	console.log('카드 컨테이너 요소:', cardContainer);
	
    if (cardContainer) {
        cardContainer.style.display = 'block'; // 보이도록 설정
        console.log('댓글 입력창과 등록 버튼이 보이도록 설정되었습니다.');
		
		// 버튼 클릭 이벤트 등록
		const btnRegisterQnaAnswers = document.getElementById('btnRegisterQnaAnswers');
		if (btnRegisterQnaAnswers) {
		    btnRegisterQnaAnswers.addEventListener('click', registerQnaAnswers);
		    console.log('등록 버튼에 클릭 이벤트가 등록되었습니다.');
		} else {
		    console.error('등록 버튼을 찾을 수 없습니다.');
			}
			    } else {
			        console.error('카드 컨테이너를 찾을 수 없습니다.');
			    }
}

function hideQnaAnswersInput() {
    const cardContainer = document.getElementById('qnaAnswerCard');
    if (cardContainer) {
        cardContainer.style.display = 'none'; // 숨기도록 설정
    }
}

/*function displayQnaAnswersInput() {
    const cardContainer = document.querySelector('.card-body');
    if (cardContainer) {
        cardContainer.style.display = 'block'; // 보이도록 설정
        console.log('댓글 입력창과 등록 버튼이 보이도록 설정되었습니다.');
    } else {
        console.error('카드 컨테이너를 찾을 수 없습니다. 요소가 아직 로드되지 않았을 수 있습니다.');
    }
}*/

/*function displayQnaAnswersInput() {
    const qnaInputContainer = document.querySelector('.card-body');
    if (qnaInputContainer) {
        qnaInputContainer.style.display = 'block'; // 보이도록 설정
        console.log('댓글 입력창과 등록 버튼이 보이도록 설정되었습니다.');
    } else {
        console.error('qnaInputContainer를 찾을 수 없습니다.');
    }
}*/

/*function observeBtnRegister() {
    const observer = new MutationObserver((mutations, observer) => {
        const btnRegisterQnaAnswers = document.getElementById('btnRegisterQnaAnswers');
        if (btnRegisterQnaAnswers) {
            console.log('btnRegisterQnaAnswers 버튼이 생성되었습니다.');
            observer.disconnect(); // 더 이상 감시하지 않음
            btnRegisterQnaAnswers.addEventListener('click', registerQnaAnswers);
        }
    });

    observer.observe(document.body, { childList: true, subtree: true });
}*/



function getAllQnaAnswers(qnaPostId) {
    const id = qnaPostId || document.querySelector('input#id').value;
    const uri = `/enumcamping/api/qnaAnswers/all/${id}`;
    console.log("Requesting URI:", uri);

	    axios.get(uri)
	        .then((response) => {
	            console.log(response); // 전체 응답을 로그로 확인
	            if (Array.isArray(response.data)) {
	                makeQnaAnswersElements(response.data);
	            } else {
	                console.error('서버로부터 배열이 아닌 데이터가 반환되었습니다:', response.data);
	            }
	        })
			.catch((error) => {
			            if (error.response && error.response.status === 403) {
			                console.log('비밀글입니다. 권한이 없으면 목록을 볼 수 없습니다.');
			            } else {
			                console.log('댓글 목록 가져오기 중 오류 발생:', error);
			            }
			        });
	}

function makeQnaAnswersElements(data) {
/*    const authUserElement = document.querySelector('span#authenticatedUser');
	let authUser = '';  // authUser 변수를 함수 내에서 선언하고 초기화
	
	if (authUserElement) {
	    authUser = authUserElement.innerText;  // authUser 값을 설정
	    console.log(`authUser = ${authUser}`);
	} else {
	    console.error('authenticatedUser 요소를 찾을 수 없습니다.');
	    return;  // 요소가 없으면 함수 실행 중단
	}
	
	const divQnaAnswers = document.querySelector('div#divQnaAnswers');
    
	if (!Array.isArray(data)) {
	    console.error('입력된 data는 배열이 아닙니다.');
	    return;
	}*/
	
	const authUserElement = document.querySelector('span#authenticatedUser');
	const authUser = authUserElement ? authUserElement.innerText : null;

	console.log(`authUser = ${authUser}`);


	const divQnaAnswers = document.querySelector('div#divQnaAnswers');
	if (divQnaAnswers) {
	    console.log('div#divQnaAnswers 요소가 존재합니다.');
	} else {
	    console.error('div#divQnaAnswers 요소를 찾을 수 없습니다.');
	}
	
    let htmlStr = '';
    for (let qnaanswers of data) {
        htmlStr += `
        <div class="card card-body mt-2">
            <div class="mt-2">
                <span class="fw-bold">${qnaanswers.userId}</span>
                <span class="text-secondary">${qnaanswers.modifiedTime}</span>
            </div>
            <div class="mt-2">
                <textarea class="qnaAnswersText form-control" data-id="${qnaanswers.id}">${qnaanswers.contents}</textarea>
            </div>
        `;
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
    
    divQnaAnswers.innerHTML = htmlStr;
    
    const btnDeletes = document.querySelectorAll('button.btnDelete');
    btnDeletes.forEach((btn) => {
        btn.addEventListener('click', deleteQnaAnswers);
    });

    const btnUpdates = document.querySelectorAll('button.btnUpdate');
    btnUpdates.forEach((btn) => {
        btn.addEventListener('click', updateQnaAnswers);
    });
}


function registerQnaAnswers() {
    const qnaPostId = document.querySelector('input#id').value;
    const contents = document.querySelector('textarea#qnaAnswersText').value;
    const userId = document.querySelector('input#qnaAnswersUserId').value;

    if (contents.trim() === '') {
        alert('댓글 내용을 입력하세요.');
        return;
    }

    const data = { qnaPostId, contents, userId };

    axios.post('/enumcamping/api/qnaAnswers', data)
        .then((response) => {
            console.log(response.data);
            alert('댓글 등록 성공!');
            document.querySelector('textarea#qnaAnswersText').value = '';
            getAllQnaAnswers(qnaPostId);
        })
        .catch((error) => {
            console.error('댓글 등록 중 오류 발생:', error);
            console.error('Failed Request:', error.config);
        });
}

function deleteQnaAnswers(event) {
    if (!confirm('정말 삭제할까요?')) {
        return;
    }

    const id = event.target.getAttribute('data-id');
    const uri = `/enumcamping/api/qnaAnswers/${id}`;
    axios.delete(uri)
        .then((response) => {
            console.log(response);
            alert(`댓글 #${id} 삭제 성공`);
            const qnaPostId = document.querySelector('input#id').value;
            getAllQnaAnswers(qnaPostId);
        })
        .catch((error) => console.log('댓글 삭제 중 오류 발생:', error));
}

function updateQnaAnswers(event) {
    const id = event.target.getAttribute('data-id');
    const textarea = document.querySelector(`textarea.qnaAnswersText[data-id="${id}"]`);

    const contents = textarea.value;
    if (contents.trim() === '') {
        alert('댓글 내용은 반드시 입력해야 합니다.');
        return;
    }

    if (!confirm('변경된 댓글을 저장할까요?')) {
        return;
    }

    const uri = `/api/qnaAnswers/${id}`;
    const data = { id, contents };
    axios.put(uri, data)
        .then((response) => {
            console.log(response);
            alert(`댓글 #${id} 업데이트 성공!`);
            getAllQnaAnswers(qnaPostId);
        })
        .catch((error) => console.log('댓글 업데이트 중 오류 발생:', error));
}
