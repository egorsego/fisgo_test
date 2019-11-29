package hub_emulator.json.registration_report;

import com.google.gson.annotations.SerializedName;
import hub_emulator.json.cash_info_report.KktRegistrationInfo;
import hub_emulator.json.cash_info_report.ShopInfo;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
@Builder
public class RegistrationData {

    @SerializedName("number_fd")
    private String numberFd;

    @SerializedName("fiscal_sign")
    private String fiscalSign;

    @SerializedName("shop_info")
    private ShopInfo shopInfo;

    @SerializedName("kkt_registration_info")
    private KktRegistrationInfo kktRegistrationInfo;

}
