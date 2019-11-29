package hub_emulator.json.purchase;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Builder
@Getter
@EqualsAndHashCode
public class Attributes {

    private String phone;
    private String email;


}
