package com.itwill.finalproject.dto;

import org.springframework.web.multipart.MultipartFile;

import com.itwill.finalproject.domain.Profile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProfileDto {
    private MultipartFile file;
    private String title;

    public Profile toEntity(String profileImageUrl) {
        return Profile.builder()
                .profileImageUrl(profileImageUrl)
                .title(title)
                .build();
    }
}
