package hub_emulator.json.purchase.product;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

@Getter
public class Parent {

    @SerializedName("index")
    private String index;

    @SerializedName("meta")
    private Meta meta;

}
