package com.itwill.finalproject.domain;

public enum UserRole {
    USER("USER"),
    ADMIN("ADMIN");
    
    
    private String authority;
    
    // 주의: enum의 생성자는 항상 private. private 수식어는 생략함.
    UserRole(String authority) {
        this.authority = authority;
    }
    
    public String getAuthority() {
        return this.authority;
    }
    public static UserRole fromValue(String value) {
        for (UserRole role : values()) {
            if (role.getAuthority() == value) {
                return role;
            }
        }
        throw new IllegalArgumentException("Unknown role Authority: " + value);
    }
    
}