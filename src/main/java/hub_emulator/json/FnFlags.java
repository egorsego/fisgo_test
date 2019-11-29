package hub_emulator.json;

import com.google.gson.annotations.SerializedName;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class FnFlags {

    @SerializedName("immediate_replacement")
    private boolean immediateReplacement;

    @SerializedName("resources_exhastion")
    private boolean resourcesExhastion;

    @SerializedName("mem_overflow")
    private boolean memOverflow;

    @SerializedName("critical_fn_err")
    private boolean criticaFnErr;

}
