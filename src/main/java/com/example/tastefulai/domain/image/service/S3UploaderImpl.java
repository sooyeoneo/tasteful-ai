package com.example.tastefulai.domain.image.service;

import com.example.tastefulai.domain.image.entity.Image;
import com.example.tastefulai.domain.member.entity.Member;
import com.example.tastefulai.global.error.errorcode.ErrorCode;
import com.example.tastefulai.global.error.exception.BadRequestException;
import com.example.tastefulai.global.error.exception.CustomException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;

import org.apache.tika.Tika;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class S3UploaderImpl implements S3Uploader {

    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final Set<String> ALLOWED_EXTENSIONS = Set.of("png", "jpeg", "jpg");
    private final Set<String> ALLOWED_MIMETYPE = Set.of("image/png", "image/jpeg", "image/jpg");

    @Transactional
    @Override
    public Image uploadImage( MultipartFile image) throws IOException {

        // 이미지 확장자 확인
        isValidExtension(image);

        //UUID 이름 생성
        String originalName = image.getOriginalFilename();
        String uuid = UUID.randomUUID().toString();
        String uniqueName = uuid + "_" + originalName.replaceAll("\\s", "_");

        // 이미지를 S3에 저장
        try {
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucket)
                            .key(uniqueName)
                            .contentType(image.getContentType())
                            .build(),
                    RequestBody.fromInputStream(image.getInputStream(), image.getSize())
            );
        } catch (IOException ioException) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }

        String imageUrl = String.format("https://%s.s3.amazonaws.com/%s", bucket, uniqueName);

        return new Image(uniqueName, image.getContentType(), image.getSize(), imageUrl);
    }

    // 파일의 확장자를 검증 (png, jpeg, jpg)
    @Override
    public void isValidExtension(MultipartFile image) throws IOException {

        // 파일 이름의 확장자를 검사
        String extension = StringUtils.getFilenameExtension(image.getOriginalFilename());
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new BadRequestException(ErrorCode.INVALID_FILE);
        }

        // 파일의 MIME 타입을 검사
        Tika tika = new Tika();
        String mimeType = tika.detect(image.getInputStream());
        if (!ALLOWED_MIMETYPE.contains(mimeType)) {
            throw new BadRequestException(ErrorCode.INVALID_FILE);
        }
    }

    @Override
    @Transactional
    public void deleteS3Image(String imageName) {
        try {
            s3Client.deleteObject(builder -> builder.bucket(bucket).key(imageName).build());
        } catch (CustomException customException) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }
    }
}
