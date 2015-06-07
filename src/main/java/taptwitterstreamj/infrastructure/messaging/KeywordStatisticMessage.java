package taptwitterstreamj.infrastructure.messaging;

public class KeywordStatisticMessage extends PublishMessage{

    public KeywordStatisticMessage(String sessionId, String endpoint, Object obj) {
        super(sessionId, endpoint, obj);
    }

}
