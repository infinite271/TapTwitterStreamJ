package taptwitterstreamj.infrastructure.assemblers.tweet;

import twitter4j.Status;

import javax.inject.Named;

@Named("TweetToDomainAssembler")
public class TweetToDomainAssembler {

    public Tweet assemble(Status status) {
        return new Tweet(status.getUser().getScreenName(), status.getUser().getLocation(), status.getText());
    }

}
