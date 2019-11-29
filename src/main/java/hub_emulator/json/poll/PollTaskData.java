package hub_emulator.json.poll;

import application_manager.api_manager.json.response.data.Position;
import com.google.gson.annotations.SerializedName;
import hub_emulator.json.purchase.*;
import hub_emulator.json.purchase.product.AlcoholMeta;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
public class PollTaskData {

    @SerializedName("name")
    private String name;

    @SerializedName("item")
    private String item;

    @SerializedName("articles")
    private List<String> articles;

    @SerializedName("price")
    private Integer price;

    @SerializedName("tax")
    private String tax;

    @SerializedName("type")
    private String type;

    @SerializedName("alcohol")
    private Boolean alcohol;

    @SerializedName("alcohol_meta")
    private AlcoholMeta alcoholMeta;

    @SerializedName("scale")
    private Boolean scale;

    @SerializedName("precision")
    private Double precision;

    @SerializedName("measure")
    private String measure;

    @SerializedName("barcodes")
    private List<String> barcodes;

    @SerializedName("group_name")
    private String groupName;

    @SerializedName("rem_id")
    private String remId;

    @SerializedName("cashier")
    private Cashier cashier;

    @SerializedName("fn_number")
    private String fnNumber;

    @SerializedName("key")
    private String key;

    @SerializedName("tax_mode")
    private String taxMode;

    @SerializedName("positions")
    private ArrayList<Positions> positions;

    @SerializedName("tags")
    private ArrayList<Tags> tags;

    @SerializedName("payments")
    private ArrayList<Payments> payments;

    @SerializedName("attributes")
    private Attributes attributes;

    @SerializedName("total")
    private Total total;


}
