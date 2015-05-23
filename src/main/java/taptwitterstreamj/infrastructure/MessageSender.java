package taptwitterstreamj.infrastructure;

import org.springframework.messaging.simp.SimpMessagingTemplate;

import javax.annotation.Resource;
import javax.inject.Named;

@Named("MessageSender")
public class MessageSender {

    @Resource
    private SimpMessagingTemplate template;

    public void sendMessage(String endpoint, Object o){
        template.convertAndSend(endpoint, o);
    }

}
