package teamredi.retrodiary.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
public class FileStorageUtil {

    private static final String LOCAL_STORE_DIR = "uploadFiles/";

    private static final String ROOT_PATH = System.getProperty("user.dir");
    private static final String FILE_DIR = ROOT_PATH + "/src/main/resources/static/uploadFiles/";

    public static String getFullPath(String filename) {
        return FILE_DIR + filename;
    }

    public static Pair<String, String> saveFile(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        // 원본 파일명 -> 서버에 저장된 파일명 (중복 X)
        // 파일명이 중복되지 않도록 UUID로 설정 + 확장자 유지
        assert originalFilename != null;
        String savedFilename = UUID.randomUUID() + "." + extractExt(originalFilename);

        Path path = Paths.get(LOCAL_STORE_DIR + savedFilename);
        log.info(path.toString());

        // 업로드 디렉토리가 존재하지 않으면 생성합니다.
        Files.createDirectories(path.getParent());

        // 파일 저장
//        file.transferTo(new File(getFullPath(savedFilename)));

        // 파일을 로컬에 저장합니다.
        Files.write(path, file.getBytes());

        // 저장된 파일의 경로를 반환합니다.
        return Pair.of(originalFilename, savedFilename);
    }

    // 확장자 추출
    private static  String extractExt(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");
        return originalFilename.substring(pos + 1);
    }



//    public static String saveFile(MultipartFile file) throws IOException {
//        // 파일 이름을 고유하게 생성합니다.
//        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
//        Path path = Paths.get(UPLOAD_DIR + fileName);
//
//        log.info(path.toString());
//
//        // 업로드 디렉토리가 존재하지 않으면 생성합니다.
//        Files.createDirectories(path.getParent());
//
//        // 파일을 로컬에 저장합니다.
//        Files.write(path, file.getBytes());
//
//        // 저장된 파일의 경로를 반환합니다.
//        return fileName;
//    }
}
