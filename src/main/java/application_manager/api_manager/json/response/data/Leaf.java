package application_manager.api_manager.json.response.data;

import lombok.Getter;

import java.util.ArrayList;

@Getter
public class Leaf {

    private ArrayList<String> childs;
    private String parent;
    private String current;

}
