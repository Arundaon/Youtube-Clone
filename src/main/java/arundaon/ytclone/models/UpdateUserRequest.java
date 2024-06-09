package arundaon.ytclone.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UpdateUserRequest {
    @Size(min = 8, max = 100)
    private String password;
    @Size(min = 3, max = 16)
    private String name;
    private String bio;
    private MultipartFile profile;
}
