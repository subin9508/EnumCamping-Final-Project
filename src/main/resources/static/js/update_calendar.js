 /**
  *  /mypage/reservation_update.jsp에 포함
  */
var selectedDate = null;
var selectedArea= null;
var selectedNight = null;
// 전역 변수 추가
var finalYear, finalMonth, finalDay, finalItemId, finalSelectedNight;

 document.addEventListener("DOMContentLoaded", function() {
    selectedDate = null;
    selectedNight = null;
    
     // resCheckIn 및 resCheckOut 날짜 가져오기
     var resCheckInDate = new Date(document.getElementById('resCheckInDate').textContent.trim());
     var resCheckOutDate = new Date(document.getElementById('resCheckOutDate').textContent.trim());

     checkNightRadio(resCheckInDate, resCheckOutDate);
     
     if (resCheckInDate) {
         // resCheckIn 날짜로 toDay 설정
         toDay = new Date(resCheckInDate);
     } else {
         toDay = new Date(); // resCheckIn 날짜가 없으면 현재 날짜 사용
     }
        
    buildCalendar();
    
     // resCheckIn 날짜를 캘린더에 표시
     if (resCheckInDate && resCheckOutDate) {
         highlightResCheckInDate(resCheckInDate, resCheckOutDate);
     } else if (resCheckInDate) {
        highlightResCheckInDate(resCheckInDate);
    }

        
        document.getElementById("btnPrevCalendar").addEventListener("click", function(event) {
            prevCalendar();
        });
        
        document.getElementById("nextNextCalendar").addEventListener("click", function(event) {
            nextCalendar();
        });
        
        addAreaRadioEventListeners();
        addNextPageEventListeners();
                               
});

// 날짜 차이에 따른 라디오 버튼 자동 선택 함수
function checkNightRadio(resCheckInDate, resCheckOutDate) {
    var timeDiff = resCheckOutDate - resCheckInDate;
    var dayDiff = timeDiff / (1000 * 3600 * 24);

    if (dayDiff === 1) {
        document.getElementById("night1").checked = true;
    } else if (dayDiff === 2) {
        document.getElementById("night2").checked = true;
    }
}

function highlightResCheckInDate(dateString) {
    var date = new Date(dateString);
    var year = date.getFullYear();
    var month = date.getMonth() + 1;
    var day = date.getDate();
    
    // 현재 표시된 달력의 년도와 월
    var currentYear = parseInt(document.getElementById("calYear").innerText);
    var currentMonth = parseInt(document.getElementById("calMonth").innerText);
    
    // 만약 resCheckIn 날짜가 현재 표시된 달력의 월/년과 일치한다면
    if (year === currentYear && month === currentMonth) {
        var cells = document.querySelectorAll('.scriptCalendar td');
        cells.forEach(function(cell) {
            if (parseInt(cell.innerText) === day) {
                calendarChoiceDay(cell);
            }
        });
    }
}

// 날짜 구간을 하이라이트하는 함수 (체크인과 체크아웃 날짜 및 그 사이의 날짜)
function highlightRangeDates(checkInDate, checkOutDate) {
    var currentYear = parseInt(document.getElementById("calYear").innerText);
    var currentMonth = parseInt(document.getElementById("calMonth").innerText);

    var cells = document.querySelectorAll('.scriptCalendar td');
    cells.forEach(function (cell) {
        var cellDate = new Date(currentYear, currentMonth - 1, parseInt(cell.innerText));

        if (cellDate >= checkInDate && cellDate <= checkOutDate) {
            cell.style.backgroundColor = "#FFFFE6"; // 이 구간의 날짜를 하이라이트
        }

        if (cellDate.getTime() === checkInDate.getTime()) {
            calendarChoiceDay(cell); // 체크인 날짜
        } else if (cellDate.getTime() === checkOutDate.getTime()) {
            cell.style.backgroundColor = "#FFE6E6"; // 체크아웃 날짜
        }
    });
}

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

    document.getElementById("calYear").innerText = toDay.getFullYear();
    document.getElementById("calMonth").innerText = autoLeftPad((toDay.getMonth() + 1), 2);

    while(tbCalendar.rows.length > 0) {
        tbCalendar.deleteRow(tbCalendar.rows.length - 1);
    }

    let row = tbCalendar.insertRow();

    let dom = 1;

    let daysLength = (Math.ceil((doMonth.getDay() + lastDate.getDate()) / 7) * 7) - doMonth.getDay();

    // resCheckIn 날짜 가져오기
    let resCheckInDate = new Date(document.getElementById('resCheckInDate').textContent);
    
    // resCheckIn으로부터 14일 후의 날짜 계산
    let fourteenDaysLater = new Date(resCheckInDate);
    fourteenDaysLater.setDate(resCheckInDate.getDate() + 14);

    for(let day = 1 - doMonth.getDay(); daysLength >= day; day++) {
        let column = row.insertCell();

        if(Math.sign(day) == 1 && lastDate.getDate() >= day) {
            column.innerText = autoLeftPad(day, 2);

            if(dom % 7 == 1) {
                column.style.color = "#FF4D4D";
            }

            if(dom % 7 == 0) {
                column.style.color = "#4D4DFF";
                row = tbCalendar.insertRow();
            }

            let currentDay = new Date(toDay.getFullYear(), toDay.getMonth(), day);

            if (currentDay < nowDate && currentDay < resCheckInDate) {
                column.style.backgroundColor = "#E5E5E5";
                column.style.cursor = "default";
            } else if (currentDay > fourteenDaysLater) {
                column.style.backgroundColor = "#E5E5E5";
                column.style.cursor = "default";
            } else {
                column.style.backgroundColor = "#FFFFFF";
                column.style.cursor = "pointer";
                column.onclick = function() { calendarChoiceDay(this); }
            }

            // 오늘 날짜인 경우
            if (currentDay.toDateString() === nowDate.toDateString()) {
                column.style.backgroundColor = "#FFFFE6";
                column.style.cursor = "pointer";
                column.onclick = function() { calendarChoiceDay(this); }
            }

            // resCheckIn 날짜인 경우
            if (currentDay.toDateString() === resCheckInDate.toDateString()) {
                column.style.backgroundColor = "#A4C392";
                column.style.cursor = "pointer";
                column.onclick = function() { calendarChoiceDay(this); }
            }
        } else {
            let exceptDay = new Date(doMonth.getFullYear(), doMonth.getMonth(), day);
            column.innerText = "";
            column.style.color = "#A9A9A9";
        }

        dom++;
    }

    console.log('buildCalendar - current selectedDate:', selectedDate);
        
    // 달력 구성이 완료된 후 resCheckIn 및 resCheckOut 날짜 하이라이트
    var resCheckOutDate = new Date(document.getElementById('resCheckOutDate').textContent);

    if (resCheckInDate && resCheckOutDate) {
        highlightRangeDates(resCheckInDate, resCheckOutDate);
    } else if (resCheckInDate) {
        highlightResCheckInDate(resCheckInDate);
    }
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
        
        // 선택한 날짜에 대한 예약 정보 가져오기
        getReservations(year, month, day);
        
        // area 라디오 버튼 표시
        const areaCards = document.querySelectorAll('.area-card');
        areaCards.forEach(card => {
            card.style.display = 'block';
        });
        
        // 모든 라디오 버튼 체크 해제
        /*const radios = document.querySelectorAll('.area-radio');
        radios.forEach(radio => {
            radio.checked = false;
        });*/
        
        selectedArea = null;
        
        
        // 두 가지 조건이 모두 만족되었는지 확인하여 함수 호출
        if (selectedDate && selectedArea) {
            getReservationNight(year, month, day, selectedArea);
        }
        
        // 가격 정보 지움
