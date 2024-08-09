 /**
  *  /reservation/calendar.jsp에 포함
  */
var selectedDate = null;
var selectedArea= null;
var selectedNight = null;
// 전역 변수 추가
var finalYear, finalMonth, finalDay, finalItemId, finalSelectedNight;
 
 document.addEventListener("DOMContentLoaded", function() {
    selectedDate = null;
    selectedNight = null;
        buildCalendar();
        
        document.getElementById("btnPrevCalendar").addEventListener("click", function(event) {
            prevCalendar();
        });
        
        document.getElementById("nextNextCalendar").addEventListener("click", function(event) {
            nextCalendar();
        });
        
        addAreaRadioEventListeners();
        addNextPageEventListeners();
});

var toDay = new Date(); // @param 전역 변수, 오늘 날짜 / 내 컴퓨터 로컬을 기준으로 toDay에 Date 객체를 넣어줌
var nowDate = new Date();  // @param 전역 변수, 실제 오늘날짜 고정값

    function prevCalendar() {
        this.toDay = new Date(toDay.getFullYear(), toDay.getMonth() - 1, toDay.getDate());
        buildCalendar();    // @param 전월 캘린더 출력 요청
    }


    function nextCalendar() {
        this.toDay = new Date(toDay.getFullYear(), toDay.getMonth() + 1, toDay.getDate());
        buildCalendar();    // @param 명월 캘린더 출력 요청
    }

    console.log('Initial selectedDate:', selectedDate);

    function buildCalendar() {

        let doMonth = new Date(toDay.getFullYear(), toDay.getMonth(), 1);
        let lastDate = new Date(toDay.getFullYear(), toDay.getMonth() + 1, 0);

        let tbCalendar = document.querySelector(".scriptCalendar > tbody");

        document.getElementById("calYear").innerText = toDay.getFullYear();                       // @param YYYY월
        document.getElementById("calMonth").innerText = autoLeftPad((toDay.getMonth() + 1), 2);   // @param MM월
        

        // @details 이전 캘린더의 출력결과가 남아있다면, 이전 캘린더를 삭제한다.
        while(tbCalendar.rows.length > 0) {
            tbCalendar.deleteRow(tbCalendar.rows.length - 1);
        }

        // @param 첫번째 개행
        let row = tbCalendar.insertRow();

        // @param 날짜가 표기될 열의 증가값
        let dom = 1;

        // @details 시작일의 요일값( doMonth.getDay() ) + 해당월의 전체일( lastDate.getDate())을  더해준 값에서
        //               7로 나눈값을 올림( Math.ceil() )하고 다시 시작일의 요일값( doMonth.getDay() )을 빼준다.
        let daysLength = (Math.ceil((doMonth.getDay() + lastDate.getDate()) / 7) * 7) - doMonth.getDay();

        // @param 달력 출력
        // @details 시작값은 1일을 직접 지정하고 요일값( doMonth.getDay() )를 빼서 마이너스( - )로 for문을 시작한다.
        for(let day = 1 - doMonth.getDay(); daysLength >= day; day++) {

            let column = row.insertCell();

            // @param 평일( 전월일과 익월일의 데이터 제외 )
            if(Math.sign(day) == 1 && lastDate.getDate() >= day) {

                // @param 평일 날짜 데이터 삽입
                column.innerText = autoLeftPad(day, 2);

                // @param 일요일인 경우
                if(dom % 7 == 1) {
                    column.style.color = "#FF4D4D";
                }

                // @param 토요일인 경우
                if(dom % 7 == 0) {
                    column.style.color = "#4D4DFF";
                    row = tbCalendar.insertRow();   // @param 토요일이 지나면 다시 가로 행을 한줄 추가한다.
                }

            }

            // @param 평일 전월일과 익월일의 데이터 날짜변경
            else {
                let exceptDay = new Date(doMonth.getFullYear(), doMonth.getMonth(), day);
                column.innerText = autoLeftPad(exceptDay.getDate(), 2);
                column.style.color = "#A9A9A9";
            }

            // @brief   전월, 명월 음영처리
            // @details 현재년과 선택 년도가 같은경우
            if(toDay.getFullYear() == nowDate.getFullYear()) {

                // @details 현재월과 선택월이 같은경우
                if(toDay.getMonth() == nowDate.getMonth()) {

                    // @details 현재일보다 이전인 경우이면서 현재월에 포함되는 일인경우
                    if(nowDate.getDate() > day && Math.sign(day) == 1) {
                        column.style.backgroundColor = "#F6F7F8";
                    }

                    // @details 현재일보다 이후이면서 현재월에 포함되는 일인경우
                    else if(nowDate.getDate() < day && lastDate.getDate() >= day) {
                        column.style.backgroundColor = "#FFFFFF";
                        column.style.cursor = "pointer";
                        column.onclick = function(){ calendarChoiceDay(this); }
                    }

                    // @details 현재일인 경우
                    else if(nowDate.getDate() == day) {
                        column.style.backgroundColor = "#FFFFE6";
                        column.style.cursor = "pointer";
                        column.onclick = function(){ calendarChoiceDay(this); }
                    }

                // @details 현재월보다 이전인경우
                } else if(toDay.getMonth() < nowDate.getMonth()) {
                    if(Math.sign(day) == 1 && day <= lastDate.getDate()) {
                        column.style.backgroundColor = "#E5E5E5";
                    }
                }

                // @details 현재월보다 이후인경우
                else {
                    if (toDay.getMonth() > nowDate.getMonth()+2){
                        if(Math.sign(day) == 1 && day <= lastDate.getDate()) {
                        column.style.backgroundColor = "#E5E5E5";
                        }
                    } else {
                        if(Math.sign(day) == 1 && day <= lastDate.getDate()) {
                            column.style.backgroundColor = "#FFFFFF";
                            column.style.cursor = "pointer";
                            column.onclick = function(){ calendarChoiceDay(this); }
                        }
                        
                    }
                }
            }

            // @details 선택한년도가 현재년도보다 작은경우
            else if(toDay.getFullYear() < nowDate.getFullYear()) {
                if(Math.sign(day) == 1 && day <= lastDate.getDate()) {
                    column.style.backgroundColor = "#E5E5E5";
                }
            }

            // @details 선택한년도가 현재년도보다 큰경우
            else {
                if(Math.sign(day) == 1 && day <= lastDate.getDate()) {
                    column.style.backgroundColor = "#FFFFFF";
                    column.style.cursor = "pointer";
                    column.onclick = function(){ calendarChoiceDay(this); }
                }
            }
            dom++;
        }
        console.log('buildCalendar - current selectedDate:', selectedDate);
    }

    /**
     * @brief   날짜 선택
     * @details 사용자가 선택한 날짜에 체크표시를 남긴다.
     */
    function calendarChoiceDay(column) {

        // @param 기존 선택일이 존재하는 경우 기존 선택일의 표시형식을 초기화 한다.
        if(document.getElementsByClassName("choiceDay")[0]) {
            
            // @see 금일인 경우
            if(document.getElementById("calMonth").innerText == autoLeftPad((nowDate.getMonth() + 1), 2) 
                && document.getElementsByClassName("choiceDay")[0].innerText == autoLeftPad(toDay.getDate(), 2)) {
                document.getElementsByClassName("choiceDay")[0].style.backgroundColor = "#FFFFE6";  //오늘날짜
            }
            
            // @see 금일이 아닌 경우
            else {
                document.getElementsByClassName("choiceDay")[0].style.backgroundColor = "#FFFFFF";
            }
            document.getElementsByClassName("choiceDay")[0].classList.remove("choiceDay");
        }

        // @param 선택일 체크 표시
        column.style.backgroundColor = "#A4C392";

        // @param 선택일 클래스명 변경
        column.classList.add("choiceDay");
        
        // let selectedDate = document.getElementById("calMonth").innerText +"월"+ document.getElementsByClassName("choiceDay")[0].innerHTML+"일";
        document.getElementById('date').innerText = document.getElementById("calMonth").innerText +"월"+ document.getElementsByClassName("choiceDay")[0].innerHTML+"일";
        
        // @details 선택한 날짜에 대한 예약 정보 가져오기
        const year = document.getElementById("calYear").innerText;
        const month = document.getElementById("calMonth").innerText;
        const day = column.innerText;
        selectedDate = `${year}-${autoLeftPad(month, 2)}-${autoLeftPad(day, 2)}`;
        selectedNight = null;
        console.log('calendarChoiceDay - selectedDate=', selectedDate);
        
        // night 라디오 버튼 숨기기
        const nightCard = document.getElementById('night-card');
        nightCard.style.display = 'none';
        
        // 선택한 날짜에 대한 예약 정보 가져오기
        getReservations(year, month, day);
        
        // area 라디오 버튼 표시
        const areaCards = document.querySelectorAll('.area-card');
        areaCards.forEach(card => {
            card.style.display = 'block';
        });
        
        // 모든 라디오 버튼 체크 해제
        const radios = document.querySelectorAll('.area-radio');
        radios.forEach(radio => {
            radio.checked = false;
        });
        
        selectedArea = null;
        
        // 아이템 테이블 숨기기
        const itemsTable = document.getElementById('items-table');
        itemsTable.style.display = 'none';
        
        
        // 두 가지 조건이 모두 만족되었는지 확인하여 함수 호출
        if (selectedDate && selectedArea) {
            getReservationNight(year, month, day, selectedArea);
        }
        
        // 가격 정보 지움
        const priceValue = document.getElementById('price-value');
        priceValue.innerText = '';
        
    }
    
    // date정보 uri로 전송
    function getReservations(year, month, day) {
        const date = `${year}-${month}-${day}`
        const uri = `../reservation/calendar/${date}`;
        
        axios.get(uri)
            .then(response => {
                console.log(response.data);
                const reservedAreas = response.data || [];
                updateRadioButtons(reservedAreas);
            })
            .catch(error => {
                console.error("There was an error fetching the reservations!", error);
            });
    }
    
    
    // 예약된 날짜 있으면 해당 구역 display = none;
    function updateRadioButtons(reservedAreas) {
        const totalAreas = 20; // 총 구역 수
        console.log(reservedAreas);
        
        for (let i = 1; i <= totalAreas; i++) {
            const areaIndex = Math.ceil(i / 4); // 각 구역의 인덱스 계산
            console.log(`area${areaIndex} 처리 시작`)
            const area = document.getElementById(`area${areaIndex}_radio`);
            console.log(`area${areaIndex}`, area);

            if (area) {
                const card = document.getElementById(`area${areaIndex}`);
                
                // 현재 areaIndex에 속하는 모든 구역 번호
                const areaNumbers = Array.from({ length: 4 }, (_, k) => (areaIndex - 1) * 4 + k + 1);
                console.log(`areaNumbers=${areaNumbers}`, area)
            
                // 예약된 구역이 하나라도 포함되어 있는지 확인
                const isReserved = areaNumbers.some(num => reservedAreas.includes(num));
                
                if (isReserved) {
                    console.log(`Area ${areaIndex} is reserved`);
                    card.style.display = "none";
                } else {
                    card.style.display = "block";
                }
            }
        }
    }
    
    
    // 구역 클릭 시 nightCard 뜨게 
    function addAreaRadioEventListeners() {
        const radios = document.querySelectorAll('.area-radio');
        radios.forEach(radio => {
            radio.addEventListener('change', function() {
                
                if (this.checked) {
                    selectedArea = this.value;
                    console.log('addAreaRadioEventListeners - selectedArea=', selectedArea);
                    console.log('addAreaRadioEventListeners - selectedDate=', selectedDate);
                    
                    const nightCard = document.getElementById('night-card');
                    nightCard.style.display = 'block';
                    
                    // 모든 라디오 버튼 체크 해제
                    const nightRadio = document.querySelectorAll('.night-radio');
                    nightRadio.forEach(radio => {
                        radio.checked = false;
                    });
                    
                    // 가격 정보 지움
                    const priceValue = document.getElementById('price-value');
                    priceValue.innerText = '';
                    
                    // 두 가지 조건이 모두 만족되었는지 확인하여 함수 호출
                    if (selectedDate && selectedArea) {
                        const year = document.getElementById("calYear").innerText;
                        const month = autoLeftPad(document.getElementById("calMonth").innerText, 2);
                        const day = autoLeftPad(document.getElementsByClassName("choiceDay")[0].innerText, 2);
                        console.log('year, month, day, selectedArea:', year, month, day, selectedArea);  // 로그 추가
                        getReservationNight(year, month, day, selectedArea);
                    }
                    
                    addNightRadioEventListeners();
                }
            });
        });
        
        
    }
    
    // 선택된 date, area 정보 uri로 전송
    function getReservationNight(year, month, day, selectedArea) {
        const date = `${year}-${month}-${day}`;
        const uri = `../reservation/calendar/${date}/${selectedArea}`;

        console.log('getReservationNight()', uri);

        axios.get(uri)
            .then(response => {
                console.log(response.data);
                const checkedDateAndArea = response.data || [];
                console.log(checkedDateAndArea);
                
                const nightCard = document.getElementById('night-card');
                const nightRadioLabel = document.getElementById('night-radio_label');
                
                // nigthCard 초기화
                nightRadioLabel.innerHTML = '';
                
                if (checkedDateAndArea.length === 0) {
                    // 다음날 예약이 없는 경우 1박 2박 옵션 모두 표시
                    nightRadioLabel.innerHTML = `
                        <h3> <strong>이용 기간</strong> </h3>
                        <br />
                        <div class="radio-options">
                        <label style="margin-right: 30px;">
                            <input class="night-radio" type="radio" name="night" value="1">
                            <span class="night-radio_text" style="font-size: 1.3em;">1박 2일</span>
                        </label>
                        <label>
                            <input class="night-radio" type="radio" name="night" value="2">
                            <span class="night-radio_text" style="font-size: 1.3em;">2박 3일</span>
                        </label>
                        </div>
                    `;
                } else {
                    // 다음날 예약이 있는 경우 1박 옵션만 표시
                    nightRadioLabel.innerHTML = `
                        <h3> <strong>체류기간<strong> </h3>
                        <div class="radio-options">
                            <label>
                                <input class="night-radio" type="radio" name="night" value="1">
                                <span class="night-radio_text">1박 2일</span>
                            </label>
                        </div>
                    `;
                }
                
                // nightCard 표시
                nightCard.style.display = 'block';
                addNightRadioEventListeners();
            })
            .catch(error => {
                console.error("There was an error fetching the reservations!", error);
            });
    }
    
    // handleNextPageClick에서 axios 전 체크사항
    function validateForm(event) {
        var dateSelected = document.getElementById('date').innerText.trim() !== "";
        var areaSelected = document.querySelector('input[name="area"]:checked') !== null;
        var nightSelected = document.querySelector('input[name="night"]:checked') !== null;
        
        if (!dateSelected || !areaSelected || !nightSelected) {
            alert("날짜, 구역, 숙박 일수를 선택해 주세요.");
            return false;
        }
        
        return true;

    }
    
    // 구역 가격 업데이트
    function updatePrice(year, month, day, selectedArea, selectedNight) {
        const date = `${year}-${month}-${day}`;
        const selectedDateObj = new Date(year, month - 1, day);
        const isWeekend = (selectedDateObj.getDay() === 0 || selectedDateObj.getDay() === 6); // 0: Sunday, 6: Saturday
        
        // 성수기 기간 설정
        const startPeakSeason = new Date(year, 6, 1); // 7월 1일 (월은 0부터 시작하므로 6은 7월을 의미)
        const endPeakSeason = new Date(year, 7, 31); // 8월 31일

        // 성수기 여부 결정
        const isPeakSeason = selectedDateObj >= startPeakSeason && selectedDateObj <= endPeakSeason;
        const seasonFactor = isPeakSeason ? 2 : 0; // 성수기면 2, 비수기면 0

        const weekendFactor = isWeekend ? 1 : 0; // 주말이면 1, 평일이면 0

        const baseItemId = (selectedArea - 1) * 4;
        const itemId = baseItemId + seasonFactor + weekendFactor + 1;
        
        const uri = `../reservation/itemPrice/${itemId}`;

        console.log('updatePrice()', uri);

        axios.get(uri)
            .then(response => {
                const price = (response.data) * selectedNight;
                document.getElementById('price-value').innerText = price;
                updateTotalAllItems();
                
                // 전역 변수 업데이트
            finalYear = year;
            finalMonth = month;
            finalDay = day;
            finalItemId = itemId;
            finalSelectedNight = selectedNight;
            
            })
            .catch(error => {
                console.error("There was an error fetching the price!", error);
            });
    }
    
    // 체크아웃 날짜 계산
    function calculateCheckOutDate(checkInDate, nights) {
        
        if (!checkInDate || isNaN(Date.parse(checkInDate))) {
            throw new RangeError("Invalid check-in date");
        }
        const date = new Date(checkInDate);
        date.setDate(date.getDate() + parseInt(nights));
        return date.toISOString().split('T')[0];
    }
    
	var selectedItems = [];

    function updateQuantity(itemId, itemPrice) {
        var quantity = document.getElementById('quantity-' + itemId).value;
        console.log('quantity= ', quantity);
        var totalPrice = quantity * itemPrice;
		
        // document.getElementById('total-' + itemId).textContent = totalPrice + '원';

        var selectedItem = {
            itemId: itemId,
            itemQuantity: quantity,
            itemAmount: totalPrice
        };

        var existingIndex = selectedItems.findIndex(item => item.itemId === itemId);
        if (existingIndex !== -1) {
            if (quantity > 0) {
                selectedItems[existingIndex] = selectedItem;
            } else {
                selectedItems.splice(existingIndex, 1);
            }
        } else {
            if (quantity > 0) {
                selectedItems.push(selectedItem);
            }
        }

        updateTotalAllItems();
    }

    function updateTotalAllItems() {
        // price 값을 숫자로 변환
        const priceText = document.getElementById('price-value').innerText;
        console.log('priceText:', priceText); // 로그 추가        
        const price = parseInt(priceText.replace(/[^0-9]/g, ''), 10) || 0; // 숫자만 추출하고 정수로 변환
        console.log('price:', price); // 로그 추가
        
        var total = selectedItems.reduce(function(sum, item) {
            return sum + item.itemAmount;
        }, price);
        console.log('total:', total); // 로그 추가
        
        var totalAllItemsElement = document.getElementById('totalAllItems');
        totalAllItemsElement.textContent = '전체 총 가격: ' + total + '원';
    }
    
    // night 라디오 버튼에 이벤트 리스너 추가
    function addNightRadioEventListeners() {
        console.log('addNightRadioEventListeners()');

        document.querySelectorAll('.night-radio').forEach(radio => {
            radio.addEventListener('change', function() {
                if (this.checked) {
                    console.log('night-radio checked');
                    const year = document.getElementById("calYear").innerText;
                    const month = autoLeftPad(document.getElementById("calMonth").innerText, 2);
                    const day = autoLeftPad(document.getElementsByClassName("choiceDay")[0].innerText, 2);
                    selectedNight = this.value;
                    const items = document.getElementById("items-table");
                    const totalAllItemsElement = document.getElementById('totalAllItems');
                    
                    console.log('selectedNight', selectedNight);

                    updatePrice(year, month, day, selectedArea, selectedNight);
                    
                    items.style.display = 'block';
                    totalAllItemsElement.style.display = 'block';
                }
            });
        });
    }
    
   // 예약하기 버튼에 이벤트 리스너 추가
    function handleNextPageClick(event) {
        console.log('handleNextPageClick');
     event.preventDefault();
	
	// 라디오박스 및 켈린더 체크 안되어 있는지 확인
    if (!validateForm()) {
        return;
    }

    console.log('Button clicked'); // 버튼 클릭 로그
    const date = `${finalYear}-${finalMonth}-${finalDay}`;
    const requirement = document.getElementById("special-requests").value;
    const reservationMaster = {
        resCheckIn: date,
        resCheckOut: calculateCheckOutDate(date, finalSelectedNight),
        resTotalPrice: parseInt(document.getElementById('totalAllItems').innerText.replace(/[^0-9]/g, '')),
        requirement: requirement || '요청없음'
    };
    console.log('reservationMaster: {}', reservationMaster);

    const mainReservationDetail = {
        itemId: finalItemId,
        itemQuantity: 1,
        itemAmount: document.getElementById('price-value').innerText
    };

    console.log('mainReservationDetail:', mainReservationDetail);

    const additionalItems = selectedItems.map(function(item) {
        return {
            itemId: parseInt(item.itemId),
            itemQuantity: parseInt(item.itemQuantity) || 0,
            itemAmount: parseInt(item.itemAmount)
        };
    });

    const reservationDetails = [mainReservationDetail, ...additionalItems];

    const data = {
        reservationMaster: reservationMaster,
        reservationDetail: reservationDetails
    };

    console.log('Data to be sent:', JSON.stringify(data, null, 2)); // 전송할 데이터 로그

    const uri = '../reservation/order';

    axios.post(uri, data, {
        headers: {
            'Content-Type': 'application/json'
        }
    })
    .then(response => {
        console.log('Response status:', response.status);
        console.log('Response data:', response.data);
        window.location.href = uri; // 페이지 리디렉션
    })
    .catch(error => {
        console.error('Error details:', error.response ? error.response.data : error.message);
        console.error('Error status:', error.response ? error.response.status : 'Unknown');
        alert('예약 처리 중 오류가 발생하였습니다.');
    });
}

// 다음페이지 버튼 이벤트리스너 추가
function addNextPageEventListeners() {
    console.log('addNextPageEventListeners()');

    const btnNextPage = document.querySelector('.btnNextPage');
    if (btnNextPage) { // 요소가 존재하는지 확인
    console.log(btnNextPage);
        //btnNextPage.removeEventListener('click', handleNextPageClick);
        btnNextPage.addEventListener('click', handleNextPageClick);
    } else {
        console.error("btnNextPage element not found");
    }
}

    

    /**
     * @brief   숫자 두자릿수( 00 ) 변경
     * @details 자릿수가 한자리인 ( 1, 2, 3등 )의 값을 10, 11, 12등과 같은 두자리수 형식으로 맞추기위해 0을 붙인다.
     * @param   num     앞에 0을 붙일 숫자 값
     * @param   digit   글자의 자릿수를 지정 ( 2자릿수인 경우 00, 3자릿수인 경우 000 … )
     */
    function autoLeftPad(num, digit) {
        if(String(num).length < digit) {
            num = new Array(digit - String(num).length + 1).join("0") + num;
        }
        return num;
    }