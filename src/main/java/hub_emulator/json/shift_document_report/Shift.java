package hub_emulator.json.shift_document_report;

import com.google.gson.annotations.SerializedName;
import hub_emulator.json.purchase.Cashier;
import lombok.Getter;

import java.util.List;

@Getter
public class Shift {

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

    @SerializedName("sum_cash_begin")
    public Integer sumCashBegin;

    @SerializedName("sum_cash_end")
    public Integer sumCashEnd;

    @SerializedName("count_cash_out")
    public Integer countCashOut;

    @SerializedName("count_cash_in")
    public Integer countCashIn;

    @SerializedName("counters")
    public List<Counter> counters;

}
