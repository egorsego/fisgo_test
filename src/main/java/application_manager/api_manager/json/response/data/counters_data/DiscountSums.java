package application_manager.api_manager.json.response.data.counters_data;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

@Getter
public class DiscountSums {

    @SerializedName("SALE")
    private double sale;

    @SerializedName("RET")
    private double ret;

//    @SerializedName("SALE")
//    private String sale;
//
//    @SerializedName("RET")
//    private String ret;

}
