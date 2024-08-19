package com.itwill.finalproject.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.itwill.finalproject.domain.Profile;
import com.itwill.finalproject.domain.User;
import com.itwill.finalproject.dto.ProfileDto;
import com.itwill.finalproject.repository.ProfileRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProfileService {
    private final ProfileRepository profileRepo;

    @Value("${file.upload.path}")
    private String uploadFolder;

    @Transactional
    public String ProfileUpload(ProfileDto profileDto, User user) {
        try {
            String imageFileName = saveProfileImage(profileDto.getFile(), user.getUserId());
            
            Profile profile = profileRepo.findByUser(user).orElse(new Profile());
            profile.setProfileImageUrl(imageFileName);
            profile.setUser(user);
            profileRepo.save(profile);
            
            return imageFileName;
        } catch (IOException e) {
            log.error("프로필 이미지 업로드 중 오류 발생", e);
            throw new RuntimeException("파일 업로드에 실패했습니다.", e);
        }
    }

    private String saveProfileImage(MultipartFile file, String userId) throws IOException {
        UUID uuid = UUID.randomUUID();
        String imageFileName = uuid + "_" + userId + "_" + file.getOriginalFilename();
        Path imageFilePath = Paths.get(uploadFolder, imageFileName);
        
        log.info("프로필 이미지 파일 이름: {}", imageFileName);
        log.info("프로필 이미지 파일 경로: {}", imageFilePath);

        Files.write(imageFilePath, file.getBytes());
        return imageFileName;
    }

    @Transactional
    public void deleteProfileImage(User user) {
        Profile profile = profileRepo.findByUser(user).orElse(null);
        if (profile != null && profile.getProfileImageUrl() != null) {
            Path imagePath = Paths.get(uploadFolder, profile.getProfileImageUrl());
            try {
                boolean deleted = Files.deleteIfExists(imagePath);
                if (deleted) {
                    log.info("프로필 이미지 삭제 성공: {}", imagePath);
                } else {
                    log.warn("프로필 이미지 파일이 존재하지 않습니다: {}", imagePath);
                }
            } catch (IOException e) {
                log.error("프로필 이미지 삭제 중 오류 발생", e);
                throw new RuntimeException("파일 삭제에 실패했습니다.", e);
            }
            profile.setProfileImageUrl(null);
            profileRepo.save(profile);
        }
    }

    public String getProfileImageUrl(User user) {
        return profileRepo.findByUser(user)
                .map(Profile::getProfileImageUrl)
                .orElse(null);
    }
}