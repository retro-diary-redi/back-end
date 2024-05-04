package teamredi.retrodiary.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import teamredi.retrodiary.util.FileStorageUtil;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Slf4j
@RestController
public class FileController {

    @GetMapping("/diaries/image/{filename}")
    public ResponseEntity<?> getDiaryImage(@PathVariable("filename") String filename) throws IOException {
        log.info(filename);

        //파일명이 한글일 때 인코딩 작업
        String savedFilename = URLEncoder.encode(filename, "UTF-8");

        //원본파일명에서 공백이 있을 때, +로 표시되므로 공백으로 처리
        savedFilename = savedFilename.replaceAll("\\+", "%20");

        // 파일 경로를 생성
        Path path = Paths.get(FileStorageUtil.LOCAL_STORE_DIR, filename);

        // 파일의 존재 및 읽기 가능성을 확인합니다
        if (!Files.exists(path) || !Files.isReadable(path)) {
            throw new RuntimeException("File not found or unreadable: " + filename);
        }

        // MIME 타입을 확인하고, 없을 경우 기본값을 설정
        String mimeType = Optional.ofNullable(Files.probeContentType(path))
                .orElse("application/octet-stream");


        // 파일을 데이터 스트림으로 변환
        Resource resource = new InputStreamResource(Files.newInputStream(path));

        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_TYPE, mimeType)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + savedFilename + ";")
                .body(resource);
    }

}
