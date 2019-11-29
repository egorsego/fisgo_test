package hub_emulator.json.purchase.product;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

@Getter
public class AlcoholMeta {

    @SerializedName("alc_code")
    private String alcCode;

    @SerializedName("alc_type_code")
    private String alcTypeCode;

    @SerializedName("capacity")
    private Double capacity;

    @SerializedName("alc_content")
    private Integer alcContent;

}
