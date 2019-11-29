package application_manager.api_manager.events.json.data.lcdData;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class XReportScreen {

    private String cashInFinal;
    private String advent;
    private String adventReturn;
    private String reserve;
    private String insertion;
    private String cashOnTop;

}
