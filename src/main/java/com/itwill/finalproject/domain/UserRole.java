package com.itwill.finalproject.domain;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum UserRole {
    USER("USER"),
    ADMIN("ADMIN");
    
    
//    private String authority;
//    
//    // 주의: enum의 생성자는 항상 private. private 수식어는 생략함.
//    UserRole(String authority) {
//        this.authority = authority;
//    }
//    
//    public String getAuthority() {
//        return this.authority;
//    }
//    public static UserRole fromValue(String value) {
//        for (UserRole role : values()) {
//            if (role.getAuthority() == value) {
//                return role;
//            }
//        }
//        throw new IllegalArgumentException("Unknown role Authority: " + value);
//    }
//    public static List<String> getAllAuthorities() {
//        return Arrays.stream(UserRole.values())
//                     .map(UserRole::getAuthority)
//                     .collect(Collectors.toList());
//    }
    
    private String role;

    UserRole(String role) {
        this.role = role;
    }

    public String getAuthority() {
        return  this.role;
    }

    public static UserRole fromRole(String role) {
        return Arrays.stream(UserRole.values())
            .filter(r -> r.role.equals(role))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("No role defined for " + role));
    }
}