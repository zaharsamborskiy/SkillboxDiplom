package searchengine.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@NoArgsConstructor
public class Result {
    private boolean result;
    private String error;

    public Result(boolean result) {
        this.result = result;
    }

    public Result(boolean result, String error) {
        this.result = result;
        this.error = error;
    }
}
