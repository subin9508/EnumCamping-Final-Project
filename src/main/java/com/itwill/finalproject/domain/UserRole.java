package com.itwill.finalproject.domain;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum UserRole {
    ADMIN(0,"ADMIN"),
    USER(1,"USER"),
    WITHDRAWUSER(2,"WITHDRAWUSER");
    

	private int code;
    private String authority;
    
    // 주의: enum의 생성자는 항상 private. private 수식어는 생략함.
    UserRole(int code, String authority) {
        this.code = code;
    	this.authority = authority;
    }
    
    
    

    public String getAuthority() {
        return  this.role;
    }

    
    public int getCode() {
    	return this.code;
    }
    
  
    
    public static UserRole fromValue(String value) {
        for (UserRole role : values()) {
            if (role.getAuthority() == value) {
                return role;
            }
        }
        throw new IllegalArgumentException("Unknown role Authority: " + value);
    }
    public static List<String> getAllAuthorities() {

        return Arrays.stream(UserRole.values())
            .filter(r -> r.role.equals(role))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("No role defined for " + role));
    }
}