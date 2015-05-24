package taptwitterstreamj.infrastructure.messaging;

import lombok.Data;

@Data
public class PublishMessage {

    private final String sessionId;
    private final String endpoint;
    private final Object obj;

}
