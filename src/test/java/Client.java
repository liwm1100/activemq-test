import com.my.activemq.test.ServerListener;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import javax.jms.*;
import java.util.UUID;

/**
 * @author liwm
 * @Description: ${todo}
 * @date 2018/1/23 0023下午 17:18
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring-activemq.xml"})
public class Client {

    @Resource
    private JmsTemplate jmsTemplate;

    private String myQueue = "test.queue";

    @Test
    public void test(){
        for (int i=0;i<10;i++){
            final int no = i+1;
            ActiveMQTextMessage message = (ActiveMQTextMessage) jmsTemplate.sendAndReceive(myQueue, new MessageCreator() {
                public Message createMessage(Session session) throws JMSException {
                    TextMessage txtMsg = session.createTextMessage("你好" + no);
                    String correlationID = UUID.randomUUID().toString();
                    System.out.println("correlationID:" + correlationID);
                    txtMsg.setJMSCorrelationID(correlationID);
                    return txtMsg;
                }
            });
            try {
                System.out.println(message.getText());
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }
}
