package taptwitterstreamj.application;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import taptwitterstreamj.infrastructure.messaging.FilterMessage;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

import static taptwitterstreamj.extensions.SpringExtension.SpringExtProvider;

@Slf4j
@Controller
public class TweetController {

    @Inject
    private ApplicationContext context;

    @MessageMapping("/getTweets")
    public void requestTweets(String filterParameters) throws Exception {
        ActorSystem system = context.getBean(ActorSystem.class);

        Map<String, Object> map = new ObjectMapper().readValue(filterParameters, HashMap.class);
        String keywordValues = (String) map.get("filterParameters");
        String[] keywords = keywordValues.split(" ");
        String sessionId = (String) map.get("sessionId");

        log.info(String.format("New user detected sessionId=%s", sessionId));

        ActorRef tweetMaster = system.actorFor("/user/TweetMaster");
        if (tweetMaster.isTerminated()) {
            tweetMaster = system.actorOf(SpringExtProvider.get(system).props("TweetMaster"), "TweetMaster");
            tweetMaster.tell(new FilterMessage(sessionId, keywords), null);
        } else {
            tweetMaster.tell(new FilterMessage(sessionId, keywords), null);
        }
    }

}
