package me.yorick.rabbitmq.exclusive;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import me.yorick.rabbitmq.QueueFactory;

public class QueueUser {

	public static void main(String[] args) throws IOException, TimeoutException {
		QueueFactory factory = new QueueFactory("localhost", 5672, "guest", "guest");
    	Connection conn = factory.createConnection();
    	Channel channel = conn.createChannel();
    	/*
    	exchange - the exchange to publish the message to
		routingKey - the routing key
		props - other properties for the message - routing headers etc
		body - the message body
    	*/
    	String exchangeName = "engine.1";
    	try {
    		channel.exchangeDeclarePassive(exchangeName);
    		channel.basicPublish(exchangeName, "command3", true, false, null, "Hello".getBytes());
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
   	}
}
