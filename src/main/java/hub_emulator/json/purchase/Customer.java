package hub_emulator.json.purchase;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

@Getter
public class Customer {

    @SerializedName("phone")
    private String phone;

    @SerializedName("email")
    private String email;

}
