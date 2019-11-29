package application_manager.api_manager.json.response.data;

import application_manager.api_manager.json.response.data.counters_data.*;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
public class CountersResponse {

    @SerializedName("REC_CNTS")
    private Map<String,String> recCounts;

    @SerializedName("SHIFT_NUM")
    private int shiftNum;

    @SerializedName("NEXT_REC_NUM")
    private int nextRecNum;

//    @SerializedName("SALE_SUMS")
//    private PaymentMethods saleSums;
    @SerializedName("SALE_SUMS")
    private Map<String,String> saleSums;

    @SerializedName("SALE_CNTS")
    private Map<String,String> saleCounts;

//    @SerializedName("RET_SUMS")
//    private PaymentMethods returnSums;

    @SerializedName("RET_SUMS")
    private Map<String,String> returnSums;

    @SerializedName("RET_CNTS")
    private Map<String,String> returnCounts;

    @SerializedName("REC_SUMS")
    private RecSum recSums;

    @SerializedName("DISCOUNT_SUMS")
    private DiscountSums discountSums;

    @SerializedName("SALE_TAXS")
    private Taxs saleTaxs;

    @SerializedName("RET_TAXS")
    private Taxs retTaxs;

    @SerializedName("X_Z_DATA")
    private XZData xzData;

    @SerializedName("DEP_SALE_SUM")
    private List<String> depSaleSum;

    @SerializedName("DEP_RET_SUM")
    private List<String> depRetSum;

    @SerializedName("PURCHASE_CNT")
    private int purchaseCount;

    @SerializedName("RET_PURCHASE_CNT")
    private int returnPurchaseCount;

    @SerializedName("PURCHASE_SUMS")
    private PaymentMethods purchaseSums;

    @SerializedName("RET_PURCHASE_SUMS")
    private PaymentMethods returnPurchaseSums;

    @SerializedName("COR_DATA")
    private CorData corData;

}
