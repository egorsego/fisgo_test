package hub_emulator.json.cash_info_report;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@EqualsAndHashCode
@ToString
public class ShopInfo {

    @SerializedName("legal_name")
    public String legalName;

    @SerializedName("real_address")
    public String realAddress;

    @SerializedName("shop_name")
    @EqualsAndHashCode.Exclude public String shopName;

    @SerializedName("address")
    public String address;

    @SerializedName("inn")
    public String inn;

    @SerializedName("kpp")
    public String kpp;

}
