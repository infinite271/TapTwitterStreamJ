package taptwitterstreamj.infrastructure.messaging;

public class HashtagMessage extends PublishMessage{

    public HashtagMessage(String sessionId, String endpoint, Object obj) {
        super(sessionId, endpoint, obj);
    }

}
