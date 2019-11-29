package hub_emulator.json.handshake;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

@Getter
public class In {

    @SerializedName("upsert_product")
    private int upsertProduct;

    @SerializedName("bind_product")
    private int bindProduct;

    @SerializedName("delete_product")
    private int deleteProduct;

    @SerializedName("upsert_discount")
    private int upsertDiscount;

    @SerializedName("delete_discount")
    private int deleteDiscount;

    @SerializedName("registration")
    private int registration;

    @SerializedName("external_purchase")
    private int externalPurchase;
}

