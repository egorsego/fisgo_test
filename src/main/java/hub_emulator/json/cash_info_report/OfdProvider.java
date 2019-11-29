package hub_emulator.json.cash_info_report;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@Builder
@EqualsAndHashCode
public class OfdProvider {

    @SerializedName("name")
    public String name;

    @SerializedName("inn")
    public String inn;

    @SerializedName("server_host")
    public String serverHost;

    @SerializedName("server_port")
    public Integer serverPort;

    @SerializedName("check_url")
    public String checkUrl;

}
