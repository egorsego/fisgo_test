package application_manager.api_manager.json.response.data;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;

@Getter
@ToString
@EqualsAndHashCode
@Builder
public class Purchase {

    @SerializedName("advance")
    private int advance;

    @SerializedName("buyer_email")
    private String buyerEmail;

    @SerializedName("cashier_inn_num")
    private String cashierInnNum;

    @SerializedName("cashier_mid_name")
    private String cashierMidName;

    @SerializedName("cashier_name")
    private String cashierName;

    @SerializedName("cashier_sur_name")
    private String cashierSurName;

    @SerializedName("clc_advance")
    private boolean clcAdvance;

    @SerializedName("clc_credit_pay")
    private boolean clcCreditPay;

    @SerializedName("credit")
    private int credit;

    @SerializedName("credit_pay")
    private int creditPay;

    @SerializedName("cur_tax_system")
    private int curTaxSystem;

    @SerializedName("date")
    @EqualsAndHashCode.Exclude private String date;

    @SerializedName("date_sync")
    private boolean dateSync;

    @SerializedName("delivery")
    private int delivery;

    @SerializedName("discountName")
    private String discountName;

    @SerializedName("discountSumPubl")
    private Integer discountSumPubl;

    @SerializedName("excess")
    private boolean excess;

    @SerializedName("id")
    private int id;

    @SerializedName("isCabinetSend")
    private boolean isCabinetSend;

    @SerializedName("isPaidCard")
    private boolean isPaidCard;

    @SerializedName("isPaidCash")
    private boolean isPaidCash;

    @SerializedName("kkt_email")
    private String kktEmail;

    @SerializedName("kkt_plant_num")
    private String kktPlantNum;

    @SerializedName("marginName")
    private String marginName;

    @SerializedName("marginSumPubl")
    private Integer marginSumPubl;

    @SerializedName("pos_count")
    private int posCount;

    @SerializedName("positions")
    private PositionResponse[] positions;

    @SerializedName("prepayment")
    private int prepayment;

    @SerializedName("received")
    private Integer received;

    @SerializedName("reciept_num")
    @EqualsAndHashCode.Exclude private int reciept_num;

    @SerializedName("rem_id")
    private String remId;

    @SerializedName("shift_num")
    @EqualsAndHashCode.Exclude private int shiftNum;

    @SerializedName("spec_clc_method")
    private boolean specClcMethod;

    @SerializedName("tel_number")
    private String telNumber;

    @SerializedName("total_sum_display")
    private Integer totalSumDisplay;

    @SerializedName("total_sum_receipt")
    private Integer totalSumReceipt;

    @SerializedName("type")
    private String type;
}
