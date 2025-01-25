package com.example.tastefulai.domain.image.service;

import com.example.tastefulai.domain.image.entity.Image;
import com.example.tastefulai.domain.member.entity.Member;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface S3Uploader {

    Image uploadImage(MultipartFile image) throws IOException;

    void isValidExtension(MultipartFile image) throws IOException;

    void deleteS3Image(String imageName);
}
