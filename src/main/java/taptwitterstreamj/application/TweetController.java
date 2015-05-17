package taptwitterstreamj.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import taptwitterstreamj.domain.Tweet;
import taptwitterstreamj.infrastructure.assemblers.tweet.TweetToDomainAssembler;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Controller
public class TweetController {

    @Autowired
    private SimpMessagingTemplate template;

    private TwitterStream twitterStream;
    private StatusListener statusListener;
    private String[] keywords;
    private Map<String, Integer> hashtags = new HashMap<>();

    private ScheduledExecutorService scheduledExecutorService;

    @Autowired
    private TweetToDomainAssembler tweetToDomainAssembler;

    @MessageMapping("/getTweets")
    public void requestTweets(String filterParameters) throws Exception {
        Map<String, Object> map = new ObjectMapper().readValue(filterParameters, HashMap.class);
        String keywordValues = (String) map.get("filterParameters");
        keywords = keywordValues.split(" ");

        scheduledExecutorService = Executors.newScheduledThreadPool(2);
        scheduledExecutorService.scheduleWithFixedDelay(this::publishHashTags, 10, 10, TimeUnit.SECONDS);
        scheduledExecutorService.scheduleWithFixedDelay(this::publishKeywordHashTags, 10, 10, TimeUnit.SECONDS);

        if (twitterStream != null) {
            log.info("Shutting down listener and cleaning up");
            twitterStream.shutdown();
            twitterStream.cleanUp();
        }
        startListening();
    }

    public void publishTweet(Tweet tweet) {
        template.convertAndSend("/topic/tweets", tweet);
    }

    public void publishHashTags() {
        template.convertAndSend("/topic/hashtags", hashtags);
    }

    public void publishKeywordHashTags(){
        Map<String, Integer> keywordHashtags = new HashMap<>();

        for(Map.Entry entry : hashtags.entrySet()){
            for(String keyword : keywords){
                String key = (String) entry.getKey();
                log.info("Comparing: " + key + " with: " + "#" + keyword);
                if(key.equals("#" + keyword)){
                    keywordHashtags.put(key, (Integer)entry.getValue());
                }
            }
        }

        template.convertAndSend("/topic/keywordHashtags", keywordHashtags);
    }

    public void startListening() {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true);
        cb.setOAuthConsumerKey("XvyUI8RV6GAH9G3krkVVPWJ8W");
        cb.setOAuthConsumerSecret("C2eEPgQuqjrFAqbEgj2dQpEa2el92qbSzgpIhjRzVsXggy4K1D");
        cb.setOAuthAccessToken("489337546-34OfUgZHV5Q8KgpM1HkOVm47FbQrmd4SjJSYmPhm");
        cb.setOAuthAccessTokenSecret("6A0qHY8cdVS0znt82hScbxfPLMFzycG2GjPhpAn9MH36y");

        twitterStream = new TwitterStreamFactory(cb.build()).getInstance();
        statusListener = new StatusListener() {
            @Override
            public void onStatus(Status status) {
                User user = status.getUser();

                String username = status.getUser().getScreenName();
                log.info(String.format("Username=%s", username));
                String profileLocation = user.getLocation();
                log.info(String.format("Location=%s", profileLocation));
                String content = status.getText();
                log.info(String.format("Content=%s", content + "\n"));

                for (HashtagEntity hashtagEntity : status.getHashtagEntities()) {
                    Integer result = hashtags.computeIfPresent("#" + hashtagEntity.getText(), (k, v) -> v + 1);
                    log.info("" + result);
                    if (result == null) {
                        log.info("Result was null so adding new entry to map");
                        hashtags.put("#" + hashtagEntity.getText(), 1);
                    }
                }

                publishTweet(tweetToDomainAssembler.assemble(status));
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

}
