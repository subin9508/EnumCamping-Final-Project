package com.itwill.finalproject.domain;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter // getter 메서드 만들어줌
@ToString // toString 메서드
@NoArgsConstructor// 기본 생성자
@EqualsAndHashCode // equals(), hashCode(), canEqual()

@EntityListeners(AuditingEntityListener.class) //-> AuditingEntityListener.class : 엔터티 생성되었는지 수정되었는지 등의 이벤트를 듣고 있는 클래스
//-> 엔터티가 최초로 생성되는 생성시간, 최종 수정된 수정시간 등을 자동으로 DB에 기록(저장) 하기 위해서 사용함. 
//-> 테이블에서 생성시간, 수정시간 컬럼 만들때 기본값 설정 하지 않아도 이게 있어서 사용 가능 함.
//->(중요!) 사용하려면 class Springboot5Application 여기에도 @EnableJpaAuditing 애너테이션 추가해야 함.

@MappedSuperclass //-> 상속. 다른 엔터티 클래스의 상위 클래스로 사용된다는 의미. 다른 테이블의 컬럼으로 포함되는 것들을 수퍼클래스로 쓰려고 만든것 
//-> JPA는 거의 jakarta.persistence로 시작해서 import한다고 함.
//간혹 옛날 코드에서 javax.persistence로 시작하면 컴파일 안된다고 함.(자바 21버전에서는 불가)
//javax는 옛날 버전(자바8,9에서 가능함)이라서 import문장을 javax에서 jakarta로 변경해주기
public class BaseTimeEntity {
	//포스트와 댓글 모두 생성, 수정 시간이 있었음. -> 엔터티들이 최소 insert된 시간, 가장 마지막에 update된 시간 정보들.
	// 거의 대부분의 엔터티(테이블)들이 가져야될 속성. 그래서 이 2가지는 수퍼클래스(상위, 부모 클래스)로 설계하고
	//다른 엔터티들은 상속 받아서 쓴다고 함. 그럼 전부 이 2가지 속성을 갖는다고.
	
	@CreatedDate //-> 엔터티 (최초)생성 시간을 저장하는 필드임을 알려주는 애너테이션
	@Column(name = "not_created_time")
	private LocalDateTime createdTime;
	
	@LastModifiedDate //-> 엔터티 (최종) 수정 시간을 저장하는 필드
	@Column(name = "not_modified_time")
	private LocalDateTime modifiedTime;

} //-> 예약 사이트에서는 자동기록 아니기 때문에 여기 다 넣음 안된다고. 따로 하라고 하심. 예약 시간 이런것들..