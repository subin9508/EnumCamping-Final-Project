package com.itwill.finalproject.domain;

public enum UserRole {
	ADMIN(0), USER(1), WITHDRAWUSER(2);

	private final int value;

	UserRole(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public static UserRole fromValue(int value) {
		for (UserRole role : values()) {
			if (role.value == value) {
				return role;
			}
		}
		throw new IllegalArgumentException("Invalid UserRole value: " + value);
	}

}