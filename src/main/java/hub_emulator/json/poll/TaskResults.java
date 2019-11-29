package hub_emulator.json.poll;

import com.google.gson.annotations.SerializedName;
import hub_emulator.json.purchase.product.Meta;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TaskResults {

    @SerializedName("result")
    private String result;

    @SerializedName("task_id")
    private Integer taskId;

    @SerializedName("type")
    private String taskType;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private PollTaskData data;

    @SerializedName("error_code")
    private String errorCode;
}
