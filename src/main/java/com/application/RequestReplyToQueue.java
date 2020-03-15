package com.application;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.w3c.dom.Text;

import javax.jms.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class RequestReplyToQueue {


    public static void main(String[] args) throws NamingException {

        InitialContext context = new InitialContext();
        Queue queue = (Queue) context.lookup("queue/requestQueue");
        //Queue replyQueue = (Queue) context.lookup("queue/replyQueue");

        try (ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
             JMSContext jmsContext = connectionFactory.createContext();
        ) {
            JMSProducer jmsProducer = jmsContext.createProducer();
            TemporaryQueue replyQueue = jmsContext.createTemporaryQueue();
            TextMessage message = jmsContext.createTextMessage("Hello how are you ??");
            message.setJMSReplyTo(replyQueue);
            jmsProducer.send(queue, message);
            System.out.println(message.getJMSMessageID());

            // Consume
            JMSConsumer jmsConsumer = jmsContext.createConsumer(queue);
            TextMessage consumedMessage = (TextMessage) jmsConsumer.receive();
            System.out.println(consumedMessage.getText());

            JMSProducer producerOfConsumer = jmsContext.createProducer();
            TextMessage textMessage = (TextMessage) jmsContext.createTextMessage("Hey I am fine thank you for asking");
            textMessage.setJMSCorrelationID(consumedMessage.getJMSMessageID());
            producerOfConsumer.send(consumedMessage.getJMSReplyTo(), textMessage);

            JMSConsumer jmsConsumer1 = jmsContext.createConsumer(replyQueue);
            TextMessage msg = (TextMessage) jmsConsumer1.receive();
            System.out.println(msg.getJMSCorrelationID());
            System.out.println(msg.getText());

            /*String s = jmsContext.createConsumer(replyQueue).receiveBody(String.class);
            System.out.println("### : " + s);*/

        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
