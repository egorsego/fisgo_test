package hub_emulator.json;

import com.google.gson.annotations.SerializedName;
import hub_emulator.json.factory.Fields;
import hub_emulator.json.update.Update;
import lombok.*;

import java.util.ArrayList;

@Getter
@Builder
@ToString
public class HubRequest {

    @SerializedName("data")
    private HubData data;

    @SerializedName("uuid")
    private String uuid;

    @SerializedName("real_url")
    private String url;

    @SerializedName("result")
    private String result;

    //___________________________________________ FACTORY FIELDS________________________________________________________
    private String partNumber;
    private String serialNumber;
    private String wlanMac;
    private Fields fields;
    private String key;
    private Integer status;

    //____________________________________________ UPDATE FIELDS________________________________________________________

    private ArrayList<Update> updates;

}
