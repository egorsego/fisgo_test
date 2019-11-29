package application_manager.api_manager.json.request.data;

import application_manager.api_manager.json.request.data.enums.ConfigFieldsEnum;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class CfgData {

    private List<ConfigFieldsEnum> fields;

}
