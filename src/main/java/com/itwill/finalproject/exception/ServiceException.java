package com.itwill.finalproject.exception;

public class ServiceException extends Exception {	
	private static final long serialVersionUID = 1L;

	// 부모 클래스(Exception)의 생성자를 호출하여 메시지를 설정합니다.
	public ServiceException(String message) {
		super(message);
	} // constructor #1
	
	
	//부모 클래스(Exception)의 생성자를 호출하여 예외 객체를 설정
	public ServiceException(Exception e) {
		super(e);
	} // constructor #2	
	
	
    // 기본 생성자
    public ServiceException() {
        super();
    }

    // 메시지와 원인 예외를 포함하는 생성자
    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    // 원인 예외만 포함하는 생성자
    public ServiceException(Throwable cause) {
        super(cause);
    }

} // end class