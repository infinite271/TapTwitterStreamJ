package taptwitterstreamj.infrastructure.messaging;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public abstract class PublishMessage {

    private final String sessionId;
    private final String endpoint;
    private Object messageContent;

}
