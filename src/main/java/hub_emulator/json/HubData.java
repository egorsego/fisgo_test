package hub_emulator.json;

import com.google.gson.annotations.SerializedName;
import hub_emulator.json.cash_info_report.KktInfo;
import hub_emulator.json.cash_info_report.KktRegistrationInfo;
import hub_emulator.json.cash_info_report.ShopInfo;
import hub_emulator.json.cash_info_report.Version;
import hub_emulator.json.handshake.Methods;
import hub_emulator.json.license.License;
import hub_emulator.json.poll.TaskResults;
import hub_emulator.json.money_document_report.Documents;
import hub_emulator.json.purchase.Cashier;
import hub_emulator.json.purchase.Purchase;
import hub_emulator.json.registration_report.RegistrationData;
import hub_emulator.json.shift_document_report.Shift;
import hub_emulator.json.update.Parameters;
import hub_emulator.json.update.Patch;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Builder
@EqualsAndHashCode
@ToString
public class HubData {

    //************************************************* HANDSHAKE ******************************************************

    @SerializedName("methods")
    private Methods methods;

    //************************************************* REGISTER *******************************************************

    @SerializedName("model")
    private Model model;

    @SerializedName("pin")
    private String pin;

    //*********************************************** CASH INFO REPORT *************************************************

    @SerializedName("versions")
    private List<Version> versions;

    @SerializedName("shop_info")
    private ShopInfo shopInfo;

    @SerializedName("kkt_info")
    private KktInfo kktInfo;

    @SerializedName("kkt_registration_info")
    private KktRegistrationInfo kktRegistrationInfo;

    @SerializedName("jacarta_expire_date")
    private String jacartaExpireDate;

    @SerializedName("is_fn")
    private Boolean isFn;

    @SerializedName("kkt_expire_date")
    private String kktExpireDate;

    @SerializedName("local_time")
    private String localTime;

    //******************************************** REGISTRATION REPORT *************************************************

    @SerializedName("type")
    private String type;

    @SerializedName("reason")
    private Integer reason;

    @SerializedName("date")
    private String date;

    @SerializedName("cashier")
    private Cashier cashier;

    @SerializedName("registration_data")
    private RegistrationData registrationData;

    // НЕУДАЧНАЯ РЕГИСТРАЦИЯ

    @SerializedName("error")
    private Map<String, String> error;

    //*********************************************  COUNTERS REPORT****************************************************

    @SerializedName("cash")
    private Integer cash;

    @SerializedName("cashless")
    private Integer cashless;

    @SerializedName("receipts")
    private Integer receipts;

    @SerializedName("balance")
    private Integer balance;

    //******************************************* PURCHASE DOCUMENT REPORT *********************************************

    @SerializedName("purchases")
    private Purchase[] purchases;

    //********************************************* MONEY DOCUMENT REPORT **********************************************

    @SerializedName("document")
    private List<Documents> documents;

    //********************************************** SHIFT DOCUMENT REPORT *********************************************

    @SerializedName("shifts")
    private List<Shift> shifts;

    //*********************************************** SEARCH PRODUCT ***************************************************

    @SerializedName("barcode")
    private String barcode;

    //************************************************ LOAD GOODS ******************************************************

    @SerializedName("task_results")
    private TaskResults[] taskResults;

    @SerializedName("tasks")
    private TaskResults[] task;

    //*********************************************** LICENSE KEY ******************************************************

    @SerializedName("license")
    private License license;

    //*********************************************** WHO AM I ** ******************************************************

    private String owner;

    @SerializedName("product_count")
    private String productCount;

    //*********************************************** STATS ** *********************************************************

    @SerializedName("need_send_to_ofd")
    private Integer needSendToOfd;


    @SerializedName("fn_flags")
    private FnFlags fnFlags;

    //*********************************************** UPDATES **********************************************************

    private ArrayList<Patch> patches;

    //*********************************************** UPDATE BACKUP ****************************************************

    private String url;

    private String uuid;

    @SerializedName("fnNumber")
    private String fnNumber;

    private Parameters parameters;

    private String configuration;

}
