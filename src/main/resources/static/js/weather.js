
/**
 * 날씨를. 구해보자!
 */

document.addEventListener('DOMContentLoaded', () => {

    let time;
    let date;
    let dateNow;
    let timeNow;
    findTime();
    const temp = document.querySelector('span#temp');
    const sky = document.querySelector('span#sky');
    const rain = document.querySelector('span#rain');

    let uri = `https://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst?` +
        `serviceKey=5ZWN2MuLqeW88XkZBweaCIimSDPJ15cJSMOyCDlFAB%2BHctaC45Dff2I6163wf0NhJpwik7uZdeeu81cUMhLnwg%3D%3D` +
        `&pageNo=1&numOfRows=1000&dataType=JSON&base_date=${date}&base_time=${time}&nx=76&ny=129`;


    //console.log(uri)
    axios.get(uri)
        .then((response) => {
            //console.log(response);
            function findData(keyword){
                const filteredData = response.data.response.body.items.item.filter(item => 
                    item.category === keyword && item.fcstDate === dateNow && item.fcstTime === timeNow
                );
                //console.log(filteredData);
                const fcstValues = filteredData.map(item => item.fcstValue);
                //console.log(fcstValues);
                return fcstValues;
            }
            const tempNow = findData("TMP");
            //console.log(tempNow);
            temp.innerHTML = tempNow;
            const cloudNow = findData("SKY");
            //console.log(cloudNow);
            if (cloudNow == 1){
                sky.innerHTML = "맑음"
            } else if ( cloudNow == 3){
                sky.innerHTML = "구름 많음"
            } else {
                sky.innerHTML = "흐림"
            }
            
            //맑음(1), 구름많음(3), 흐림(4)
            const rainNow = findData('PTY');
            if (rainNow==0){
                rain.innerHTML = "비X, ";
            } else if (rainNow==1){
                rain.innerHTML = "비, ";
            } else if (rainNow==2){
                rain.innerHTML = "비/눈, ";
            } else if (rainNow==3){
                rain.innerHTML = "눈, ";
            } else {
                rain.innerHTML = "소나기, ";
            }
            //console.log(rainNow);
            //없음(0), 비(1), 비/눈(2), 눈(3), 소나기(4) 
        })
        .catch((error) => console.log(error));

    
    function findTime() {

        const today = new Date();

        let year = today.getFullYear();
        let month = ('0' + (today.getMonth() + 1)).slice(-2);
        let day = ('0' + today.getDate()).slice(-2);
        
        dateNow = year+month+day;
        
        let hour = today.getHours();
        let min = today.getMinutes();
        hour = ('0' + hour).slice(-2);
        timeNow = hour.toString()+"00";
        //console.log('min');
        //console.log(min);
        //console.log(dateNow);
        //console.log(timeNow);
        //console.log('min');
        //console.log(hour);
        if (hour<12){
            time = "2300";
            day = today.getDate()-1;
            day = ('0' + day).slice(-2);
        } 
        /*else if (hour==11) {
            //console.log('확인');
            if (min<10){
                time = "2300";
                day = today.getDate()-1;
                day = ('0' + day).slice(-2);
            } else {
               time = "1100"; 
            }
        }*/ else {
            time = "1100";
        }

        date = year + month + day;

        console.log('예보 기준 시각 : ',time);
        console.log('예보 기준 날짜 : ',date);

    }
    

});