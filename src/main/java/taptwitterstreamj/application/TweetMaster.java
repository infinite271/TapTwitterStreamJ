package taptwitterstreamj.application;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.UntypedActor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import taptwitterstreamj.infrastructure.messaging.FilterMessage;
import taptwitterstreamj.infrastructure.messaging.PublishMessage;

import javax.inject.Inject;
import javax.inject.Named;

import static taptwitterstreamj.extensions.SpringExtension.SpringExtProvider;

@Named("TweetMaster")
@Scope("prototype")
public class TweetMaster extends UntypedActor {

    @Inject
    private ApplicationContext context;

    @Override
    public void onReceive(Object message) throws Exception {

        ActorSystem system = context.getBean(ActorSystem.class);

        if (message instanceof FilterMessage) {
            ActorRef streamActor = system.actorFor("/user/StreamActor-" + ((FilterMessage) message).getSessionId());
            if (streamActor.isTerminated()) {
                streamActor = system.actorOf(SpringExtProvider.get(system).props("StreamActor"), "StreamActor-" + ((FilterMessage) message).getSessionId());
                streamActor.tell(message, getSelf());
            } else {
                streamActor.tell(message, getSelf());
            }
        } else if (message instanceof PublishMessage) {
            ActorRef publishActor = system.actorFor("/user/PublishActor-" + ((PublishMessage) message).getSessionId());
            if (publishActor.isTerminated()) {
                publishActor = system.actorOf(SpringExtProvider.get(system).props("PublishActor"), "PublishActor-" + ((PublishMessage) message).getSessionId());
                publishActor.tell(message, getSelf());
            } else {
                publishActor.tell(message, getSelf());
            }
        } else{
            unhandled(message);
        }
    }

}
