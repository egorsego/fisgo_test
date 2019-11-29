package application_manager.api_manager.json.request;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class Request {

    private String uuid;

    @SerializedName("tasks")
    private List<TasksRequest> tasksRequestList;
}
