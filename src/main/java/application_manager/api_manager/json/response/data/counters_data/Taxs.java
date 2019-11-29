package application_manager.api_manager.json.response.data.counters_data;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

@Getter
public class Taxs {


    @SerializedName("NDS_18")
    String nds18;

    @SerializedName("NDS_10")
    String nds10;

    @SerializedName("NDS_0")
    String nds0;

    @SerializedName("WITHOUT_NDS")
    String withoutNds;

    @SerializedName("NDS_18_118")
    String nds18_118;

    @SerializedName("NDS_10_110")
    String nds10_110;

}
