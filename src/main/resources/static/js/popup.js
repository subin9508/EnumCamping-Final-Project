/**
 * 
 */

 // 쿠키 설정 함수
function setCookie(name, value, expiredays) {
    var todayDate = new Date();
    todayDate.setDate(todayDate.getDate() + expiredays);
    var cookieString = name + "=" + encodeURIComponent(value)
        + "; path=/; expires=" + todayDate.toGMTString();

    // 환경에 따라 secure 속성 추가
    if (location.protocol === 'https:') {
        cookieString += "; secure";
    }

    // 도메인 설정
    var domain = window.location.hostname;
    if (domain !== 'localhost') {
        cookieString += "; domain=" + domain;
    }

    // SameSite 속성 설정
    cookieString += "; SameSite=Lax";

    document.cookie = cookieString;
    console.log("Setting cookie:", name, value, "Expires:", todayDate,
        "Cookie String:", cookieString);
}

// 팝업 닫기 및 오버레이 숨기기 함수
function checkPopupsAndHideOverlay() {
    if (document.getElementById('popup1').classList.contains('hidden')
        && document.getElementById('popup2').classList.contains('hidden')) {
        document.getElementById('layer_popup').style.display = 'none'; // 모든 팝업이 닫혔을 때만 배경 숨김
    }
}

// 팝업을 닫는 함수
function closePop1() {
    var chkbox1 = document.getElementById('chkbox1');
    if (chkbox1.checked) {
        setCookie("maindiv1", "done", 1);
    }
    document.getElementById('popup1').classList.add('hidden');
    checkPopupsAndHideOverlay();
}

function closePop2() {
    var chkbox2 = document.getElementById('chkbox2');
    if (chkbox2.checked) {
        setCookie("maindiv2", "done", 1);
    }
    document.getElementById('popup2').classList.add('hidden');
    checkPopupsAndHideOverlay();
}

document.addEventListener('DOMContentLoaded', function() {
    var cookiedata = document.cookie;
    if (!cookiedata.includes("maindiv1=done")) {
        document.getElementById('popup1').classList.remove('hidden');
    } else {
        document.getElementById('popup1').classList.add('hidden');
    }
    if (!cookiedata.includes("maindiv2=done")) {
        document.getElementById('popup2').classList.remove('hidden');
    } else {
        document.getElementById('popup2').classList.add('hidden');
    }
    // 모든 팝업이 숨겨져 있다면 layer_popup도 숨김
    if (document.getElementById('popup1').classList.contains('hidden')
        && document.getElementById('popup2').classList.contains('hidden')) {
        document.getElementById('layer_popup').style.display = 'none';
    } else {
        document.getElementById('layer_popup').style.display = 'flex';
    }
});

document.body.addEventListener('click', function(event) {
    if (!event.target.closest('.popup')
        && document.getElementById('layer_popup').style.visibility === 'visible') {
        if (document.getElementById('popup1').classList.contains('hidden')
            && document.getElementById('popup2').classList.contains('hidden')) {
            document.getElementById('layer_popup').style.display = 'none';
        }
    }
});

function showPop() {
    var layerPopup = document.getElementById('layer_popup');
    layerPopup.classList.add('active'); // 활성화 상태 추가
    layerPopup.style.visibility = 'visible';
}