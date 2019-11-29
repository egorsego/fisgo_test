package application_manager.cashbox;

import application_config.AppConfig;
import application_manager.api_manager.CashBoxType;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.log4j.Log4j;
import org.aeonbits.owner.ConfigFactory;

@Getter
@ToString
@Log4j
public class CashBox {

    public static final int EVENTS_PORT = 3426;
    public static final int EMUL_FN_PORT = 3427;

    public static final int SSH_PORT = 22;
    public static final int TCP_PORT = 3425;
    public static final String SSH_NAME = "root";

    private Keyboard keyboard;
    private CashBoxType boxType;
    @Getter
    private  String ipAddr;
    private String sshPass;

    /**
     * Конструктор с ручной инициализацией параметров (тип кассы и ip кассы)
     *
     * @param boxType - тип кассы
     * @param ipAddr  - ip кассы
     */
    public CashBox(CashBoxType boxType, String ipAddr) {
        this.boxType = boxType;
        this.ipAddr = ipAddr;
        this.keyboard = new Keyboard(boxType);
        initSshPass(boxType);
    }

    /**
     * Конструктор с инициализацией параметров из файла "src/test/resources/cashBoxData.properties".
     */
    public CashBox() {
        initCashboxProperty();
        this.keyboard = new Keyboard(boxType);
    }

    /**
     * Инициализация типа и ip кассы. Используется в конструкторе без аргументов
     */
    private void initCashboxProperty() {
        AppConfig config = ConfigFactory.create(AppConfig.class);

        this.boxType= config.cashBoxType();
        this.ipAddr = config.cashBoxIp();

        String passFromConfig = config.cashBoxSshPassword();
        if( passFromConfig != null){
            this.sshPass = passFromConfig;
        } else {
            initSshPass(this.boxType);
        }
    }

    /**
     * Инициализация пароля для SSH соединения. Пароль зависит от типа кассы.
     *
     * @param boxType - тип кассы
     */
    private void initSshPass(CashBoxType boxType) {
        if (boxType.equals(CashBoxType.DREAMKAS_F) || boxType.equals(CashBoxType.PULSE_FA)) {
            this.sshPass = "root";
        } else if (boxType.equals(CashBoxType.KASSA_F) || boxType.equals(CashBoxType.KASSA_RB)) {
            this.sshPass = "324012";
        }
    }
}
