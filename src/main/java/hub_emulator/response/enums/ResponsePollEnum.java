package hub_emulator.response.enums;

import lombok.Getter;

public enum ResponsePollEnum {

    LOAD_GOODS("./src/main/resources/hub-responses/goods"),
    ACTIVATE_KEY("./src/main/resources/hub-responses/licenseKey"),
    INVALID_KEY("./src/main/resources/hub-responses/invalidLicenseKey"),
    INVALID_DATA_KEY("./src/main/resources/hub-responses/licenseKeyNoData"),
    EXTERNAL_PURCHASE("./src/main/resources/hub-responses/externalPurchase"),
    REGISTRATION(""),
    CHANGE_PARAMETERS(""),
    CLOSE_FN(""),
    CLOSE_FN_INCORRECT(""),
    CHANGE_PARAMETERS_AND_FN(""),
    NEED_REGISTERED(""),
    DEFAULT("");

    @Getter
    private String path;

    ResponsePollEnum(String path) { this.path = path; }
}
