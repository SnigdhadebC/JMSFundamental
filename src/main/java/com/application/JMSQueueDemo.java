package com.application;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

import javax.jms.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class JMSQueueDemo {

    InitialContext initialContext = null;
    Queue queue = null;
    JMSContext jmsContext = null;

    JMSQueueDemo() throws NamingException {
        initialContext  = new InitialContext();
        queue = (Queue) initialContext.lookup("queue/requestQueue");
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
        jmsContext = connectionFactory.createContext();
    }

    public void send() throws JMSException {
        JMSProducer jmsProducer = jmsContext.createProducer();
        TemporaryQueue replyQueue = jmsContext.createTemporaryQueue();
        BytesMessage bytesMessage = jmsContext.createBytesMessage();
        bytesMessage.writeUTF("Rise Above and Stop Not till the goal is reached....");
        bytesMessage.setJMSReplyTo(replyQueue);
        jmsProducer.send(queue,bytesMessage);
    }


    public void consume() throws JMSException {
        JMSConsumer jmsConsumer = jmsContext.createConsumer(queue);
        BytesMessage bytesMessage = (BytesMessage) jmsConsumer.receive();
        System.out.println("Message Recived by Consumer :: "+bytesMessage.readUTF());

        Destination destination = bytesMessage.getJMSReplyTo();
        MapMessage mapMessage = jmsContext.createMapMessage();
        mapMessage.setBoolean("isUserValid",true);
        mapMessage.setObject("tokenId","1234abcd");

        String messageId = bytesMessage.getJMSMessageID();
        mapMessage.setJMSCorrelationID(messageId);
        JMSProducer jmsProducer = jmsContext.createProducer();
        jmsProducer.send(destination,mapMessage);
        System.out.println("------------------------------------------------------------");
        acknowledmentByProducer(destination);
    }

    private void acknowledmentByProducer(Destination destination) throws JMSException {
        MapMessage message = (MapMessage)jmsContext.createConsumer(destination).receive();
        System.out.println("isUserValid :: "+message.getBoolean("isUserValid"));
        System.out.println("token id :: "+(String)message.getObject("tokenId"));
    }

    public static void main(String[] args) {
        try {
            JMSQueueDemo jmsDemo = new JMSQueueDemo();
            jmsDemo.send();
            jmsDemo.consume();
        } catch (NamingException | JMSException e) {
            e.printStackTrace();
        }


    }
}
