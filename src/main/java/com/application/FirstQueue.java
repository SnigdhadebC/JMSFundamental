package com.application;

import javax.jms.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class FirstQueue {

    public static void main(String[] args) {
        InitialContext initialContext = null;
        Connection connection = null;
        try {
            initialContext = new InitialContext();
            ConnectionFactory factory = (ConnectionFactory) initialContext.lookup("ConnectionFactory");
            connection = factory.createConnection();
            Session session = connection.createSession();

            //Destination
            Queue queue = (Queue)initialContext.lookup("queue/myQueue");

            MessageProducer producer = session.createProducer(queue);
            Message message = session.createTextMessage("I am the creator of my destiny !!! I will be successful ...");
            producer.send(message);


            // Message Consumer
            Queue consumerQueue = (Queue) initialContext.lookup("queue/myQueue");
            MessageConsumer consumer = session.createConsumer(consumerQueue);
            connection.start(); // only used when reading by consumer
            TextMessage messageReceived = (TextMessage)consumer.receive(5000);
            System.out.println("Message Received : "+messageReceived.getText());





        } catch (NamingException e) {
            e.printStackTrace();
        } catch (JMSException e) {
            e.printStackTrace();
        }
        finally {
            if(initialContext != null){
                try {
                    initialContext.close();
                } catch (NamingException e) {
                    e.printStackTrace();
                }
            }
            if(connection != null){
                try {
                    connection.close();
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
