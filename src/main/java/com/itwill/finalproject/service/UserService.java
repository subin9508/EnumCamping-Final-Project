package com.itwill.finalproject.service;


import java.util.List;
import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.itwill.finalproject.domain.ReservationMaster;
import com.itwill.finalproject.domain.User;

import com.itwill.finalproject.domain.UserRole;
import com.itwill.finalproject.dto.ReservationDetailListDto;

import com.itwill.finalproject.dto.UserCreateDto;
import com.itwill.finalproject.dto.UserSignInDto;
import com.itwill.finalproject.repository.ReservationDetailRepository;
import com.itwill.finalproject.repository.ReservationMasterRepository;
import com.itwill.finalproject.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service

//Spring Security에서 로그인/로그아웃 처리에서 사용할 수 있도록 하기 위해서 
//UserDetailsService 인터페이스를 구현함.
public class UserService implements UserDetailsService {
	
	private final PasswordEncoder passwordEncoder; 
    private final UserRepository userRepo;
    private final ReservationMasterRepository reservationMasterRepo;
    private final ReservationDetailRepository reservationDetailRepo;

	// 아이디 중복 체크: true - 중복되지 않은 아이디(사용 가능한 아이디), false - 중복된 아이디.
    
    public boolean checkUserid(String userId) {
        log.info("checkUserid(user_id={})", userId);

        // findByUserId 메소드가 Optional을 반환한다고 가정
        Optional<User> userOptional = userRepo.findByUserId(userId);

        // Optional이 비어 있으면 사용자 ID가 존재하지 않으므로 중복이 아님
        if (userOptional.isEmpty()) {
            return true; // 중복된 아이디가 없으므로 사용 가능
        } else {
            return false; // 중복된 아이디가 있으므로 사용 불가능
        }
    }

	// 회원 가입 서비스
//	@Transactional
//	public User create(UserCreateDto dto) {
//		log.info("create({})", dto);
//
//		User result = userRepo.save(dto.toEntity());
//
//		return result;
//	}
//	
	//회원 가입 서비스 security 적용
	 @Transactional
	    public User create(UserCreateDto dto) {
	        log.info("create(dto={})", dto);
	        
	        User result = userRepo.save(dto.toEntity(passwordEncoder));
	        // save() -> (1) insert into members, (2) insert into member_roles
	        return result;
	    }

	
	
	// 로그인 서비스
//	public Optional<User> read(UserSignInDto dto) {
//		log.info("read(dto={})", dto);
//
//		// 리포지토리 메서드를 호출해서, 아이디와 비밀번호가 일치하는 사용자가 있는 지 검색
//		return userRepo.findByUserIdAndUserPassword(dto.getUserId(), dto.getUserPassword());
//
//		
//	}
	 //로그인 서비스 security 적용
	 public Optional<User> read(UserSignInDto dto) {
	        log.info("read(dto={})", dto);

	        // 아이디로 사용자를 조회
	        Optional<User> userOptional = userRepo.findByUserId(dto.getUserId());

	        // 사용자가 존재하고 비밀번호가 일치하는지 확인
	        if (userOptional.isPresent()) {
	            User user = userOptional.get();
	            // 평문 비밀번호와 암호화된 비밀번호를 비교
	            if (passwordEncoder.matches(dto.getUserPassword(), user.getUserPassword())) {
	                return Optional.of(user); // 비밀번호가 일치하면 사용자 반환
	            }
	        }

	        return Optional.empty(); // 사용자 없거나 비밀번호가 일치하지 않으면 빈 Optional 반환
	    }
	 
	 
	
	//UserDetailsService 오버라이드
	 @Override
	 public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
	        // DB 테이블(members)에 username이 일치하는 사용자가 있으면 UserDetails 타입의
	        // 객체를 리턴하고, 그렇지 않으면 UsernameNotFoundException을 던짐.
	        
	        log.info("loadUserByUsername(username={})", username);
	        
