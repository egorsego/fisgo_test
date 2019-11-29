package application_manager.api_manager.json.response.data;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@Builder
@EqualsAndHashCode
public class Discount {

    @SerializedName("id")
    private int $id;
    private String mode;
    private String name;
    private String type;
    private String remId;
    private double value;
}
