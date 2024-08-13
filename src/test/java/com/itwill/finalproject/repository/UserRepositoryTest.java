package com.itwill.finalproject.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.itwill.finalproject.domain.User;
import com.itwill.finalproject.domain.UserRole;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
public class UserRepositoryTest {
	@Autowired
	UserRepository userRepo;
	
	@Autowired
	PasswordEncoder passwordEncoder;
	
	   @Test
	    public void testSave() {
	        // 엔터티 객체를 DB members 테이블에 저장.
	        
	        User m = User.builder()
	                .userId("admin123")
	                .userPassword(passwordEncoder.encode("qwer1234"))
	                .userEmail("admin@itw44ill.com")
	                .userName("동준")
	                .userRole(0)
	                .userPhone("010-5044-5296")
	                .userState(0)
	                .build();
	        log.info("save 호출 전 = {}", m);
	        
	        m = userRepo.save(m);
	        //-> members 테이블, member_roles 테이블에 insert.
	        log.info("save 호출 후 = {}", m);
	    }
}
