package com.itwill.finalproject.web;


import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
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
import com.itwill.finalproject.domain.QnAAnswers;
import com.itwill.finalproject.domain.User;
import com.itwill.finalproject.dto.QnACreateDto;
import com.itwill.finalproject.dto.QnAListItemDto;
import com.itwill.finalproject.dto.QnASearchRequestDto;
import com.itwill.finalproject.dto.QnAUpdateDto;
import com.itwill.finalproject.service.QnAAnswerService;
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
	private final QnAAnswerService qnaanwserSvc;
	private final UserService userService;
	
//	// 비밀글 접근 가능 여부를 확인하는 메서드
//	private boolean canAccessQnA(QnA qna, UserDetails userDetails) {
//	    return !qna.isSecret() || 
//	           qna.getQnaUserId().equals(userDetails.getUsername()) ||
//	           userDetails.getAuthorities().stream()
//	                      .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
//	}

	
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
        
        // 비밀글 체크박스를 사용하지 않았을 때 기본값 설정
        if (dto.getQnaLock() == null) {
            dto.setQnaLock(0);
        }
        
        // 서비스 계층의 메서드를 호출해서 작성한 포스트를 DB에 저장.
        qnaSvc.create(dto);
        
        return "redirect:/community/qna/list";
    }

       
    
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

