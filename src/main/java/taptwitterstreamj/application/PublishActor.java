package taptwitterstreamj.application;

import akka.actor.UntypedActor;
import org.springframework.context.annotation.Scope;
import taptwitterstreamj.infrastructure.messaging.PublishMessage;
import taptwitterstreamj.infrastructure.MessageSender;

import javax.inject.Inject;
import javax.inject.Named;

@Named("PublishActor")
@Scope("prototype")
public class PublishActor extends UntypedActor {

    private final MessageSender messageSender;

    @Inject
    public PublishActor(@Named("MessageSender") MessageSender messageSender){
        this.messageSender = messageSender;
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if(message instanceof PublishMessage){
            messageSender.sendMessage(((PublishMessage) message).getEndpoint(), ((PublishMessage) message).getObj());
        } else{
            unhandled(message);
        }
    }

}
