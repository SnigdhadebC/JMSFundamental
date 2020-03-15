package com.application.com.application.p2p.listeners;

import com.application.com.application.p2p.model.Passenger;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

import javax.jms.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class ReservationCheckMessageListener implements MessageListener {

    private InitialContext initialContext = null;
    private Queue requestQueue = null;
    private Queue replyQueue = null;
    private JMSContext jmsContext = null;

    public ReservationCheckMessageListener() throws NamingException {
        initialContext = new InitialContext();
        replyQueue = (Queue) initialContext.lookup("queue/replyQueue");
        ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory();
        jmsContext = cf.createContext();
    }

    @Override
    public void onMessage(Message message) {
        ObjectMessage data = (ObjectMessage) message;
        try {
            Passenger passenger = (Passenger) data.getObject();
            System.out.println("Passenger Details Received: "+passenger);

            MapMessage mapMessage = jmsContext.createMapMessage();
            mapMessage.setBoolean("isReserved",true);
            mapMessage.setInt("id",passenger.getId());
            mapMessage.setString("name",passenger.getFirstName()+" "+passenger.getLastName());

            jmsContext.createProducer().send(replyQueue,mapMessage);

        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
