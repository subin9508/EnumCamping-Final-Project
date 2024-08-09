/**
 * 날씨를. 구해보자!
 */

document.addEventListener('DOMContentLoaded', () => {
    // 전역 변수 선언
    let time;
    let date;
    let dateNow;
    let timeNow;
    
    // 현재 시간과 날짜 계산
    findTime();
    
    // DOM 요소 선택
    const temp = document.querySelector('span#temp');
    const sky = document.querySelector('span#sky');
    const rain = document.querySelector('span#rain');

    // API 요청 URL 생성
    let uri = `https://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst?` +
        `serviceKey=5ZWN2MuLqeW88XkZBweaCIimSDPJ15cJSMOyCDlFAB%2BHctaC45Dff2I6163wf0NhJpwik7uZdeeu81cUMhLnwg%3D%3D` +
        `&pageNo=1&numOfRows=1000&dataType=JSON&base_date=${date}&base_time=${time}&nx=76&ny=129`;

    // 날씨 API 호출
    axios.get(uri)
        .then((response) => {
            // API 응답 데이터 콘솔 로그
            // console.log(response.data);

            function findData(keyword){
                const filteredData = response.data.response.body.items.item.filter(item => 
                    item.category === keyword && item.fcstDate === dateNow && item.fcstTime === timeNow
                );
                
                // 데이터가 존재하지 않는 경우 처리
                if (filteredData.length === 0) return ['정보 없음'];

                const fcstValues = filteredData.map(item => item.fcstValue);
                return fcstValues;
            }

            // 현재 기온, 하늘 상태, 강수 상태 가져오기
            const tempNow = findData("TMP");
            temp.innerHTML = tempNow[0] || '정보 없음';
            
            const cloudNow = findData("SKY");
            if (cloudNow[0] == 1){
                sky.innerHTML = "맑음";
            } else if (cloudNow[0] == 3){
                sky.innerHTML = "구름 많음";
            } else {
                sky.innerHTML = "흐림";
            }
            
            const rainNow = findData('PTY');
            if (rainNow[0] == 0){
                rain.innerHTML = "비X, ";
            } else if (rainNow[0] == 1){
                rain.innerHTML = "비, ";
            } else if (rainNow[0] == 2){
                rain.innerHTML = "비/눈, ";
            } else if (rainNow[0] == 3){
                rain.innerHTML = "눈, ";
            } else {
                rain.innerHTML = "소나기, ";
            }

        })
        .catch((error) => console.log(error));

    // 현재 시간과 날짜를 계산하여 `date`와 `time` 변수 설정
    function findTime() {
        const today = new Date();
        let year = today.getFullYear();
        let month = ('0' + (today.getMonth() + 1)).slice(-2);
        let day = ('0' + today.getDate()).slice(-2);
        dateNow = year + month + day;
        
        let hour = today.getHours();
        let min = today.getMinutes();
        hour = ('0' + hour).slice(-2);
        timeNow = hour.toString() + "00";
        
        if (hour < 12) {
            time = "2300";
            day = today.getDate() - 1;
            day = ('0' + day).slice(-2);
        } else {
            time = "1100";
        }

        date = year + month + day;

        // 디버깅을 위한 콘솔 로그
        console.log('예보 기준 시각 : ', time);
        console.log('예보 기준 날짜 : ', date);
    }
});