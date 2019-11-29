package hub_emulator.json.handshake;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

@Getter
public class Out {
    @SerializedName("handshake")
    private int handshake ;

    @SerializedName("purchase_document_report")
    private int purchaseDocumentReport;

    @SerializedName("money_document_report")
    private int moneyDocumentReport;

    @SerializedName("unregister")
    private int unregister;

    @SerializedName("shift_document_report")
    private int shiftDocumentReport;

    @SerializedName("cash_info_report")
    private int cashInfoReport;

    @SerializedName("poll")
    private int poll;

    @SerializedName("kkt_register_info")
    private int kktRegisterInfo;

    @SerializedName("register")
    private int register;

    @SerializedName("registration_report")
    private int registrationReport;

    @SerializedName("who_am_i")
    private int whoAmI;

    @SerializedName("counters_report")
    private int countersReport;

    @SerializedName("search_product")
    private int searchProduct;
}
