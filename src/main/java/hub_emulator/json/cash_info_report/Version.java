package hub_emulator.json.cash_info_report;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@Builder
@EqualsAndHashCode
public class Version {

    @SerializedName("type")
    public String type;

    @SerializedName("version")
    @EqualsAndHashCode.Exclude public String version;

    @SerializedName("remote_updatable")
    public Boolean remoteUpdatable;

    @SerializedName("project")
    public String project;

    @SerializedName("product")
    public String product;

}
