package com.demoblog.service;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.entity.ContentType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Component
@Service
public class S3Service {

    //key : fileName
    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    /**
     * MultipartFile을 전달받아 File로 전환한 후 S3에 업로드
     *
     * @param multipartFile
     * @param dirName
     * @return
     * @throws IOException
     */
    public String uploadFiles(MultipartFile multipartFile, String dirName) throws IOException {
        //todo: Exception Handler
        File uploadFile = convert(multipartFile)
                .orElseThrow(() -> new IllegalArgumentException("MultipartFile -> File 전환 실패"));
        return upload(uploadFile, dirName);
    }

    public Map<String, String> uploadFiles(String filePath, MultipartFile[] multipartFiles) throws IOException {
        Map<String, String> results = new HashMap<>();

        //파일 확장자 추출
        for (MultipartFile mf : multipartFiles) {
            String contentType = mf.getContentType();

            //확장자 명이 존재하지 않을 경우 저체 취소 처리
            if (ObjectUtils.isEmpty(contentType)) {
//                throw new CustomException(ErrorCode.INVALID_FILE_CONTENT_TYPE);
                throw new IllegalArgumentException("파일 확장자가 없습니다.");
            } else if (!(contentType.equals(ContentType.IMAGE_JPEG.toString())
                    || contentType.equals(ContentType.IMAGE_PNG.toString()))) {
//                throw new CustomException(ErrorCode.MISMATCH_IMAGE_FILE);
                throw new IllegalArgumentException("잘못된 파일 확장자 입니다(.PNG, .JPEG only");
            }

        }

        //S3 업로드
        List<String> listUrl = new ArrayList<>();
        for (MultipartFile mf : multipartFiles) {
            File uploadFile = convert(mf)
                    .orElseThrow(() -> new IllegalArgumentException("MultipartFile -> File 전환 실패"));

            listUrl.add(upload(uploadFile, filePath));
        }
        return results;
    }


    private String upload(File uploadFile, String dirName) {
        //파일명 중복을 피하기 위한 날짜 추가
        String formatDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("/yyyy-MM-dd HH:mm/"));
        String fileName = dirName + formatDate + uploadFile.getName();

        String uploadImageUrl = putS3(uploadFile, fileName);

        removeNewFile(uploadFile);  // 로컬에 생성된 File 삭제 (MultipartFile -> File 전환 하며 로컬에 파일 생성됨)
        log.info("********file name : {} ************", fileName);
        return uploadImageUrl;      // 업로드된 파일의 S3 URL 주소 반환
    }

    private String putS3(File uploadFile, String fileName) {
        amazonS3Client.putObject(
                new PutObjectRequest(bucket, fileName, uploadFile)
                        .withCannedAcl(CannedAccessControlList.PublicRead)    // PublicRead 권한으로 업로드 됨
        );
        return amazonS3Client.getUrl(bucket, fileName).toString();
    }

    private void removeNewFile(File targetFile) {
        if (targetFile.delete()) {
            log.info("파일이 삭제되었습니다.");
        } else {
            log.error("파일이 삭제되지 못했습니다.");
        }
    }

    /**
     * 클라로부터 입력받은 MultipartFile을 file로 변환 후, 로컬에 저장하고
     * upload 메서드로 버킷에 업로드
     * - Unable to calculate MD5 hash: [파일명] (No such file or directory) 방지
     */
    private Optional<File> convert(MultipartFile file) throws IOException {
        File convertFile = new File(file.getOriginalFilename());
        if (convertFile.createNewFile()) {
            try (FileOutputStream fos = new FileOutputStream(convertFile)) {
                fos.write(file.getBytes());
            }
            return Optional.of(convertFile);
        }
        return Optional.empty();
    }

    /**
     * 새로운 파일 고유 ID를 생성합니다.
     *
     * @return 36자리의 UUID
     */
    public static String createFileId() {
        return UUID.randomUUID().toString();
    }

    public void delete(String filePath) throws SdkClientException {

        //todo: Error Handling
        if (!amazonS3Client.doesObjectExist(bucket, filePath)) {
            throw new AmazonS3Exception("Object " + filePath + " does not exist!");
        }
        amazonS3Client.deleteObject(new DeleteObjectRequest(bucket, filePath));
        log.info("file deletion complete from aws : {}", filePath);
    }

    public Map<String, String> getList() {
        Map<String, String> resultMap = new HashMap<>();
//        amazonS3Client.get

        return resultMap;
    }
}