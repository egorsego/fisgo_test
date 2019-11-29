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
public class PositionResponse {

   private String agents_info;
   private String discountName;
   private Integer discountSum;
   private String marginName;
   private String marginSum;
   private String pos_num;
   private String clc_full_prepayment;
   private String clc_full_credit;
   private String clc_credit;
   private String clc_pl_agent;
   private String prepayment;
   private String credit;
   private String quantity;

   @SerializedName("pos_cost")
   private Integer posCost;

    @SerializedName("total_pos_sum")
   private Integer totalPosSum;

    @SerializedName("pos_sum_display")
   private Integer posSumDisplay;

   private Integer tax_sum;
   private String mode;
   private String mode_value;

}
