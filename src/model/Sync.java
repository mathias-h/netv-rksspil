package model;

import java.io.IOException;

interface AccessCallback {
	public void call() throws Exception;
}

public class Sync {
	private final Server server;
	private final SyncCommand[] messages;
	private final int processId;
	private int timestamp;
	private int replies = 0;
	private AccessCallback onAccess = null;

	public Sync(Server server, int processId) {
		this.server = server;
		this.processId = processId;
		messages = new SyncCommand[server.numberOfClients() + 1];
	}

	public void request(AccessCallback onAccess) throws Exception {
		if (this.onAccess != null) {
			throw new Exception("alerady waiting");
		}
		SyncCommand request = new SyncCommand(SyncCommand.Type.REQUEST, timestamp, processId);
		server.sendCommand(request);
		messages[processId] = request;
		this.onAccess = onAccess;
	}

	private void sendReply(SyncCommand command) throws IOException {
		timestamp = 1 + Math.max(timestamp, command.timestamp);
		SyncCommand reply = new SyncCommand(SyncCommand.Type.REPLY, timestamp, processId);
		server.sendCommand(reply, command.processId);
	}

	private boolean isBefore(SyncCommand command) {
		if (timestamp == command.timestamp)
			return processId > command.processId;
		else
			return timestamp > command.timestamp;
	}

	public void handleSyncCommand(SyncCommand command) throws Exception {
		System.out.println("received: " + command);
		
		if (command.type == SyncCommand.Type.REQUEST) {
			if (onAccess == null) {
				sendReply(command);
			} else {
				timestamp = 1 + Math.max(timestamp, command.timestamp);
				SyncCommand reply = new SyncCommand(SyncCommand.Type.REPLY, timestamp, processId);

				messages[command.processId] = reply;

				if (isBefore(command)) {
					server.sendCommand(reply, command.processId);
				}
			}
		} else if (command.type == SyncCommand.Type.REPLY) {
			if (onAccess != null) {
				replies += 1;

				if (replies == messages.length - 1) {
					onAccess.call();

					onAccess = null;
					replies = 0;

					for (int i = 0; i < messages.length; i++) {
						if (messages[i] != null && messages[i].processId != processId && messages[i].type == SyncCommand.Type.REQUEST) {
							sendReply(command);
							messages[i] = null;
						}
					}
				}
			}
		}
	}
}
