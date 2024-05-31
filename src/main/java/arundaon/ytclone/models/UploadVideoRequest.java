package arundaon.ytclone.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UploadVideoRequest {
    @NotBlank @Size(max=64)
    private String title;
    @NotBlank
    private String video;
    private String description;
}
