package application_manager.api_manager;

import lombok.Getter;

public enum CashBoxType {

    DREAMKAS_F("dreamkasf"),
    KASSA_F("kassaf"),
    PULSE_FA("pulsefa"),
    KASSA_RB("kassarb"),
    KASSA_BUS("kassaf"),
    SPUTNIK("");

    @Getter
    private String propertyFileName;

    CashBoxType(String propertyFileName) {
        this.propertyFileName = propertyFileName;
    }
}
