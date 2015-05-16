package taptwitterstreamj.infrastructure.assemblers.tweet;

import org.springframework.stereotype.Component;
import taptwitterstreamj.domain.Tweet;
import twitter4j.Status;

@Component
public class TweetToDomainAssembler {

    public Tweet assemble(Status status) {
        return new Tweet(status.getUser().getScreenName(), status.getUser().getLocation(), status.getText());
    }

}
