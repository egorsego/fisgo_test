package application_manager.api_manager.json.response.data;

import lombok.*;

@AllArgsConstructor
@Getter
@Builder
@EqualsAndHashCode
@ToString
public class Position {
    GoodsResponse goodsResponse;
    PositionResponse positionResponse;
}
