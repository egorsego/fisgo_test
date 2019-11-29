package kabinet_api.json;

import hub_emulator.json.purchase.*;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;

@Builder
@Getter
public class Receipt {

    private Integer deviceId;
    private String type;
    private String taxMode;
    private ArrayList<Position> positions;
    private ArrayList<Payments> payments;
    private ArrayList<Tags> tags;
    private Attributes attributes;
    private Total total;
    private Integer timeout;

}