//        const priceValue = document.getElementById('price-value');
//        priceValue.innerText = '';
        
    }
    
    // date정보 uri로 전송
    function getReservations(year, month, day) {
        const date = `${year}-${month}-${day}`
        const uri = `../reservation/calendar/${date}`;
        
        axios.get(uri)
            .then(response => {
                console.log(response.data);
                const reservedAreas = response.data || [];
                updateRadioButtons(year, month, day,reservedAreas);
            })
            .catch(error => {
                console.error("There was an error fetching the reservations!", error);
            });
    }
    
    
    // 예약된 날짜 있으면 해당 구역 display = none;
    function updateRadioButtons(year, month, day,reservedAreas) {
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
                    findPrice(year, month, day, areaIndex);
                }
            }
        }
    }
    
    // 구역 가격 업데이트
    function findPrice(year, month, day,areaIndex) {
        const selectedDateObj = new Date(year, month - 1, day);
        const isWeekend = (selectedDateObj.getDay() === 0 || selectedDateObj.getDay() === 6 || selectedDateObj.getDay() === 5 || selectedDateObj.getDay() === 5); // 0: Sunday, 6: Saturday, 5: Friday
    
        // 성수기 기간 설정
        const startPeakSeason = new Date(year, 6, 1); // 7월 1일 (월은 0부터 시작하므로 6은 7월을 의미)
        const endPeakSeason = new Date(year, 7, 31); // 8월 31일
    
        // 성수기 여부 결정
        const isPeakSeason = selectedDateObj >= startPeakSeason && selectedDateObj <= endPeakSeason;
        const seasonFactor = isPeakSeason ? 2 : 0; // 성수기면 2, 비수기면 0
    
        const weekendFactor = isWeekend ? 1 : 0; // 주말이면 1, 평일이면 0
    
        const itemId = (areaIndex - 1) * 4 + seasonFactor + weekendFactor + 1;
    
        const uri = `../reservation/itemPrice/${itemId}`;
    
        console.log('updatePrice()', uri);
    
        axios.get(uri)
            .then(response => {
                const price = (response.data.price) 
                document.getElementById(`price_${areaIndex}`).innerText = `${price}원`;
    
            })
            .catch(error => {
                console.error("There was an error fetching the price!", error);
            });
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
//                    const priceValue = document.getElementById('price-value');
//                    priceValue.innerText = '';
                    
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
        const isWeekend = (selectedDateObj.getDay() === 0 || selectedDateObj.getDay() === 6 || selectedDateObj.getDay() === 5); // 0: Sunday, 6: Saturday, 5: Friday
        
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
                const price = (response.data.price) * selectedNight;
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
    
    // 각 항목의 총 가격을 업데이트
    document.getElementById('total-' + itemId).textContent = totalPrice + '원';

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

     // 선택된 모든 아이템의 총 가격을 계산
     var total = selectedItems.reduce(function(sum, item) {
         return sum + item.itemAmount;
     }, 0);
     console.log('total:', total); // 로그 추가

     // 합계를 표시할 요소를 업데이트
     var totalSumElement = document.getElementById('totalSum');
     totalSumElement.textContent = total;

     // 전체 총 가격도 업데이트
     var totalAllItemsElement = document.getElementById('totalAllItems');
     totalAllItemsElement.textContent = '전체 총 가격: ' + (total + price) + '원';
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
	const resId = document.querySelector("input#resId").value; // 예약 ID 입력값 가져오기
	    console.log('resId=', resId);
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

    const uri = `../mypage/reservation_order?resId=${resId}`;

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