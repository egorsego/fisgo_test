package hub_emulator.json.update;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
@Builder
public class Update {

    private String version;
    private String url;
    private String md5;
    private Integer size;
    private Boolean barrier;

}

