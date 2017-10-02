package model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

interface CommandEvent {
	public void fire(Command command) throws Exception;
}

class Client {
	String ip;
	int processId;
	ObjectOutputStream oos;

	public Client(String ip, int processId) {
		this.ip = ip;
		this.processId = processId;
	}
}

class Server {
	private final Client[] clients = new Client[] { new Client("10.24.2.146", 1), new Client("10.24.1.181", 0) };
	private final CommandEvent onCommand;
	private final int port;

	public Server(int port, CommandEvent onCommand) throws IOException {
		this.onCommand = onCommand;
		this.port = port;
		acceptConnections();
	}

	public int numberOfClients() {
		return clients.length;
	}

	private void acceptConnections() throws IOException {
		ServerSocket server = new ServerSocket(port);

		new Thread(() -> {
			try {
				while (true) {
					Socket socket = server.accept();
					readCommands(socket);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			try {
				server.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}).start();
	}

	private void readCommands(Socket socket) throws IOException {
		ObjectInputStream oos = new ObjectInputStream(socket.getInputStream());

		new Thread(() -> {
			try {
				while (true) {
					Command command = (Command) oos.readObject();

					onCommand.fire(command);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}).start();
	}

	public void sendCommand(Command command, int pid) throws IOException {
		System.out.println("send: " + command);
		for (Client client : clients) {
			if (client.processId == pid) {
				client.oos.writeObject(command);
			}
		}
	}

	public void sendCommand(Command command) throws IOException {
		for (Client client : clients) {
			client.oos.writeObject(command);
		}
	}

	public void connectToClients() throws Exception {
		for (Client client : clients) {
			Socket socket = new Socket(client.ip, port);
			ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
			client.oos = oos;
		}
	}
}