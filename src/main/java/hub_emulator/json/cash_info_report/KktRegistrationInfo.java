package hub_emulator.json.cash_info_report;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class KktRegistrationInfo {

    @SerializedName("registry_number")
    public String registryNumber;

    @SerializedName("tax_modes")
    public List<String> taxModes;

    @SerializedName("autonomic")
    public Boolean autonomic;

    @SerializedName("ofd_provider")
    public OfdProvider ofdProvider;

    @SerializedName("work_mode")
    public List<String> workMode;

    @SerializedName("agents")
    public List<Agent> agents;

    @SerializedName("sender_email")
    @EqualsAndHashCode.Exclude public String senderEmail;

    @SerializedName("automatic_device_number")
    public String automaticDeviceNumber;

}
