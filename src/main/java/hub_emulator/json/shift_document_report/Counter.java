package hub_emulator.json.shift_document_report;

import com.google.gson.annotations.SerializedName;
import hub_emulator.json.purchase.Payments;
import lombok.Getter;

import java.util.List;

@Getter
public class Counter {

    @SerializedName("type")
    public String type;

    @SerializedName("payments")
    public List<Payments> payments;

    @SerializedName("total_sum")
    public Integer totalSum;

}
