package com.itwill.finalproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.itwill.finalproject.domain.User;

public interface UserRepository extends JpaRepository<User, Integer>{

}
