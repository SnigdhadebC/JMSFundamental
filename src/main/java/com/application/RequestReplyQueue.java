package com.application;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSProducer;
import javax.jms.Queue;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class RequestReplyQueue {
    public static void main(String[] args) throws NamingException {

        InitialContext initialContext = new InitialContext();
        Queue requestQueue = (Queue) initialContext.lookup("queue/requestQueue");
        Queue replyQueue = (Queue) initialContext.lookup("queue/replyQueue");

        try(ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory();
            JMSContext jmsContext = factory.createContext();
        ) {
            JMSProducer producer = jmsContext.createProducer();
            producer.send(requestQueue,"I am the request !!!");

            JMSConsumer consumer = jmsContext.createConsumer(requestQueue);
            String request = consumer.receiveBody(String.class);
            System.out.println("### "+request);

            JMSProducer consumerProducer = jmsContext.createProducer();
            consumerProducer.send(replyQueue,"I acknowledge the reply");

            JMSConsumer consumer1 = jmsContext.createConsumer(replyQueue);
            System.out.println("### "+consumer1.receiveBody(String.class,5000));
        }
    }
}
