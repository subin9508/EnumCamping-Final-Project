/**
 * /info/infoRental.jsp에 포함.
 */ 

// 각 이미지와 모달 창 요소를 가져옴
    var barbecueImg = document.getElementById('barbecueImage');
    var showerImg = document.getElementById('showerImage');
    var toiletImg = document.getElementById('toiletImage');
    var sinkImg = document.getElementById('sinkImage');
    var campFireImg = document.getElementById('campFireImage');

    var modal1 = document.getElementById('myModal1');
    var modal2 = document.getElementById('myModal2');
    var modal3 = document.getElementById('myModal3');
    var modal4 = document.getElementById('myModal4');
    var modal5 = document.getElementById('myModal5');

    var modalImg1 = document.getElementById('modalImage1');
    var modalImg2 = document.getElementById('modalImage2');
    var modalImg3 = document.getElementById('modalImage3');
    var modalImg4 = document.getElementById('modalImage4');
    var modalImg5 = document.getElementById('modalImage5');

    // 각 이미지를 클릭했을 때 모달 창을 보여주는 기능을 추가
    barbecueImg.onclick = function() {
        modal1.style.display = "block";
        modalImg1.src = this.src;
    }
    showerImg.onclick = function() {
        modal2.style.display = "block";
        modalImg2.src = this.src;
    }
    toiletImg.onclick = function() {
        modal3.style.display = "block";
        modalImg3.src = this.src;
    }
    sinkImg.onclick = function() {
        modal4.style.display = "block";
        modalImg4.src = this.src;
    }
    poolImg.onclick = function() {
        modal5.style.display = "block";
        modalImg5.src = this.src;
    }

    // 각 모달 창의 닫기 버튼을 설정
    var closeBtns = document.getElementsByClassName('close');
    for (var i = 0; i < closeBtns.length; i++) {
        closeBtns[i].onclick = function() {
            this.parentElement.style.display = "none";
        }
    }

    // 모달 창 외부를 클릭하면 모달이 닫히도록 설정
    window.onclick = function(event) {
        if (event.target.classList.contains('modal')) {
            event.target.style.display = "none";
        }
    }