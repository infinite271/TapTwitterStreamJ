package taptwitterstreamj;

import akka.actor.UntypedActor;

import org.springframework.context.annotation.Scope;
import taptwitterstreamj.application.CountingService;
import taptwitterstreamj.infrastructure.MessageSender;
import taptwitterstreamj.infrastructure.assemblers.tweet.Tweet;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * An actor that can count using an injected CountingService.
 *
 * @note The scope here is prototype since we want to create a new actor
 * instance for use of this bean.
 */
@Named("CountingActor")
@Scope("prototype")
class CountingActor extends UntypedActor {

    public static class Count {
    }

    public static class Get {
    }

    // the service that will be automatically injected
    final CountingService countingService;
    final MessageSender messageSender;

    @Inject
    public CountingActor(@Named("CountingService") CountingService countingService, @Named("MessageSender") MessageSender messageSender) {
        this.countingService = countingService;
        this.messageSender = messageSender;
    }

    private int count = 0;

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof Count) {
            messageSender.sendMessage("/topic/tweets", new Tweet("test", "test", "test"));
            count = countingService.increment(count);
        } else if (message instanceof Get) {
            getSender().tell(count, getSelf());
        } else {
            unhandled(message);
        }
    }
}
