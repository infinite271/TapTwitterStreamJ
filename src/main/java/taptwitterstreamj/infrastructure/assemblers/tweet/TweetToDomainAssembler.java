package taptwitterstreamj.infrastructure.assemblers.tweet;

import taptwitterstreamj.aspects.Log;
import taptwitterstreamj.domain.Tweet;
import twitter4j.Status;

import javax.inject.Named;

@Named("TweetToDomainAssembler")
public class TweetToDomainAssembler {

    @Log
    public Tweet assemble(Status status) {
        return new Tweet(status.getUser().getScreenName(), status.getUser().getLocation(), status.getText());
    }

}
