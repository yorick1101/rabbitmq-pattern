package me.yorick.rabbitmq.rpc;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

public class RpcServer {

	private static String requestExchangeName = "rpc_exchange";
	private static String routeKey = "command";

	public static void main(String[] args) throws IOException, TimeoutException {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");

		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();

		channel.exchangeDeclare(requestExchangeName, "direct", true, true, false, null);
		String queueName = channel.queueDeclare().getQueue();
		channel.queueBind(queueName, requestExchangeName, routeKey);
		
		Consumer consumer = new DefaultConsumer(channel) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
				AMQP.BasicProperties replyProps = new AMQP.BasicProperties
						.Builder()
						.correlationId(properties.getCorrelationId())
						.build();

				String response = "";
				try {
					String message = new String(body,"UTF-8");
					response = message + " Received";
				}
				catch (RuntimeException e){
					System.out.println(" [.] " + e.toString());
				}
				finally {
					channel.basicPublish( "", properties.getReplyTo(), replyProps, response.getBytes("UTF-8"));
				}
			}
		};
		channel.basicConsume(queueName, false, consumer);
	}

}