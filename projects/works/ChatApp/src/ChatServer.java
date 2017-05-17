import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ChatServer {

	private List<ChatSession> sessions = new ArrayList<>();
	private ChatClient chatClient;

	public ChatServer(ChatClient chatClient) {
		this.chatClient = chatClient;
	}

	public void start(String access) {
		try (ServerSocket serverSocket = new ServerSocket(new Endpoint(access).getPort())) {
			while (true) {
				Socket clientSocket = serverSocket.accept();
				ChatSession session = new ChatSession(this, clientSocket, chatClient);
				sessions.add(session);
				session.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public List<Endpoint> getEndpoints() {
		List<Endpoint> ret = new ArrayList<>();
		for (ChatSession chatSession : sessions) {
			ret.add(chatSession.getEndpoint());
		}
		return ret;
	}
}
