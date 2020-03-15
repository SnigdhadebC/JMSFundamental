package com.application;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

import javax.jms.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class MessagePriority {
    public static void main(String[] args) throws NamingException {

        InitialContext initialContext = new InitialContext();
        Queue queue = (Queue)initialContext.lookup("queue/myQueue");
        try(
                ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory();
                JMSContext jmsContext = cf.createContext();

                ){

            JMSProducer producer = jmsContext.createProducer();
            String[] messages = new String[3];
            messages[0] = "Message 1";
            messages[1] = "Message 2";
            messages[2] = "Message 3";

            //producer.setPriority(3);
            producer.send(queue,messages[0]);

            //producer.setPriority(1);
            producer.send(queue,messages[1]);

            //producer.setPriority(9);
            producer.send(queue,messages[2]);

            JMSConsumer consumer = jmsContext.createConsumer(queue);
            for(int i = 0; i < 3 ;i++){
                System.out.println(consumer.receive().getJMSPriority());
                //System.out.println(consumer.receive().getJMSMessageID());
            }

        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

}
