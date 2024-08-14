package com.itwill.finalproject.web;

import java.security.Principal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.itwill.finalproject.domain.QnA;
import com.itwill.finalproject.dto.QnACreateDto;
import com.itwill.finalproject.dto.QnAListItemDto;
import com.itwill.finalproject.dto.QnASearchRequestDto;
import com.itwill.finalproject.dto.QnAUpdateDto;
import com.itwill.finalproject.service.QnAService;
import com.itwill.finalproject.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/community/qna")
public class QnAController {
	
	private final QnAService qnaSvc;
	
	// 비밀글 접근 가능 여부를 확인하는 메서드
	private boolean canAccessQnA(QnA qna, UserDetails userDetails) {
	    return !qna.isSecret() || 
	           qna.getQnaUserId().equals(userDetails.getUsername()) ||
	           userDetails.getAuthorities().stream()
	                      .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
	}

	
	@GetMapping("/list")
	public void list(@RequestParam(name = "p", defaultValue = "0" ) int pageNo, 
			@RequestParam(name = "category", required = false) String category,
			@RequestParam(name = "keyword", required = false) String keyword,
			Model model) {
		log.info("list(pageNo={}, category={}, keyword={})", pageNo, category, keyword);
	
	    // Pageable 생성 (페이지 번호와 함께)
//	    Pageable pageable = PageRequest.of(pageNo, 10, Sort.by("id").descending());

	    // 서비스 계층에서 페이지 데이터를 가져옵니다.
	    Page<QnAListItemDto> page = qnaSvc.read(pageNo, Sort.by("id").descending());

	    // 'page' 객체를 모델에 추가하여 템플릿에 전달
	    model.addAttribute("pager", page);
	    model.addAttribute("qnas", page.getContent());  // 페이지 콘텐츠를 별도로 전달
	    model.addAttribute("totalCount", page.getTotalElements());
	    
	    // 현재 페이지 번호, 총 페이지 수를 모델에 추가
	    model.addAttribute("currentPage", page.getNumber()); // 현재 페이지 번호 (0부터 시작)
	    model.addAttribute("totalPages", page.getTotalPages()); // 총 페이지 수
	    
	    // category와 keyword를 모델에 추가
	    model.addAttribute("category", category);
	    model.addAttribute("keyword", keyword);
	    
	    // pagination fragment에서 사용하기 위한 현재 요청 주소 정보
	    model.addAttribute("baseUrl", "/community/qna/list");
    }
    
//    @PreAuthorize("hasRole('USER')") //-> role이 일치하는 아이디/비밀번호 인증.
    @GetMapping("/create")
    public void create() {
        log.info("create() GET");
    }
    
//    @PreAuthorize("hasRole('USER')")
    @PostMapping("/create")
    public String create(@AuthenticationPrincipal UserDetails userDetails, @ModelAttribute QnACreateDto dto) {
        log.info("QNA create(dto={})", dto);
        
        // 현재 로그인된 사용자의 아이디를 설정
        dto.setQnaUserId(userDetails.getUsername());
        
        // 서비스 계층의 메서드를 호출해서 작성한 포스트를 DB에 저장.
        qnaSvc.create(dto);
        
        return "redirect:/community/qna/list";
    }

    
//    @PreAuthorize("hasRole('USER')")
//    @GetMapping({ "/details", "/modify" })
//    public void details(@RequestParam(name = "id") Long id, 
//    		@AuthenticationPrincipal UserDetails userDetails,
//    		Model model) {
//        log.info("details(id={})", id);
//        
//        QnA qna = qnaSvc.readById(id);
//
//        // 접근 제한 로직
//        if (!canAccessQnA(qna, userDetails)) {
//            model.addAttribute("message", "작성자와 관리자만 접근 가능합니다.");
//            model.addAttribute("accessDenied", true);
//        }
//
//        model.addAttribute("qna", qna);
//        model.addAttribute("signedInUser", userDetails.getUsername());
//        model.addAttribute("userRole", userDetails.getAuthorities().stream()
//                            .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")) ? 0 : 1);
//
//        //-> view 이름은, 요청 주소가 "details"인 경우에는 details.html
//        // 요청 주소가 "modify"인 경우에는 modify.html
//    }
        
    	
// // QnA 게시글 수정 폼 조회
//    @GetMapping("/modify")
//    public String modifyForm(@RequestParam(name = "id") Long id, 
//                             @AuthenticationPrincipal UserDetails userDetails,
//                             Model model) {
//        log.debug("modifyForm(Id={})", id);
//
//        // QnA 게시글 조회
//        QnA qna = qnaSvc.readById(id);
//        String signedInUser = userDetails.getUsername();
//        boolean isAdmin = userDetails.getAuthorities().stream()
//                                     .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
//
//        log.debug("signedInUser: {}", signedInUser);
//        log.debug("isAdmin: {}", isAdmin);
//        log.debug("qnaUserId: {}", qna.getQnaUserId());
//
//        // 비밀글 여부 확인
//        if (qna.getQnaLock() == 1 && !qna.getQnaUserId().equals(signedInUser) && !isAdmin) {
//            model.addAttribute("message", "비밀글은 작성자와 관리자만 볼 수 있습니다.");
//            return "redirect:/community/qna/list"; // 접근 거부 시 리스트 페이지로 리다이렉트
//        }
//
//        model.addAttribute("qna", qna);
//        model.addAttribute("signedInUser", signedInUser);
//        model.addAttribute("userRole", isAdmin ? 0 : 1);
//
//        return "/community/qna/modify"; // 수정 페이지로 이동
//    }
//
//    // QnA 게시글 상세 조회
//    @GetMapping("/details")
//    public String details(@RequestParam(name = "id") Long id, 
//                          @AuthenticationPrincipal UserDetails userDetails,
//                          Model model,
//                          RedirectAttributes redirectAttributes
//                          ) {
//        log.debug("details(Id={})", id);
//
//        // QnA 게시글 조회
//        QnA qna = qnaSvc.readById(id);
//        String signedInUser = userDetails.getUsername();
//        boolean isAdmin = userDetails.getAuthorities().stream()
//                                     .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
//
//        log.debug("signedInUser: {}", signedInUser);
//        log.debug("isAdmin: {}", isAdmin);
//        log.debug("qnaUserId: {}", qna.getQnaUserId());
//
//        // 비밀글 여부 확인
//        if (!canAccessQnA(qna, userDetails)) {
//            redirectAttributes.addFlashAttribute("message", "작성자와 관리자만 접근 가능합니다.");
//            return "redirect:/community/qna/list"; // 접근 거부 시 리스트 페이지로 리다이렉트
//        }
//
//        // 조회수 증가
////        QnAListItemDto.updateViewCount(id); // 조회수 증가 메서드 호출
//        model.addAttribute("qna", qna);
//        model.addAttribute("signedInUser", signedInUser);
//        model.addAttribute("userRole", isAdmin ? 0 : 1);
//
//        // 답변 목록 추가
////        List<QnAAnswerDto> answers = qnaanswerService.getAnswersByQnaPostId(qnaPostId);
////        log.debug("답변 목록 추가:{}", answers);
////        model.addAttribute("answers", answers);
//
//        return "/community/qna/details";
//    }
    
    
 // QnA 게시글 수정 폼 조회
    @GetMapping("/modify")
    public String modifyForm(@RequestParam(name = "id") Long id, 
                             @AuthenticationPrincipal UserDetails userDetails,
                             Model model, RedirectAttributes redirectAttributes) {
        log.debug("modifyForm(Id={})", id);

        // QnA 게시글 조회
        QnA qna = qnaSvc.readById(id);
        String signedInUser = userDetails.getUsername();
        boolean isAdmin = userDetails.getAuthorities().stream()
                                     .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        log.debug("signedInUser: {}", signedInUser);
        log.debug("isAdmin: {}", isAdmin);
        log.debug("qnaUserId: {}", qna.getQnaUserId());

        // 비밀글 여부 확인
        if (qna.getQnaLock() == 1 && !qna.getQnaUserId().equals(signedInUser) && !isAdmin) {
            redirectAttributes.addFlashAttribute("message", "비밀글은 작성자와 관리자만 볼 수 있습니다.");
            return "redirect:/community/qna/list"; // 접근 거부 시 리스트 페이지로 리다이렉트
        }

        model.addAttribute("qna", qna);
        model.addAttribute("signedInUser", signedInUser);
        model.addAttribute("userRole", isAdmin ? 0 : 1);

        return "/community/qna/modify"; // 수정 페이지로 이동
    }

