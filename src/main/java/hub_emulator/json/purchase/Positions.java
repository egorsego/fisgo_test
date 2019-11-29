package hub_emulator.json.purchase;

import com.google.gson.annotations.SerializedName;
import hub_emulator.json.purchase.product.Product;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.ArrayList;

@Builder
@Getter
@EqualsAndHashCode
public class Positions {

    @SerializedName("number")
    private int number;

    @SerializedName("quantity")
    private int quantity;

    @SerializedName("price")
    private int price;

    @SerializedName("discount")
    private int discount;

    @SerializedName("total")
    private long total;

    @SerializedName("name")
    private String name;

    @SerializedName("tax")
    private String tax;

    @SerializedName("tax_sum")
    private int taxSum;

    @SerializedName("type")
    private String type;

    @SerializedName("barcode")
    private String barcode;

    @SerializedName("excise_barcode")
    private String exciseBarcode;

    @SerializedName("discounts")
    private Discounts discounts;

    @SerializedName("product")
    private Product product;

    @SerializedName("article")
    private String article;

    @SerializedName("tags")
    private ArrayList<Tags> tags;
}
