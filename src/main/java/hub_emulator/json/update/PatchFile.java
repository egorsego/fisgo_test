package hub_emulator.json.update;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
@Builder
public class PatchFile {

    private Integer size;
    private String md5;
    private String path;
    private String url;

}
