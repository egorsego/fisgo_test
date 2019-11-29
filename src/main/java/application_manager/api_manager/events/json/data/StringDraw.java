package application_manager.api_manager.events.json.data;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class StringDraw {

    private String bright;
    @SerializedName("start_pos")
    @EqualsAndHashCode.Exclude
    private int startPos;
    @EqualsAndHashCode.Exclude
    @SerializedName("string_number")
    private int stringNumber;
    private String text;

}
