package hub_emulator.json.license;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

@Getter
public class License {

    @SerializedName("services")
    private Services[] services;

}
