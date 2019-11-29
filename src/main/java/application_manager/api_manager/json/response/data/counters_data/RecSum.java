package application_manager.api_manager.json.response.data.counters_data;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

@Getter
public class RecSum {

    @SerializedName("CANCELED")
    private String canceled;

    @SerializedName("DEFFERED")
    private String deffered;

    @SerializedName("INSERT")
    private String insert;

    @SerializedName("RESERVE")
    private String reserve;

}
