package com.my.activemq.test;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;

import javax.jms.*;

/**
 * @author liwm
 * @Description:
 * @date 2018/1/23 0023下午 17:07
 */
public class ServerListener implements MessageListener {

    @Autowired
    private ActiveMQConnectionFactory connectionFactory;

    private String myQueue = "test.queue";

    public void onMessage(Message message) {
        try {
            Connection connection = connectionFactory.createConnection();
            connection.start();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination adminQueue = session.createQueue(myQueue);

            //设置一个消息生成器响应来自客户端的消息，我们将从一个消息发送到从jmsreplytoheader字段发送到的目的地
            MessageProducer replyProducer = session.createProducer(null);
            replyProducer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

            TextMessage response = session.createTextMessage();
            if (message instanceof TextMessage) {
                TextMessage txtMsg = (TextMessage) message;
                String messageText = txtMsg.getText();
                response.setText(messageText+",liwm");
            }

            //从接收到的消息中设置相关ID为响应消息的相关ID
            //这可以让客户端识别该消息的响应
            //向服务器发送的一个未完成的消息
            response.setJMSCorrelationID(message.getJMSCorrelationID());

            //将响应发送到接收消息的JMSReplyTo字段指定的目的地，
            //这大概是客户创建的临时队列
            replyProducer.send(message.getJMSReplyTo(), response);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