	        Optional<User> entity = userRepo.findByUserId(username);
	        if (entity.isPresent()) {
	            return entity.get();
	        } else {
	            throw new UsernameNotFoundException(username + ": 일치하는 사용자 정보 없음.");
	        }
	    }
	 

	// 이메일 중복 체크: true - 중복되지 않은 이메일(사용 가능한 이메일), false - 중복된 이메일.
	public boolean checkEmail(String userEmail) {
		log.info("checkUserEmail(email={})", userEmail);
		 return !userRepo.findByUserEmail(userEmail).isPresent();
	}

	

	public User read(String userId) {
		log.info("read(id={})", userId);

		return userRepo.findByUserId(userId).orElse(null);
	}


    //비밀번호를 찾아 리턴하는 메서드
	public User searchPassword(User user) {
		log.info("searchPassword");
		return user;

	}

	//비밀번호를 변경하는 메서드
	@Transactional
	public User updatePassword(User user) {
		log.info("updatePassword");
		return user;
	}

	
	//이름,아이디,이메일을 검색해 비밀번호를 찾는 메서드
	public String findPasswordByNameAndEmailAndId(String name, String email, String id) {
		log.info("findPasswordByNameAndEmailAndId({}{}{})", name, email, id);
		Optional<User> user = userRepo.findPasswordByNameAndEmailAndId(name, email, id);
		return user.map(User::getUserPassword).orElse(null);
	}
	
	//이름,이메일을 검색해 아이디를 찾는 메서드
	public String findIdByNameAndEmail(String name, String email) {
	    // 데이터베이스에서 사용자 아이디 찾기 로직
		Optional<User> user = userRepo.findIdByNameAndEmail(name, email);
		log.info("findIdByNameAndEmail({}{})", name, email);
		return user.map(User::getUserId).orElse(null);
	    
	}

	
	// 회원탈퇴 관련
	// 시큐리티 적용
	@Transactional
	public boolean deactivateAccount(Integer userKey, String userPassword) {
	    log.info("Checking password for userKey: {}", userKey);
	    
	    // 사용자의 정보를 가져옴
	    User user = userRepo.findById(userKey).orElseThrow(() -> 
	        new IllegalArgumentException("사용자 정보를 찾을 수 없습니다.")
	    );

	    // 비밀번호 확인: 사용자가 입력한 비밀번호와 DB에 저장된 암호화된 비밀번호를 비교
	    if (!passwordEncoder.matches(userPassword, user.getUserPassword())) {
	        log.info("비밀번호가 일치하지 않음 : {}", userKey);
	        return false; // 비밀번호가 일치하지 않으면 false 반환
	    }
	    
	    // 회원 비활성화
	    userRepo.deactivateUser(userKey);
	    
	    // 탈퇴 회원 정보 저장
	    userRepo.insertDeletedUser(userKey);
	    
	    return true; // 비활성화 성공 시 true 반환
	}
    
    public boolean checkUserIsActive(String userId) {
        return userRepo.checkUserIsActive(userId) == 1; // 1이면 활성(로그인가능), 0이면 비활성(탈퇴 & 계정 정지)
    }
    
    public boolean checkDeactivationPeriod(String userId) {
    	
    	 int count = userRepo.checkDeactivationPeriod(userId);
         return count > 0; // 1 이상이면 활성화, 0이면 비활성화 // 1이면 비활성화 기간 종료(로그인가능), 0이면 기간 중(아직 비활성화)
    }
    
    
    
    // 유저 키로 사용자 조회
    public Optional<User> findByUserKey(Integer userKey) {
    	return userRepo.findByUserKey(userKey);
    }
    
    
    public Optional<ReservationMaster> readReservationList(Integer userKey) {
        Optional<ReservationMaster> list = reservationMasterRepo.findById(userKey);
        log.info("Reservation list for user {}: {}", userKey, list);
        return list;
    }

    public Optional<ReservationMaster> readReservationMasterDetails(Integer resId) {
        log.info("Finding reservation master details for resId: {}", resId);
        Optional<ReservationMaster> resMaster = reservationMasterRepo.findById(resId);
        log.info("Found ReservationMaster: {}", resMaster);
        return resMaster;
    }


    public List<ReservationDetailListDto> readReservationDetails(Integer rdId) {
        log.info("Finding reservation details for resvationMaster: {}", rdId);
        List<ReservationDetailListDto> resDetails = reservationDetailRepo.findByRdId(rdId);
        log.info("Found ReservationDetails: {}", resDetails);

        return resDetails;
    }
    
   
    
    
    
    
}