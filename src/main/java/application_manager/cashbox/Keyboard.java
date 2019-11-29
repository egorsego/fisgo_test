package application_manager.cashbox;

import application_manager.api_manager.CashBoxType;
import lombok.extern.log4j.Log4j;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;

@Log4j
public class Keyboard {

    private Map<String, String> keyValuesMap;

    /**
     * Конструктор инициализирует поле keyValuesMap в зависимости от типа кассы, так как значения кнопок на каждой кассе
     * разные
     * @param cashBoxType - тип кассы
     */
    Keyboard(CashBoxType cashBoxType) {
        initKeyboard(cashBoxType);
    }

    /**
     * Метод инициализации map keyValuesMap.
     * @param cashBoxType - тип кассы
     */
    private void initKeyboard(CashBoxType cashBoxType) {
        keyValuesMap = new HashMap<>();
        Properties properties = new Properties();
        try {
            properties.load(new FileReader("./src/main/resources/keys-properties/key"
                    + cashBoxType.getPropertyFileName() + ".properties"));
        } catch (IOException e) {
            log.error("IOException", e);
        }
        properties.forEach((k, v) -> keyValuesMap.put((String) k, (String) v));
    }

    /**
     * Метод для получения значения нужной кнопки
     * @param keyEnum - необходимая кнопка
     * @return - возвращает значение int кнопки
     */
    public int getKeyValue(KeyEnum keyEnum) {
        return Integer.decode(keyValuesMap.get(keyEnum.toString()));
    }

}
