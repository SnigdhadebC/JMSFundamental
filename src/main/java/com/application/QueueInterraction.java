package com.application;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

import javax.jms.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class QueueInterraction {

    InitialContext initialContext = null;
    Queue requestQueue = null;
    Queue expiryQueue= null;
    ActiveMQConnectionFactory connectionFactory = null;
    JMSContext jmsContext = null;

    public QueueInterraction() throws NamingException {
        initialContext = new InitialContext();
        requestQueue = (Queue) initialContext.lookup("queue/requestQueue");
        expiryQueue = (Queue) initialContext.lookup("queue/expiryQueue");
        connectionFactory = new ActiveMQConnectionFactory();
        jmsContext = connectionFactory.createContext();
    }

    public void produce(){
        JMSProducer producer = jmsContext.createProducer();
        TextMessage message = jmsContext.createTextMessage("Hello World !!!");
        TemporaryQueue tempQueue = jmsContext.createTemporaryQueue();
        try {
            message.setJMSReplyTo(tempQueue);
            producer.send(requestQueue,message);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public void produceOnpriority() throws JMSException {
        JMSProducer producer = jmsContext.createProducer();
        producer.setTimeToLive(10000);
        producer.setDeliveryDelay(5000);
        for(int i = 0 ; i < 3 ; i++){
            //producer.setPriority(i);
            TextMessage message = jmsContext.createTextMessage("Message : "+i);
            message.setBooleanProperty("isUserEnabled",true); // assigning metadata
            message.setStringProperty("userToken","abcd1234"); // assigning metadata
            producer.send(requestQueue,message);
            System.out.println("Data sent..."+message.getText());
        }
    }

    public void consumeOnpriority() throws JMSException {
        JMSConsumer consumer = jmsContext.createConsumer(requestQueue);
        for(int i = 0 ; i < 3 ; i++){
            TextMessage msg = (TextMessage) consumer.receive();
            if(msg == null){
                TextMessage message = (TextMessage) jmsContext.createConsumer(expiryQueue).receive();
                System.out.println("Inside ExpiredQueue : "+message.getText());
            }else{
                System.out.println("User Enabled : "+msg.getBooleanProperty("isUserEnabled"));
                System.out.println("User Token : "+msg.getStringProperty("userToken"));
                System.out.println(msg.getText());
            }
        }
    }

    public void consume() throws JMSException {
        JMSConsumer consumer = jmsContext.createConsumer(requestQueue);
        TextMessage messageReceived = (TextMessage) consumer.receive();
        String messageConsumed = messageReceived.getText();
        System.out.println("Message Consumed :: "+messageConsumed);
        sendAcknowledgementToProducer(messageReceived);
    }

    private void sendAcknowledgementToProducer(TextMessage messageReceived) throws JMSException {
        try {
            Destination destination = messageReceived.getJMSReplyTo();
            String jmsMessageID = messageReceived.getJMSMessageID();
            System.out.println(jmsMessageID);

            TextMessage messageFromConsumer = jmsContext.createTextMessage("Reply from the Consumer :: Acknowledge !!!");
            messageFromConsumer.setJMSCorrelationID(jmsMessageID);
            JMSProducer prod = jmsContext.createProducer();
            prod.send(destination,messageFromConsumer);

            receiveAcknowledgementByProducer(destination);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    private void receiveAcknowledgementByProducer(Destination destination) throws JMSException {
        final JMSConsumer consumer = jmsContext.createConsumer(destination);
        TextMessage message = (TextMessage) consumer.receive();
        System.out.println("Acknowledgement Received by Producer :: "+message.getText());
    }

    public static void main(String[] args) {
        try {
            QueueInterraction queue = new QueueInterraction();
            queue.produceOnpriority();
            queue.consumeOnpriority();
        } catch (NamingException | JMSException e) {
            e.printStackTrace();
        }


    }
}
