package application_manager.api_manager.json.response.data;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@EqualsAndHashCode
@ToString
public class GoodsResponse {

    @SerializedName("agents_info")
    private String agentsInfo;

    @SerializedName("barcode")
    private String barcode;

    @SerializedName("goods_name")
    private String goodsName;

    @SerializedName("unit_name")
    private String unit_name;

    @SerializedName("directory_code")
    private String directoryCode;

    @SerializedName("goods_group_code")
    private String goodsGroupCode;

    @SerializedName("id_goods_group")
    private String idGoodsGroup;

    @SerializedName("goods_code")
    private String goodsCode;

    @SerializedName("rem_id")
    private String remId;

    @SerializedName("measure")
    private String measure;

    @SerializedName("dep_name")
    private String depName;

    @SerializedName("unit_price")
    private int unitPrice;

    @SerializedName("attributes")
    private int attributes;

    @SerializedName("precision")
    private int precision;

    @SerializedName("tax_number")
    private int taxNumber;

    @SerializedName("goods_type")
    private int goodsType;

    @SerializedName("article")
    private String article;

}
