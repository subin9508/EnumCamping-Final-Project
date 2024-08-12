document.addEventListener('DOMContentLoaded', function() {
    // 모든 이미지 요소를 가져오기
    const images = document.querySelectorAll('.facilities-card img');
    const carouselElement = document.getElementById('carouselExampleControls');
    const carouselItems = document.querySelectorAll('.carousel-item');
    const modalTitle = document.getElementById('modalTitle');
    const modal = new bootstrap.Modal(document.getElementById('imageModal'));

    images.forEach((image, index) => {
        image.addEventListener('click', function() {
            // 클릭된 이미지의 인덱스와 일치하는 Carousel 아이템을 활성화
            carouselItems.forEach(item => item.classList.remove('active'));
            carouselItems[index].classList.add('active');

            // 해당 이미지의 타이틀을 모달 타이틀로 설정
            const title = carouselItems[index].getAttribute('data-title');
            modalTitle.textContent = title;

            // 모달을 표시
            modal.show();
        });
    });

    // Carousel 슬라이드 변경 시 modal-title 업데이트
    carouselElement.addEventListener('slide.bs.carousel', function(e) {
        const activeItem = e.relatedTarget;
        const newTitle = activeItem.getAttribute('data-title');
        modalTitle.textContent = newTitle;
    });
    
    // 모달 창 외부를 클릭하면 모달이 닫히도록 설정
    window.onclick = function(event) {
        if (event.target.classList.contains('modal')) {
            const modals = document.querySelectorAll('.modal');
            modals.forEach(modal => modal.style.display = 'none');
        }
    }
});