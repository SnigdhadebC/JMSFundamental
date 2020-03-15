package com.application;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

import javax.jms.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class RequestReply2Queue {

    public static void main(String[] args) throws NamingException {
        InitialContext initialContext = new InitialContext();
        Queue queue = (Queue) initialContext.lookup("queue/myQueue");

        try(ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
            JMSContext jmsContext = connectionFactory.createContext();
        ){
            JMSProducer jmsProducer = jmsContext.createProducer();
            TemporaryQueue temporaryQueue = jmsContext.createTemporaryQueue();
            TextMessage message = jmsContext.createTextMessage("JMS Producer Queue");
            message.setJMSReplyTo(temporaryQueue);
            jmsProducer.send(queue,message);
            System.out.println("Message Id: "+message.getJMSMessageID());


            // Consumer


        } catch (JMSException e) {
            e.printStackTrace();
        }


    }
}
