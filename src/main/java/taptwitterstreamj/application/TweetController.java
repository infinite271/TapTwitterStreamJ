package taptwitterstreamj.application;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Controller
public class TweetController {

    @MessageMapping("/getTweets")
    public void requestTweets(String filterParameters) throws Exception {
        //log.info("TEMPLATE IS: " + template.toString());
        Map<String, Object> map = new ObjectMapper().readValue(filterParameters, HashMap.class);
        String keywordValues = (String) map.get("filterParameters");
        String[] keywords = keywordValues.split(" ");
        ActorSystem actorSystem = ActorSystem.create("ActorSystem");
        //ActorRef tweetMaster = actorSystem.actorOf(Props.create((TweetMaster.class)));
        //tweetMaster.tell(new FilterMessage(keywords), null);
    }

}
