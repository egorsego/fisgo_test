package hub_emulator.json;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

@Getter
public class Model {

    @SerializedName("model")
    private String model;

    @SerializedName("readable_model")
    private String readableModel;

}
