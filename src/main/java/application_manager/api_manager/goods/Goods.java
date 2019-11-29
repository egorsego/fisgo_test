package application_manager.api_manager.goods;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Goods {

    private String ID;
    private String PRECISION;
    private String MEASURE;
    private String NAME;
    private String BARCODE;
    private String GOODS_TYPE;
    private String PRICE;
    private String NDS;
    private String ATTRIBUTES;
    private String REM_ID;

}
