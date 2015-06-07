package taptwitterstreamj.infrastructure.messaging;

public class TweetMessage extends PublishMessage{

    public TweetMessage(String sessionId, String endpoint, Object obj) {
        super(sessionId, endpoint, obj);
    }

}
