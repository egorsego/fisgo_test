package hub_emulator.json.update;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@Builder
@EqualsAndHashCode
public class Parameters {

    @SerializedName("_ssuk")
    private String ssuk;

}

