package hub_emulator.json.cash_info_report;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class Agent {

    @SerializedName("type")
    public String type;

    @SerializedName("requisites")
    public Requisites requisites;

    public Agent(String type) {
        this.type = type;
    }
}
