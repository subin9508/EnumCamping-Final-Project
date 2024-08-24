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
        
    	 if (profileDto.getFile() == null || profileDto.getFile().isEmpty()) {
    	        throw new IllegalArgumentException("업로드할 파일이 없습니다.");
    	    }
    	
    	
    	try {
            String imageFileName = saveProfileImage(profileDto.getFile(), user.getUserId());
            
            Profile profile = profileRepo.findByUser(user).orElse(new Profile());
            
            // 기존 이미지가 있다면 삭제
            if (profile.getProfileImageUrl() != null) {
                deleteExistingProfileImage(profile.getProfileImageUrl());
            }
            
            
            profile.setProfileImageUrl(imageFileName);
            profile.setUser(user); // 이 부분이 중요
            profile = profileRepo.save(profile);
            
            // User 엔터티에도 profile 설정
            user.setProfile(profile);
            
            return imageFileName;
        } catch (IOException e) {
            log.error("프로필 이미지 업로드 중 IO 오류 발생", e);
            throw new RuntimeException("파일 저장 중 오류가 발생했습니다: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("프로필 이미지 업로드 중 예상치 못한 오류 발생", e);
            throw new RuntimeException("프로필 업로드 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    	
    }
    
    private void deleteExistingProfileImage(String fileName) {
        try {
            Path filePath = Paths.get(uploadFolder, fileName);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            log.error("기존 프로필 이미지 삭제 중 오류 발생", e);
        }
    }

    private String saveProfileImage(MultipartFile file, String userId) throws IOException {
        String fileExtension = getFileExtension(file.getOriginalFilename());
        String imageFileName = userId + "_profile" + fileExtension;
        Path imageFilePath = Paths.get(uploadFolder, imageFileName);

        log.info("프로필 이미지 파일 이름: {}", imageFileName);
        log.info("프로필 이미지 파일 경로: {}", imageFilePath);

        Files.write(imageFilePath, file.getBytes());
        return imageFileName;
    }

    private String getFileExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf("."));
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