//        // 비밀글 여부 확인
//        if (qna.getQnaLock() == 1 && !qna.getQnaUserId().equals(signedInUser) && !isAdmin) {
//            redirectAttributes.addFlashAttribute("message", "비밀글은 작성자와 관리자만 볼 수 있습니다.");
//            return "redirect:/community/qna/list"; // 접근 거부 시 리스트 페이지로 리다이렉트
//        }

        model.addAttribute("qna", qna);
        model.addAttribute("signedInUser", signedInUser);
        model.addAttribute("userRole", isAdmin ? 0 : 1);

        return "/community/qna/modify"; // 수정 페이지로 이동
    }

    // QnA 게시글 상세 조회
    @GetMapping("/details")
    public String details(@RequestParam(name = "id") Long id,
    					  @RequestParam(name = "p", defaultValue = "0") int pageNo,
                          @AuthenticationPrincipal  UserDetails userDetails,
                          Model model, RedirectAttributes redirectAttributes) {
    	log.debug("details(Id={}, pageNo={})", id, pageNo);
    	
        // 현재 인증된 사용자 정보를 가져옴
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();

        String signedInUser = null;
        Integer userRole = null;

        if (principal instanceof UserDetails) {
            UserDetails authenticatedUserDetails = (UserDetails) principal;
            signedInUser = authenticatedUserDetails.getUsername();
            userRole = userService.findByUserId(signedInUser).getUserRole();
        } else if (principal instanceof String) {
            signedInUser = (String) principal;
            User user = userService.findByUserId(signedInUser);
            if (user != null) {
                userRole = user.getUserRole();
            }
        } else {
            throw new IllegalStateException("Unexpected principal type: " + principal.getClass().getName());
        }
        
     
//        // 현재 인증된 사용자 정보를 가져옴
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        UserDetails authenticatedUserDetails = (UserDetails) authentication.getPrincipal();
        
//        // UserService를 통해 User 객체를 가져옴
//        User user = userService.findByUserId(authenticatedUserDetails.getUsername());
//        
//        // 유저가 null인 경우 예외 처리
//        if (user == null) {
//            throw new NullPointerException("User object is null");
//        }
//        
//        model.addAttribute("user", user);
        
        // QnA 게시글 조회
        QnA qna = qnaSvc.readById(id);

        // 로그인을 하지 않은 경우, userDetails는 null
//        String signedInUser = (user1 != null) ? user1.getUsername() : null;
//        Integer userRole = null;
//        
//        if (user1!= null) {
//            userRole = ((User) user1).getUserRole(); // userRole 값을 가져옴
//        }

        // 로그인을 하지 않은 경우, userDetails는 null
//        String signedInUser = user.getUsername();
//        Integer userRole = user.getUserRole();
        
        log.debug("signedInUser: {}", signedInUser);
        log.debug("userRole: {}", userRole);
        log.debug("qnaUserId: {}", qna.getQnaUserId());
        log.debug("qnaLock: {}", qna.getQnaLock());

        // 비밀글 여부 확인
//        if (qna.getQnaLock() == 1 && !qna.getQnaUserId().equals(signedInUser) && !isAdmin) {
//            redirectAttributes.addFlashAttribute("message", "작성자와 관리자만 접근 가능합니다.");
//            return "redirect:/community/qna/list"; // 접근 거부 시 리스트 페이지로 리다이렉트
//        }

//        if (!canAccessQnA(qna, userDetails)) {
//            redirectAttributes.addFlashAttribute("message", "작성자와 관리자만 접근 가능합니다.");
//            return "redirect:/community/qna/list?p=" + pageNo;
//        }
        
        // 비밀글 여부 확인
        if (qna.isSecret()) {
            // 비밀글인데 로그인하지 않았거나 작성자가 아니거나 관리자가 아닌 경우 접근 불가
            if (signedInUser == null || 
                (!qna.getQnaUserId().equals(signedInUser) && userRole != 0)) {
                redirectAttributes.addFlashAttribute("message", "작성자와 관리자만 접근 가능합니다.");
                return "redirect:/community/qna/list";
            }
        }
        
     // 조회수 증가 조건: 비밀글이 아닌 경우엔 모든 회원 및 비회원 및 관리자 증가 / 비밀글인 경우 작성자와 관리자만 증가
//        if (!qna.isSecret() || qna.getQnaUserId().equals(signedInUser)) {
//        	log.debug("Incrementing view count");
//        	qna.incrementViewCount(); // 조회수 증가 메서드 호출
//        }

//        if (!qna.isSecret()) {
//            // 비밀글이 아니면 모든 사용자에게 조회수 증가
//            log.debug("Incrementing view count for non-secret QnA");
//            qna.incrementViewCount();
//        } else if (signedInUser != null && (qna.getQnaUserId().equals(signedInUser) || userRole == 0)) {
//            // 비밀글인데 작성자이거나 관리자일 경우에만 조회수 증가
//            log.debug("Incrementing view count for secret QnA (authorized user)");
//            qna.incrementViewCount();
//        } else {
//            // 비밀글인데 작성자나 관리자가 아닌 경우 접근 불가
//            redirectAttributes.addFlashAttribute("message", "작성자와 관리자만 접근 가능합니다.");
//            return "redirect:/community/qna/list";
//        }
        
        // 비밀글 여부 확인 및 조회수 증가
        qna = qnaSvc.incrementViewCount(id, signedInUser, userRole);
        
        // 댓글 목록 조회
        List<QnAAnswers> comments = qnaanwserSvc.readCommentsList(id);
        model.addAttribute("comments", comments);
        log.debug("Comments: {}", comments);
        
        // **답변 목록 조회 추가**
        List<QnAAnswers> qnaAnswers = qnaanwserSvc.findByQnaId(id);  // QnA ID로 답변 리스트 조회
        model.addAttribute("qnaAnswers", qnaAnswers);  // 답변 리스트 모델에 추가
        
        // 답변이 없으면 상태를 '답변 대기'로 설정
        if (qnaAnswers.isEmpty()) {
            qna.setQnaState(0);  // 답변 대기 상태
        } else {
            qna.setQnaState(1);  // 답변 완료 상태
        }
        
        
        model.addAttribute("qna", qna);
        model.addAttribute("signedInUser", signedInUser);
        model.addAttribute("userRole", userRole);
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
        
        // 비밀글 체크박스를 사용하지 않았을 때 기본값 설정
        if (dto.getQnaLock() == null) {
            dto.setQnaLock(0);
        }
        
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