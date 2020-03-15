package com.application;

import javax.jms.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Enumeration;

public class FirstTopic {
    public static void main(String[] args) {
        InitialContext initialContext = null;
        Connection connection = null;

        //1. Set up Initial Context Factory
        try {
            initialContext = new InitialContext();

            //2. Connection Factory
            ConnectionFactory connectionFactory = (ConnectionFactory) initialContext.lookup("ConnectionFactory");
            //3. Creating a connection
            connection = connectionFactory.createConnection();
            //4. Creating a session
            Session session = connection.createSession();

            //
            Topic topic = (Topic) initialContext.lookup("topic/myTopic");
            MessageProducer messageProducer = session.createProducer(topic);

            TextMessage message = session.createTextMessage("Message 1");
            messageProducer.send(message);
            //messageProducer.send(message1);

           /* QueueBrowser browser = session.createBrowser(queue);
            Enumeration enumeration = browser.getEnumeration();
            while (enumeration.hasMoreElements()){
                TextMessage msg = (TextMessage) enumeration.nextElement();
                System.out.println(msg.getText());
            }*/

            // Message Consumer
            connection.start();

            MessageConsumer consumer1 = session.createConsumer(topic);
            MessageConsumer consumer2 = session.createConsumer(topic);
            TextMessage messageReceived =(TextMessage) consumer1.receive(10000);
            System.out.println("Received in client : "+messageReceived.getText());

            TextMessage messageReceived1 =(TextMessage) consumer2.receive(10000);
            System.out.println("Received in client2 : "+messageReceived1.getText());
        } catch (NamingException e) {
            e.printStackTrace();
        } catch (JMSException e) {
            e.printStackTrace();
        }
        finally{
            try {
                initialContext.close();
            } catch (NamingException e) {
                e.printStackTrace();
            }

            try {
                connection.close();
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }


    }
}
