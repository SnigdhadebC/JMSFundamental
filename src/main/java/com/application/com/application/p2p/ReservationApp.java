package com.application.com.application.p2p;

import com.application.com.application.p2p.listeners.ReservationCheckMessageListener;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.Queue;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class ReservationApp {

    private InitialContext initialContext = null;
    private Queue requestQueue = null;
    private Queue replyQueue = null;
    private JMSContext jmsContext = null;

    public ReservationApp() throws NamingException {
        initialContext = new InitialContext();
        requestQueue = (Queue) initialContext.lookup("queue/requestQueue");
        replyQueue = (Queue) initialContext.lookup("queue/replyQueue");
        ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory();
        jmsContext = cf.createContext();
    }

    public static void main(String[] args) {
        try {
            ReservationApp reservationApp = new ReservationApp();
            // receive passenger data
            reservationApp.receivePassengerData();
            Thread.sleep(5000);

        } catch (NamingException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void receivePassengerData() throws NamingException {
        JMSConsumer jmsConsumer = jmsContext.createConsumer(requestQueue);
        jmsConsumer.setMessageListener(new ReservationCheckMessageListener());
    }

}
