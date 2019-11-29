package hub_emulator.json.cash_info_report;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

@Getter
public class Requisites {

    @SerializedName("AGENT_PHONE")
    public String agentPhone;

    @SerializedName("OPERATOR_PHONE")
    public String operatorPhone;

    @SerializedName("SUPPLIER_PHONE")
    public String supplierPhone;

    @SerializedName("AGENT_OPERATION")
    public String agentOperation;

    @SerializedName("OPERATOR_NAME")
    public String operatorName;

    @SerializedName("OPERATOR_ADDRESS")
    public String operatorAddress;

    @SerializedName("OPERATOR_INN")
    public String operatorInn;

    @SerializedName("OPERATOR_PAY_PHONE")
    public String operatorPayPhone;

}
