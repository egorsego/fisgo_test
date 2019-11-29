package kabinet_api.json;

import hub_emulator.json.purchase.Tags;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.ArrayList;

@Builder
@Getter
@EqualsAndHashCode
public class Position {

    private String name;
    private String type;
    private Integer quantity;
    private Integer price;
    private String tax;
    private ArrayList<Tags> tags;

}
