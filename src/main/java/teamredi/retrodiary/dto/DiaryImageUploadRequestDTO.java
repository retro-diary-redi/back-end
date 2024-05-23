package teamredi.retrodiary.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
public class DiaryImageUploadRequestDTO {
    // 여러개의 이미지를 업로드할 경우에는 List<MultpartFile>로 코드를 수정한다.
    private MultipartFile file;

    @Builder
    public DiaryImageUploadRequestDTO(MultipartFile file) {
        this.file = file;
    }
}
