package hub_emulator.response.enums;

import lombok.Getter;

public enum MethodsEnum {

    HANDSHAKE("/handshake/v2"),
    PURCHASE_DOCUMENT_REPORT("/purchase_document_report/v3"),
    MONEY_DOCUMENT_REPORT("/money_document_report/v1"),
    UNREGISTER("/unregister/v1"),
    SHIFT_DOCUMENT_REPORT("/shift_document_report/v1"),
    CASH_INFO_REPORT("/cash_info_report/v1"),
    POLL("/poll/v3"),
    KKT_REGISTER_INFO("/kkt_register_info/v1"),
    REGISTER("/register/v1"),
    REGISTRATION_REPORT("/registration_report/v1"),
    WHO_AM_I("/who_am_i/v1"),
    COUNTERS_REPORT("/counters_report/v1"),
    STATS("/stats"),
    SEARCH_PRODUCT("/search_product/v1"),
    FACTORY_ACTIVATE_KEY("/1c/get_activation_key"),
    FACTORY_START("/v2/test/start"),
    FACTORY_FINISH("/v2/test/finish"),
    UPDATE_FISGO_VERSION("/update/v2/projects/fisgo/products/kassaf/updates/"),
    UPDATE_BACKUP("/update/v2/projects/fisgo/products/kassaf/configs"),
    UPLOAD_FILE("/upload"),
    DOWNLOAD_NEW_VERSION("/download_new_version"),
    UPLOAD_BACKUP_FILE("/upload_backup");

    @Getter
    private final String path;

    MethodsEnum(String url) {
        this.path = url;
    }
}
