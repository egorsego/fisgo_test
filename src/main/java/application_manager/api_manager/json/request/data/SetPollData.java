package application_manager.api_manager.json.request.data;

import application_manager.api_manager.json.CommandEnum;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class SetPollData {

    @SerializedName("task_id")
    private int taskId;
    @SerializedName("command")
    private CommandEnum command;
    @SerializedName("poll_time")
    private int pollTime;

}
