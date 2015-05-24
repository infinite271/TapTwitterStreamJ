package taptwitterstreamj.application;

import akka.actor.ActorRef;
import akka.actor.Cancellable;
import akka.actor.UntypedActor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import scala.concurrent.duration.Duration;
import taptwitterstreamj.infrastructure.messaging.FilterMessage;
import taptwitterstreamj.infrastructure.messaging.PublishMessage;
import taptwitterstreamj.infrastructure.assemblers.tweet.TweetToDomainAssembler;
import taptwitterstreamj.infrastructure.messaging.ShutdownMessage;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
@Named("StreamActor")
@Scope("prototype")
class StreamActor extends UntypedActor {

    private final TweetToDomainAssembler tweetToDomainAssembler;

    private TwitterStream twitterStream;
    private StatusListener statusListener;
    private String[] keywords;
    private String sessionId;
    private ActorRef sender;
    private Cancellable hashtagJob;
    private Cancellable keywordHashtagJob;
    private Map<String, Integer> hashtags;
    private Map<String, Integer> keywordHashtags;

    @Inject
    public StreamActor(@Named("TweetToDomainAssembler") TweetToDomainAssembler tweetToDomainAssembler) {
        this.tweetToDomainAssembler = tweetToDomainAssembler;
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof FilterMessage) {
            sender = getSender();
            hashtags = new ConcurrentHashMap<>();
            keywordHashtags = new ConcurrentHashMap<>();
            sessionId = ((FilterMessage) message).getSessionId();
            keywords = ((FilterMessage) message).getKeywords();
            hashtagJob = getContext().system().scheduler().schedule(Duration.create(10, TimeUnit.SECONDS), Duration.create(10, TimeUnit.SECONDS),
                    getSender(), new PublishMessage(sessionId, "/topic/hashtags/", hashtags), getContext().system().dispatcher(), getSelf());
            keywordHashtagJob = getContext().system().scheduler().schedule(Duration.create(10, TimeUnit.SECONDS), Duration.create(10, TimeUnit.SECONDS),
                    getSender(), new PublishMessage(sessionId, "/topic/keywordHashtags/", keywordHashtags), getContext().system().dispatcher(), getSelf());
            subscribe();
        } else if (message instanceof ShutdownMessage) {
            hashtagJob.cancel();
            keywordHashtagJob.cancel();
            twitterStream.shutdown();
            context().stop(self());
        } else {
            unhandled(message);
        }
    }

    public void subscribe() {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true);
        cb.setOAuthConsumerKey("XvyUI8RV6GAH9G3krkVVPWJ8W");
        cb.setOAuthConsumerSecret("C2eEPgQuqjrFAqbEgj2dQpEa2el92qbSzgpIhjRzVsXggy4K1D");
        cb.setOAuthAccessToken("489337546-34OfUgZHV5Q8KgpM1HkOVm47FbQrmd4SjJSYmPhm");
        cb.setOAuthAccessTokenSecret("6A0qHY8cdVS0znt82hScbxfPLMFzycG2GjPhpAn9MH36y");

        if (twitterStream != null) {
            twitterStream.cleanUp();
            twitterStream.shutdown();
        }

        twitterStream = new TwitterStreamFactory(cb.build()).getInstance();

        statusListener = new StatusListener() {
            @Override
            public void onStatus(Status status) {
                processHashtags(status);
                processKeywordHashtags();
                sender.tell(new PublishMessage(sessionId, "/topic/tweets/", tweetToDomainAssembler.assemble(status)), getSelf());
            }

            @Override
            public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
            }

            @Override
            public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
            }

            @Override
            public void onScrubGeo(long userId, long upToStatusId) {
            }

            @Override
            public void onStallWarning(StallWarning warning) {
            }

            @Override
            public void onException(Exception ex) {
            }
        };
        log.info("Setting up stream to filter by=" + Arrays.toString(keywords));
        FilterQuery fq = new FilterQuery();
        String language[] = {"en"};
        fq.track(keywords).language(language);
        twitterStream.addListener(statusListener);
        twitterStream.filter(fq);
    }

    private void processHashtags(Status status) {
        for (HashtagEntity hashtagEntity : status.getHashtagEntities()) {
            Integer result = hashtags.computeIfPresent("#" + hashtagEntity.getText(), (k, v) -> v + 1);
            if (result == null) {
                hashtags.put("#" + hashtagEntity.getText(), 1);
            }
        }
    }

    private void processKeywordHashtags() {
        for (Map.Entry entry : hashtags.entrySet()) {
            for (String keyword : keywords) {
                String key = (String) entry.getKey();
                if (key.equals("#" + keyword)) {
                    keywordHashtags.put(key, (Integer) entry.getValue());
                }
            }
        }
    }
}
