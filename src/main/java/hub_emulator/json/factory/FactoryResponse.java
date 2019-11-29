package hub_emulator.json.factory;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
@Builder
public class FactoryResponse {

    private String key;
    private String error;
    private Integer status;

}
