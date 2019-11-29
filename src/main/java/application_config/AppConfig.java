package application_config;

import application_manager.api_manager.CashBoxType;
import org.aeonbits.owner.Config;

@Config.Sources("classpath:config.properties")
public interface AppConfig extends Config {

    @Key("cashbox.ip")
    String cashBoxIp();

    @Key("cashbox.type")
    CashBoxType cashBoxType();

    @Key("cashbox.sshPassword")
    String cashBoxSshPassword();

    @Key("hub.ip")
    String hubIp();

    @Key("hub.port")
    @DefaultValue("5011")
    int hubPort();

    @Key("app.startWithRebootFiscat")
    boolean startWithRebootFiscat();

}
