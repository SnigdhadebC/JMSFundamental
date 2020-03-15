package com.application.com.application.p2p;

import com.application.com.application.p2p.model.Passenger;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

import javax.jms.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class FlightCheckInApp {

    private InitialContext initialContext = null;
    private Queue requestQueue = null;
    private Queue replyQueue = null;
    private JMSContext jmsContext = null;

    public FlightCheckInApp() throws NamingException {
        initialContext = new InitialContext();
        requestQueue = (Queue) initialContext.lookup("queue/requestQueue");
        replyQueue = (Queue) initialContext.lookup("queue/replyQueue");
        ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory();
        jmsContext = cf.createContext();
    }

    public static void main(String[] args) {
        FlightCheckInApp checkinApp = null;
        try {
            checkinApp = new FlightCheckInApp();

            Passenger passenger = new Passenger(01,"Tom","Moseley","98********","tom@gmail.com");
            // Sending passenger data to Queue
            checkinApp.sendToQueue(passenger);

            // Get the response back , if the passenger seat is booked
            checkinApp.checkIfPassengerSeatIsBooked();

        } catch (NamingException | JMSException e) {
            e.printStackTrace();
        }
    }

    private void checkIfPassengerSeatIsBooked() throws JMSException {
        MapMessage mapMessage = (MapMessage)jmsContext.createConsumer(replyQueue).receive(5000);
        boolean isReserved = mapMessage.getBoolean("isReserved");
        int id = mapMessage.getInt("id");
        String name = mapMessage.getString("name");
        if(isReserved)
           System.out.println("The booking of the passenger having id: "
                   +id+" and name Mr/Ms "+name+ " is CONFIRMED");
       else
           System.out.println("The booking of the passenger having id : "
                   +id+" and name Mr/Ms "+name+ " is NOT CONFIRMED");
    }


    /**
     *
     * @param passenger
     */
    private void sendToQueue(Passenger passenger) throws JMSException {
        JMSProducer producer = jmsContext.createProducer();
        ObjectMessage objectMessage = jmsContext.createObjectMessage();
        objectMessage.setObject(passenger);

        producer.send(requestQueue,objectMessage);
    }
}
