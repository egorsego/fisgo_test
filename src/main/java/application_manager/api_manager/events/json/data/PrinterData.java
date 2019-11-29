package application_manager.api_manager.events.json.data;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class PrinterData {

    @SerializedName("print_buffer")
    private ArrayList<String> printBuffer;

}

