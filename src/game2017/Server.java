package game2017;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

class Connection {
	Socket socket;

	public Connection(Socket socket, Main main) {
		this.socket = socket;
		
		new Thread(() -> {
			try {
				ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
				
				while(true) {
					Command c = (Command) ois.readObject();
							
					if (c instanceof NameCommand) {
						NameCommand nc = (NameCommand) c;
						main.addPlayer(nc.name, nc.posX, nc.posY, nc.dir);
					} else if (c instanceof MoveCommand) {
						main.playerMoved((MoveCommand) c);
					} else {
						throw new Exception("invalid command type");
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}).start();
	}
	
	public void sendCommand(Command c) {
		new Thread(() -> {
			try {
				ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
				oos.writeObject(c);
				oos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}).start();
	}
}

public class Server {
	public static final String[] ips = new String[] {
		"10.24.2.233",
		"10.24.1.94",
		"10.24.3.193"
	};
	public static final List<Connection> connections = new ArrayList<>();
	public final Main main;
	
	public Server(Main main) throws Exception {
		this.main = main;
		ServerSocket s = new ServerSocket(1024);
		
		new Thread(() -> {
			try {
				while(true) {
					System.out.println("venter på connection");
					Socket client = s.accept();
					
					connections.add(new Connection(client, main));
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}).start();
	}
	
	public void connectToClients() throws Exception {
		for (String ip : ips) {
			new Socket(ip, 1024);
		}
	}
	
	public void sendCommand(Command c) {
		for (Connection conn : connections) {
			conn.sendCommand(c);
		}
	}
}
