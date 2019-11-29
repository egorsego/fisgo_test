package hub_emulator.json.update;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
@Builder
public class PatchVersion {

    private String to;

}
