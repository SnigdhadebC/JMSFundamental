package com.application;

import javax.jms.ConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.Queue;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class JMX2Queue {
    public static void main(String[] args) throws NamingException {

        //1. To load the initial context
        InitialContext initialContext = new InitialContext();
        ConnectionFactory connectionFactory =(ConnectionFactory) initialContext.lookup("ConnectionFactory");
        Queue queue = (Queue)initialContext.lookup("queue/myQueue");
        try(JMSContext jmsContext =  (JMSContext) connectionFactory.createContext();

        ){
            jmsContext.createProducer().send(queue,"Arise, Awake and Stop not till the goal is reached...");
            String result = jmsContext.createConsumer(queue).receiveBody(String.class,5000);
            System.out.println(result);
        }




    }
}
