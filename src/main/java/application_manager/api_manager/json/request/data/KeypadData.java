package application_manager.api_manager.json.request.data;

import application_manager.api_manager.json.request.data.enums.KeypadActionEnum;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class KeypadData {
    private int key1;
    private int key2;
    private KeypadActionEnum action;
}
