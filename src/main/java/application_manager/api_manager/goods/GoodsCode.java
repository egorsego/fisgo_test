package application_manager.api_manager.goods;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class GoodsCode {

    private String GOODS_ID;
    private String HASH_VAL;
    private String TYPE;

}