    // QnA 게시글 상세 조회
    @GetMapping("/details")
    public String details(@RequestParam(name = "id") Long id,
    					  @RequestParam(name = "p", defaultValue = "0") int pageNo,
                          @AuthenticationPrincipal UserDetails userDetails,
                          Model model, RedirectAttributes redirectAttributes) {
    	log.debug("details(Id={}, pageNo={})", id, pageNo);

        // QnA 게시글 조회
        QnA qna = qnaSvc.readById(id);
        String signedInUser = userDetails.getUsername();
        boolean isAdmin = userDetails.getAuthorities().stream()
                                     .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        log.debug("signedInUser: {}", signedInUser);
        log.debug("isAdmin: {}", isAdmin);
        log.debug("qnaUserId: {}", qna.getQnaUserId());
        log.debug("qnaLock: {}", qna.getQnaLock());

        // 비밀글 여부 확인
//        if (qna.getQnaLock() == 1 && !qna.getQnaUserId().equals(signedInUser) && !isAdmin) {
//            redirectAttributes.addFlashAttribute("message", "작성자와 관리자만 접근 가능합니다.");
//            return "redirect:/community/qna/list"; // 접근 거부 시 리스트 페이지로 리다이렉트
//        }

        if (!canAccessQnA(qna, userDetails)) {
            redirectAttributes.addFlashAttribute("message", "작성자와 관리자만 접근 가능합니다.");
            return "redirect:/community/qna/list?p=" + pageNo;
        }
        
        // 조회수 증가 조건: 
        if (!qna.isSecret() || qna.getQnaUserId().equals(signedInUser) || isAdmin) {
        	log.debug("Incrementing view count");
        	qna.incrementViewCount(); // 조회수 증가 메서드 호출
        }
        
        model.addAttribute("qna", qna);
        model.addAttribute("signedInUser", signedInUser);
        model.addAttribute("userRole", isAdmin ? 0 : 1);
        model.addAttribute("pageNo", pageNo);

        return "/community/qna/details";
    }
    
    
//    @PreAuthorize("hasRole('USER')")
    @GetMapping("/delete")
    public String delete(@RequestParam("id") Long id, 
    		@AuthenticationPrincipal UserDetails userDetails) {
        log.info("delete(id={})", id);
        
        qnaSvc.delete(id);
        
        return "redirect:/community/qna/list";
    }
    
//    @PreAuthorize("hasRole('USER')")
    @PostMapping("/update")
    public String update(@AuthenticationPrincipal UserDetails userDetails, QnAUpdateDto dto) {
        log.info("update(dto={})", dto);
        
        qnaSvc.update(dto);
        
        return "redirect:/community/qna/details?id=" + dto.getId();
    }
    
    @GetMapping("/search")
    public String search(QnASearchRequestDto dto, Model model) {
        log.info("search(dto={})", dto);
        
        Page<QnAListItemDto> result = qnaSvc.search(dto, Sort.by("id").descending());
        
        // 일관된 모델 속성 이름 사용
        model.addAttribute("pager", result);  // "page"를 "pager"로 변경
        model.addAttribute("qnas", result.getContent());  // 페이지 콘텐츠를 별도로 전달
        model.addAttribute("totalCount", result.getTotalElements());
        
        // 현재 페이지 번호, 총 페이지 수를 모델에 추가
        model.addAttribute("currentPage", result.getNumber()); // 현재 페이지 번호 (0부터 시작)
        model.addAttribute("totalPages", result.getTotalPages()); // 총 페이지 수
        
        // 검색 조건을 유지하기 위해 category와 keyword를 모델에 추가
        model.addAttribute("category", dto.getCategory());
        model.addAttribute("keyword", dto.getKeyword());
        
        // pagination fragment에서 사용할 현재 요청 주소 정보
        model.addAttribute("baseUrl", "/community/qna/search");
        
        return "/community/qna/list";
    }
		
}