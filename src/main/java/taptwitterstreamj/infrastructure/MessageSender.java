package taptwitterstreamj.infrastructure;

import org.springframework.messaging.simp.SimpMessagingTemplate;

import javax.inject.Inject;
import javax.inject.Named;

@Named("MessageSender")
public class MessageSender {

    @Inject
    private SimpMessagingTemplate template;

    public void sendMessage(String endpoint, Object o){
        template.convertAndSend(endpoint, o);
    }

}
