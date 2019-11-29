package hub_emulator.json.purchase;

import com.google.gson.annotations.SerializedName;
import hub_emulator.response.enums.TypeResponseExPurchase;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Builder
@Getter
@EqualsAndHashCode
public class Payments {

    @SerializedName("type")
    private TypeResponseExPurchase type;

    @SerializedName("sum")
    private Integer sum;

    @SerializedName("count")
    public Integer count;
}
