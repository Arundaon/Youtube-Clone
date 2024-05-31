package arundaon.ytclone.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class UpdateVideoRequest {

    @JsonIgnore
    @NotBlank
    private String id;

    @Size(max=64)
    private String title;

    private String description;
}
