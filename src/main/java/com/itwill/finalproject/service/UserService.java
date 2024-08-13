package com.itwill.finalproject.service;


import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.itwill.finalproject.domain.ReservationMaster;
import com.itwill.finalproject.domain.User;
import com.itwill.finalproject.dto.ReservationDetailDto;
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
public class UserService {

    private final UserRepository userRepo;
    private final ReservationMasterRepository reservationMasterRepo;
    private final ReservationDetailRepository reservationDetailRepo;

	// 아이디 중복 체크: true - 중복되지 않은 아이디(사용 가능한 아이디), false - 중복된 아이디.
    
	public boolean checkUserid(String userId) {
		log.debug("checkUserid(user_id={})", userId);

		User user = userRepo.findByUserId(userId).orElseThrow();
		if (user == null) { // userid가 일치하는 레코드가 없을 때(중복된 아이디가 없는 경우)
			return true;
		} else { // userid가 일치하는 레코드가 있을 때(아이디가 중복된 경우)
			return false;
		}
	}

	// 회원 가입 서비스
	@Transactional
	public User create(UserCreateDto dto) {
		log.debug("create({})", dto);

		User result = userRepo.save(dto.toEntity());

		return result;
	}

	// 로그인 서비스
	public Optional<User> read(UserSignInDto dto) {
		log.debug("read(dto={})", dto);

		// 리포지토리 메서드를 호출해서, 아이디와 비밀번호가 일치하는 사용자가 있는 지 검색
		return userRepo.findByUserIdAndUserPassword(dto.getUserId(), dto.getUserPassword());

		
	}

	// 이메일 중복 체크: true - 중복되지 않은 이메일(사용 가능한 이메일), false - 중복된 이메일.
	public boolean checkEmail(String userEmail) {
		log.debug("checkUserEmail(email={})", userEmail);
		 return !userRepo.findByUserEmail(userEmail).isPresent();
	}

	

	public User read(String userId) {
		log.debug("read(id={})", userId);

		return userRepo.findByUserId(userId).orElse(null);
	}


    //비밀번호를 찾아 리턴하는 메서드
	public User searchPassword(User user) {
		log.debug("searchPassword");
		return user;

	}

	//비밀번호를 변경하는 메서드
	@Transactional
	public User updatePassword(User user) {
		log.debug("updatePassword");
		return user;
	}

	
	//이름,아이디,이메일을 검색해 비밀번호를 찾는 메서드
	public String findPasswordByNameAndEmailAndId(String name, String email, String id) {
		log.debug("findPasswordByNameAndEmailAndId({}{}{})", name, email, id);
		String user = userRepo.findPasswordByNameAndEmailAndId(name, email, id).orElseThrow();
		if (user != null) {
			return user;
		}
		return null;
	}
	
	//이름,이메일을 검색해 아이디를 찾는 메서드
	public String findIdByNameAndEmail(String name, String email) {
		log.debug("findIdByNameAndEmail({} {})", name, email);
		String user = userRepo.findIdByNameAndEmail(name, email).orElseThrow();
		if (user != null) {
			return user;
		}
		return null;
	}

	
	// 회원탈퇴 관련
	@Transactional
    public boolean deactivateAccount(Integer userKey, String userPassword) {
		log.debug("Checking password for userKey: {}", userKey);
        // 비밀번호 확인
    	Integer count = userRepo.checkPassword(userKey, userPassword);
        if (count == 0) {
        	log.debug("Password does not match for userKey: {}", userKey);
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
        log.debug("Reservation list for user {}: {}", userKey, list);
        return list;
    }

    public Optional<ReservationMaster> readReservationMasterDetails(Integer resId) {
        log.debug("Finding reservation master details for resId: {}", resId);
        Optional<ReservationMaster> resMaster = reservationMasterRepo.findById(resId);
        log.debug("Found ReservationMaster: {}", resMaster);
        return resMaster;
    }

    public List<ReservationDetailDto> readReservationDetails(Integer rdId) {
        log.debug("Finding reservation details for resvationMaster: {}", rdId);
        List<ReservationDetailDto> resDetails = reservationDetailRepo.findByRdId(rdId);
        log.debug("Found ReservationDetails: {}", resDetails);
        return resDetails;
    }
    
    
    
    
    
}