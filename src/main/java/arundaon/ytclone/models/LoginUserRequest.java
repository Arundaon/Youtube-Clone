package arundaon.ytclone.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class LoginUserRequest {
    @NotBlank
    @Size(max = 16)
    private String username;

    @NotBlank
    @Size(max = 100)
    private String password;
}
