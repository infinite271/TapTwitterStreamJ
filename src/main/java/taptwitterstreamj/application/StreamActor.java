package taptwitterstreamj.application;

import akka.actor.ActorRef;
import akka.actor.Cancellable;
import akka.actor.UntypedActor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import scala.concurrent.duration.Duration;
import taptwitterstreamj.aspects.Log;
import taptwitterstreamj.domain.Tweet;
import taptwitterstreamj.infrastructure.messaging.*;
import taptwitterstreamj.infrastructure.assemblers.tweet.TweetToDomainAssembler;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
@Named("StreamActor")
@Scope("prototype")
public class StreamActor extends UntypedActor {

    private final TweetToDomainAssembler tweetToDomainAssembler;

    private ActorRef tweetMaster;
    private TwitterStream twitterStream;
    private StatusListener statusListener;
    private String[] keywords;
    private String sessionId;
    private Cancellable hashtagJob;
    private Cancellable keywordHashtagJob;
    private Map<String, Integer> hashtags;
    private Map<String, Integer> keywordStatistics;

    @Inject
    public StreamActor(@Named("TweetToDomainAssembler") TweetToDomainAssembler tweetToDomainAssembler) {
        this.tweetToDomainAssembler = tweetToDomainAssembler;
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof FilterMessage) {
            tweetMaster = getSender();
            hashtags = new ConcurrentHashMap<>();
            keywordStatistics = new ConcurrentHashMap<>();
            sessionId = ((FilterMessage) message).getSessionId();
            keywords = ((FilterMessage) message).getKeywords();
            hashtagJob = getContext().system().scheduler().schedule(Duration.create(10, TimeUnit.SECONDS), Duration.create(10, TimeUnit.SECONDS),
                    getSelf(), new HashtagMessage(sessionId, "/topic/hashtags/", null), getContext().system().dispatcher(), null);
            keywordHashtagJob = getContext().system().scheduler().schedule(Duration.create(10, TimeUnit.SECONDS), Duration.create(10, TimeUnit.SECONDS),
                    getSelf(), new KeywordStatisticMessage(sessionId, "/topic/keywordStatistics/", null), getContext().system().dispatcher(), null);
            subscribe();
        } else if (message instanceof HashtagMessage) {
            ((HashtagMessage) message).setMessageContent(deepCopyMap(hashtags));
            tweetMaster.tell(message, getSelf());
            hashtags.clear();
        } else if (message instanceof KeywordStatisticMessage) {
            ((KeywordStatisticMessage) message).setMessageContent(deepCopyMap(keywordStatistics));
            tweetMaster.tell(message, getSelf());
            keywordStatistics.clear();
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
                processKeywordStatistics(status.getText());
                Tweet tweet = tweetToDomainAssembler.assemble(status);
                log.info(String.format("New Tweet received=%s", tweet.toString()));
                tweetMaster.tell(new TweetMessage(sessionId, "/topic/tweets/", tweet), getSelf());
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

    @Log
    private void processHashtags(Status status) {
        for (HashtagEntity hashtagEntity : status.getHashtagEntities()) {
            Integer result = hashtags.computeIfPresent("#" + hashtagEntity.getText(), (k, v) -> v + 1);
            if (result == null) {
                hashtags.put("#" + hashtagEntity.getText(), 1);
            }
        }
    }

    @Log
    private void processKeywordStatistics(String tweetContent) {
        for (String keyword : keywords) {
            if (tweetContent.contains(keyword)) {
                Integer result = keywordStatistics.computeIfPresent(keyword, (k, v) -> v + 1);
                if (result == null) {
                    keywordStatistics.put(keyword, 1);
                }
            }
        }
    }

    private Map<String, Integer> deepCopyMap(Map<String, Integer> mapToCopy){
        Map<String, Integer> copy = new HashMap<>();
        for(Map.Entry<String, Integer> entry : mapToCopy.entrySet()){
            copy.put(entry.getKey(), entry.getValue());
        }
        return copy;
    }
}
