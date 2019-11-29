package application_manager.api_manager.json.response;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

import java.util.List;

@Getter
public class Response {

    @SerializedName("tasks")
    List<TaskResponse> taskResponseList;

    @SerializedName("event")
    String event;

    @SerializedName("hub_mode")
    String hubMode;
}
