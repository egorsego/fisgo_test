package hub_emulator.json.handshake;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

@Getter
public class Methods {

    @SerializedName("out")
    private Out out;

    @SerializedName("in")
    private In in;

}
