package com.demoblog.controller;

import com.demoblog.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;


@RestController
@RequestMapping(value = "/images", produces = APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class FileController {
    private final S3Service s3Service;

    @PostMapping
    public Map<String, String> post(
            @RequestPart("image") MultipartFile multipartFile) {
        Map<String, String> ressultMap = new HashMap<>();

        try {
            String url = s3Service.uploadFiles(multipartFile, "static");
            ressultMap.put(multipartFile.getOriginalFilename(), url);
            return ressultMap;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

//    @PostMapping
//    public Map<String, String> upload(@RequestParam Long userId, @RequestParam String category,
//            @RequestPart("files") MultipartFile[] multipartFiles) throws IOException {
//
//        return s3Service.uploadFiles( category + "/" + userId, multipartFiles);
//    }

    @DeleteMapping
    public void remove(@RequestParam String filePath) {
        s3Service.delete(filePath);
    }

//    @GetMapping
//    public Map<String, String> getList() {
//        s3Service.getList();
//    }
}
