package taptwitterstreamj.application;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.PoisonPill;
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

    private ActorSystem actorSystem;

    @MessageMapping("/getTweets")
    public void requestTweets(String parameters) throws Exception {
        actorSystem = context.getBean(ActorSystem.class);

        Map<String, Object> map = new ObjectMapper().readValue(parameters, HashMap.class);
        String keywordValues = (String) map.get("filterParameters");
        String[] keywords = keywordValues.split(" ");
        String sessionId = (String) map.get("sessionId");

        log.info(String.format("New user detected sessionId=%s, starting up actors", sessionId));

        ActorRef tweetMaster = actorSystem.actorFor("/user/TweetMaster");
        if (tweetMaster.isTerminated()) {
            tweetMaster = actorSystem.actorOf(SpringExtProvider.get(actorSystem).props("TweetMaster"), "TweetMaster");
            tweetMaster.tell(new FilterMessage(sessionId, keywords), null);
        } else {
            tweetMaster.tell(new FilterMessage(sessionId, keywords), null);
        }
    }

    @MessageMapping("/shutdown")
    public void shutdown(String parameters) throws Exception {
        Map<String, Object> map = new ObjectMapper().readValue(parameters, HashMap.class);
        String sessionId = (String) map.get("sessionId");

        log.info(String.format("User disconnected=%s, shutting down actors", sessionId));

        ActorRef streamActor = actorSystem.actorFor("/user/StreamActor-" + sessionId);
        ActorRef publishActor = actorSystem.actorFor("/user/PublishActor-" + sessionId);
        streamActor.tell(PoisonPill.getInstance(), null);
        publishActor.tell(PoisonPill.getInstance(), null);
    }

}
