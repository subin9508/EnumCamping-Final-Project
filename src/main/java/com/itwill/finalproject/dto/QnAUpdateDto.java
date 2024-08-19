package com.itwill.finalproject.dto;

import lombok.Data;

@Data
public class QnAUpdateDto {
    private Long id;
    private String title;
    private String content;
    private Integer qnaLock;
}
