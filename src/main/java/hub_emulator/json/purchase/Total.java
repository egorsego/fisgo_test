package hub_emulator.json.purchase;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Builder
@Getter
@EqualsAndHashCode
public class Total {

    @SerializedName("total_sum")
    private Integer totalSum;
    @SerializedName("taxes_sum")
    private TaxesSum taxesSum;

    /**
     * Поле необходимое для Кабинет-API
     */
    private Integer priceSum;
}
