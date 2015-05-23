package taptwitterstreamj.infrastructure.assemblers.tweet;

import org.springframework.stereotype.Component;
import twitter4j.Status;

@Component("TweetToDomainAssembler")
public class TweetToDomainAssembler {

    public Tweet assemble(Status status) {
        return new Tweet(status.getUser().getScreenName(), status.getUser().getLocation(), status.getText());
    }

}
