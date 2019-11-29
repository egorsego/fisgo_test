package hub_emulator.json.purchase;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

@Getter
public class Discounts {

    @SerializedName("rem_id")
    private String remId;

    @SerializedName("sum")
    private long sum;
}
