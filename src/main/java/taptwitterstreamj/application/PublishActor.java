package taptwitterstreamj.application;

import akka.actor.UntypedActor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import taptwitterstreamj.infrastructure.messaging.PublishMessage;
import taptwitterstreamj.infrastructure.MessageSender;
import taptwitterstreamj.infrastructure.messaging.ShutdownMessage;

import javax.inject.Inject;
import javax.inject.Named;

@Slf4j
@Named("PublishActor")
@Scope("prototype")
public class PublishActor extends UntypedActor {

    private final MessageSender messageSender;

    @Inject
    public PublishActor(@Named("MessageSender") MessageSender messageSender) {
        this.messageSender = messageSender;
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof PublishMessage) {
            log.info(String.format("Publishing to client=%s", message));
            messageSender.sendMessage(((PublishMessage) message).getEndpoint() + ((PublishMessage) message).getSessionId(), ((PublishMessage) message).getMessageContent());
        } else if (message instanceof ShutdownMessage) {
            context().stop(self());
        } else {
            unhandled(message);
        }
    }

}
