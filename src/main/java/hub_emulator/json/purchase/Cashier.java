package hub_emulator.json.purchase;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Cashier {

    @SerializedName("name")
    private String name;

    @SerializedName("tab_number")
    public String tabNumber;

    @SerializedName("inn")
    public String inn;
}
