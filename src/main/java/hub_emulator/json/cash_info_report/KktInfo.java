package hub_emulator.json.cash_info_report;

import com.google.gson.annotations.SerializedName;
import hub_emulator.json.license.License;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@EqualsAndHashCode
@ToString
public class KktInfo {

    @SerializedName("kkt_factory_number")
    public String kktFactoryNumber;

    @SerializedName("kkt_registry_name")
    public String kktRegistryName;

    @SerializedName("fn_number")
    @EqualsAndHashCode.Exclude public String fnNumber;

    @SerializedName("fn_registry_name")
    public String fnRegistryName;

    @SerializedName("ffd_version")
    public String ffdVersion;

    @SerializedName("fn_registered")
    public Boolean fnRegistered;

    @SerializedName("kkt_registered")
    public Boolean kktRegistered;

    @SerializedName("license")
    @EqualsAndHashCode.Exclude public License license;
}
