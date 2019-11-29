package hub_emulator.json.license;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

@Getter
public class Services {

    @SerializedName("description")
    private String description;

    @SerializedName("code")
    private Integer code;

    @SerializedName("expire_date")
    private String expireDate;

}
