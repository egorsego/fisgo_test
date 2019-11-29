package hub_emulator.json.factory;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class FactoryRequest {

    private String partNumber;
    private String serialNumber;

}
