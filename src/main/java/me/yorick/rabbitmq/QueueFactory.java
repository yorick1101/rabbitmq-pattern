package me.yorick.rabbitmq;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class QueueFactory {
	
	private ConnectionFactory factory = new ConnectionFactory();
	
	public QueueFactory(String host, int port, String user, String password) {
		factory.setHost(host);
		factory.setPort(port);
		factory.setUsername(user);
		factory.setPassword(password);
		factory.setAutomaticRecoveryEnabled(true);
		factory.setRequestedHeartbeat(60);
	}

	public Connection createConnection() throws IOException, TimeoutException {
		return factory.newConnection();
	}	
	
}
