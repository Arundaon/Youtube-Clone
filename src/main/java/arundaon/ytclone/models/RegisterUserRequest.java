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
public class RegisterUserRequest {
    @NotBlank @Size(min = 3, max = 16)
    private String username;

    @NotBlank @Size(min = 8, max = 100)
    private String password;

    @NotBlank @Size(min = 3, max = 16)
    private String name;
}
