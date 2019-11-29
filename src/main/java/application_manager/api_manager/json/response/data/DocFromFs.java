package application_manager.api_manager.json.response.data;

import com.google.gson.annotations.SerializedName;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
@EqualsAndHashCode
public class DocFromFs {
    @SerializedName("docs_from_fs")
    private List<Tag> docsFromArray;

}
