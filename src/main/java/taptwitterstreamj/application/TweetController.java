package taptwitterstreamj.application;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import taptwitterstreamj.aspects.Log;
import taptwitterstreamj.infrastructure.messaging.FilterMessage;
import taptwitterstreamj.infrastructure.messaging.ShutdownMessage;

import javax.inject.Inject;
import java.util.HashMap;

import static taptwitterstreamj.extensions.SpringExtension.SpringExtProvider;

@Slf4j
@Controller
public class TweetController {

    @Inject
    private ApplicationContext context;

    private ActorSystem actorSystem;

    @Log
    @MessageMapping("/getTweets")
    public void requestTweets(String parameters) throws Exception {
        actorSystem = context.getBean(ActorSystem.class);

        HashMap map = new ObjectMapper().readValue(parameters, HashMap.class);
        String keywordValues = (String) map.get("filterParameters");
        String[] keywords = keywordValues.split(" ");
        String sessionId = (String) map.get("sessionId");

        ActorRef tweetMaster = actorSystem.actorFor("/user/TweetMaster");
        if (tweetMaster.isTerminated()) {
            log.info("Starting up TweetMaster Actor");
            tweetMaster = actorSystem.actorOf(SpringExtProvider.get(actorSystem).props("TweetMaster"), "TweetMaster");
            tweetMaster.tell(new FilterMessage(sessionId, keywords), null);
        } else {
            log.info("TweetMaster Actor already exists, will not create one");
            tweetMaster.tell(new FilterMessage(sessionId, keywords), null);
        }
    }

    @Log
    @MessageMapping("/shutdown")
    public void shutdown(String parameters) throws Exception {
        actorSystem = context.getBean(ActorSystem.class);

        HashMap map = new ObjectMapper().readValue(parameters, HashMap.class);
        String sessionId = (String) map.get("sessionId");

        log.info("Shutting down Stream and Publish actors");

        ActorRef streamActor = actorSystem.actorFor("/user/StreamActor-" + sessionId);
        ActorRef publishActor = actorSystem.actorFor("/user/PublishActor-" + sessionId);

        streamActor.tell(new ShutdownMessage(), null);
        publishActor.tell(new ShutdownMessage(), null);
    }

}
