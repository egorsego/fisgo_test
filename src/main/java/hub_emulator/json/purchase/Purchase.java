package hub_emulator.json.purchase;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

@Getter
public class Purchase {

    @SerializedName("cashier")
    private Cashier cashier;

    @SerializedName("date")
    private String date;

    @SerializedName("discount_sum")
    private String discountSum;

    @SerializedName("number")
    private int number;

    @SerializedName("payments")
    private Payments[] payments;

    @SerializedName("positions")
    private Positions[] positions;

    @SerializedName("shift")
    private int shift;

    @SerializedName("sum_without_discounts")
    private String sumWithouDiscounts;

    @SerializedName("total_sum")
    private String totalSum;

    @SerializedName("type")
    private String type;

    @SerializedName("rem_id")
    private String remId;

    @SerializedName("checking_site")
    private String checkingSite;

    @SerializedName("number_fn")
    private String numberFn;

    @SerializedName("number_fd")
    private String numberFd;

    @SerializedName("registry_number")
    private String registryNumber;

    @SerializedName("fiscal_sign")
    private String fiscalSign;

    @SerializedName("customer")
    private Customer customer;




}
