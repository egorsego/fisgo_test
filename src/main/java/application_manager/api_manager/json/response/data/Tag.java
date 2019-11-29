package application_manager.api_manager.json.response.data;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;

@Getter
@ToString
@EqualsAndHashCode
@Builder
public class Tag {

    @SerializedName("name")
    @EqualsAndHashCode.Exclude private String name;

    @SerializedName("tag")
    private Integer tag;

    @SerializedName("value")
    private Object value;

    @SerializedName("ofd_quittance")
    private ArrayList<Integer> ofdQuittance;

}
