package hub_emulator.json.purchase;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Builder
@Getter
@EqualsAndHashCode
public class TaxesSum {

    @SerializedName("NDS_10")
    private Integer nds10;
    @SerializedName("NDS_18")
    private Integer nds18;
    @SerializedName("NDS_10_CALCULATED")
    private Integer nds10calculated;
    @SerializedName("NDS_18_CALCULATED")
    private Integer nds18calculated;

}
