package me.yorick.rabbitmq.exclusivequeue;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import me.yorick.rabbitmq.QueueFactory;

/*
 * 
 *   Good to detect if the owner exists, 
 * 
 * 
 * 
 */


public class QueueOwner {

	public static void main(String[] args) throws IOException, TimeoutException {
		QueueFactory factory = new QueueFactory("localhost", 5672, "guest", "guest");
		Connection conn = factory.createConnection();
		Channel channel = conn.createChannel();

		/*
		 * durable - true if we are declaring a durable queue (the queue will survive a server restart)
		 * exclusive - true if we are declaring an exclusive queue (restricted to this connection)
		 * autoDelete - true if we are declaring an autodelete queue (server will delete it when no longer in use)
		 * arguments - other properties (construction arguments) for the queue
		 * 
		 */
		String exchangeName = "engine.1";
		channel.exchangeDeclare(exchangeName, "direct", true, true, false, null);
		String queueName1 = channel.queueDeclare().getQueue();
		channel.queueBind(queueName1, exchangeName, "command");
		String queueName2 = channel.queueDeclare().getQueue();
		channel.queueBind(queueName2, exchangeName, "command2");

		Consumer consumer1 = new DefaultConsumer(channel) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
					throws IOException {
				String message = new String(body, "UTF-8");
				System.out.println(" [x] Received1 '" + message + "'");
			}
		};

		Consumer consumer2 = new DefaultConsumer(channel) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
					throws IOException {
				String message = new String(body, "UTF-8");
				System.out.println(" [x] Received2 '" + message + "'");
			}
		};	

		channel.basicConsume(queueName1, true, consumer1);
		channel.basicConsume(queueName2, true, consumer2);
	}

}
