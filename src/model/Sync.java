package model;

import java.io.IOException;

interface AccessCallback {
	public void call() throws Exception;
}

public class Sync {
	private final Server server;
	private final SyncCommand[] messages;
	private int timestamp;
	private int processId;
	private int replys = 0;
	private AccessCallback onAccess = null;

	public Sync(Server server) {
		this.server = server;
		messages = new SyncCommand[server.numberOfClients()+1];
	}

	public void request(AccessCallback onAccess) throws IOException {
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

	private boolean isAfter(SyncCommand command) {
		if (timestamp == command.timestamp)
			return processId < command.processId;
		else
			return timestamp < command.timestamp;
	}

	public void handleSyncCommand(SyncCommand command) throws Exception {
		if (command.type == SyncCommand.Type.REQUEST) {
			if (onAccess == null) {
				sendReply(command);
			} else {
				timestamp = 1 + Math.max(timestamp, command.timestamp);
				SyncCommand reply = new SyncCommand(SyncCommand.Type.REPLY, timestamp, processId);

				messages[command.processId] = reply;

				if (!isAfter(command)) {
					server.sendCommand(reply, command.processId);
				}
			}
		} else if (command.type == SyncCommand.Type.REPLY) {
			replys += 1;

			if (replys == messages.length - 1) {
				onAccess.call();

				onAccess = null;
				replys = 0;

				for (int i = 0; i < messages.length; i++) {
					if (messages[i] != null && messages[i].type == SyncCommand.Type.REQUEST) {
						sendReply(command);
						messages[i] = null;
					}
				}
			}
		}
	}
}
