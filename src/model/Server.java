package model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import game2017.Main;

interface CommandEvent {
	public void fire(Command command) throws Exception;
}

class Server {
	private static final String[] ips = new String[] {
			// "10.24.2.233",
			"10.24.1.94",
			// "10.24.3.193"
	};
	private final List<ObjectOutputStream> outputStreams = new ArrayList<>();
	private final CommandEvent onCommand;
	
	public Server(int port, CommandEvent onCommand) throws IOException {
		this.onCommand = onCommand;
		acceptConnections(port);
	}

	private void acceptConnections(int port) throws IOException {
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

	public void sendCommand(Command command) throws IOException {
		for (ObjectOutputStream oos : outputStreams) {
			oos.writeObject(command);
		}
	}

	public void connectToClients() throws Exception {
		for (String ip : ips) {
			System.out.println("connecting to " + ip);
			Socket socket = new Socket(ip, 3486);
			ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
			outputStreams.add(oos);
		}

		
	}
}