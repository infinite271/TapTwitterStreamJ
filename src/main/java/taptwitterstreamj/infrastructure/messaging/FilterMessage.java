package taptwitterstreamj.infrastructure.messaging;

import lombok.Data;

@Data
public class FilterMessage {

    private final String sessionId;
    private final String[] keywords;

}
