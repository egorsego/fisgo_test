package hub_emulator.json.factory;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Builder
@Getter
@EqualsAndHashCode
public class Fields {

    @SerializedName("DEVICE_UUID")
    private String deviceUuid;
    @SerializedName("ETH_MAC")
    private String ethMac;
}
