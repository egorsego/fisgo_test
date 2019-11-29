package hub_emulator.json.money_document_report;

import com.google.gson.annotations.SerializedName;
import hub_emulator.json.purchase.Cashier;
import lombok.Getter;

@Getter
public class Documents {

    @SerializedName("shift")
    public Integer shift;

    @SerializedName("number")
    public Integer number;

    @SerializedName("date")
    public String date;

    @SerializedName("type")
    public String type;

    @SerializedName("cashier")
    public Cashier cashier;

    @SerializedName("sum")
    public Integer sum;


}
