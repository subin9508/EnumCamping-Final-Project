/**
 * reservation_details.html에 추가
 */

 
 document.addEventListener('DOMContentLoaded',()=>{
	
	const changeBtn = document.querySelector('a#changeBtn');
	const resSpecial = document.querySelector('input#resSpecial');
	changeBtn.addEventListener('click', ()=>{
		
		//에ㅂ인 것 같음
		//그냥 res_special 만들어서 특가 예약하는 경우 뽑아오는게 나을 듯
		
		

		//있으면 안된다고 하기
		
		confirm('특가 기간이 지나 구역 변경만 가능합니다.\n날짜/숙박일수/물품 변경을 원하시면 취소 후 진행해주세요.');
		
	});
	
	
	
	
 });