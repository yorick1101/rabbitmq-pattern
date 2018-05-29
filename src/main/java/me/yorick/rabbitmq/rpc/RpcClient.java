package me.yorick.rabbitmq.rpc;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

public class RpcClient {
	
	private static String replyQueueName;
	private static Channel channel;
	private static String requestExchangeName = "rpc_exchange";

	public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
		ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        Connection connection = factory.newConnection();
        channel = connection.createChannel();

        replyQueueName = channel.queueDeclare().getQueue();
        channel.exchangeDeclarePassive(requestExchangeName);
        
        channel.basicConsume(replyQueueName, false, new DefaultConsumer(channel) {
        	 @Override
             public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
        		 System.out.println("Get Reply:"+new String(body));
        	 }
        });
        String message = "Hello abc";
        call(message);
        System.out.println("Send Request:"+message);
	}

	
	public static String call(String message) throws IOException, InterruptedException  {
		
		
		final String corrId = UUID.randomUUID().toString();

        AMQP.BasicProperties props = new AMQP.BasicProperties
                .Builder()
                .correlationId(corrId)
                .replyTo(replyQueueName)
                .build();

        channel.basicPublish(requestExchangeName, "command", props, message.getBytes("UTF-8"));
        return message;
		
	}
}