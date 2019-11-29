package application_manager.api_manager.json.response;

import application_manager.api_manager.json.request.data.enums.ConfigFieldsEnum;
import application_manager.api_manager.json.response.data.*;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
public class TaskResponse {

    @SerializedName("task_id")
    private int taskId;

    private String result;

    @SerializedName("lcd_screen")
    private int[] lcdScreen;

    @SerializedName("keypad_mode")
    private int keypadMode;

    @SerializedName("cfg_data")
    private Map<ConfigFieldsEnum, String> configData;

    @SerializedName("counters_data")
    private CountersResponse countersData;

    @SerializedName("loader_status")
    private String loaderStatus;

    @SerializedName("GoodsData")
    private GoodsResponse goodsData;

    @SerializedName("PositionData")
    private PositionResponse positionData;

    @SerializedName("leaf")
    private Leaf leafData;

    @SerializedName("discounts")
    private List<Discount> discounts;

    @SerializedName("purchase")
    private Purchase purchase;

    @SerializedName("date")
    private String date;

    private String message;

    @SerializedName("docs_from_fs")
    private List<List<Tag>> docFromFs;

    @SerializedName("last_fd_num")
    private int lastFdNum;

    @SerializedName("positions_cnt")
    private int countPosition;

    @SerializedName("date_from_fs")
    private String dateFromFs;
}
