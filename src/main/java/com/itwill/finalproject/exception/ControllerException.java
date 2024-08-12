package com.itwill.finalproject.exception;

public class ControllerException extends Exception {	
	private static final long serialVersionUID = 1L;

	// 부모 클래스(Exception)의 생성자를 호출하여 메시지를 설정
	public ControllerException(String message) {
		super(message);
	} // constructor #1
	
	// 부모 클래스(Exception)의 생성자를 호출하여 예외 객체를 설정
	public ControllerException(Exception e) {
		super(e);
	} // constructor #2	

} // end class