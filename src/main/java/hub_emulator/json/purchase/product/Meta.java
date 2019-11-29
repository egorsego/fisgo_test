package hub_emulator.json.purchase.product;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@EqualsAndHashCode
public class Meta {

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
}
