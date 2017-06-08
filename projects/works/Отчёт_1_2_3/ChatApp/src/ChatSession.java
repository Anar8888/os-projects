import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;

public class ChatSession {

	private PrintWriter out;
	private BufferedReader in;
	private ChatServer chatServer;
	private ChatClient chatClient;

	public ChatSession(ChatServer chatServer, Socket clientSocket, ChatClient chatClient) throws IOException {
		this.chatServer = chatServer;
		this.chatClient = chatClient;
		out = new PrintWriter(clientSocket.getOutputStream(), true);
		in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
	}
	
	public void processInput(String input) {
		System.out.println(endpoint + " : " + input);
	}

	private Endpoint endpoint;

	public void start() {
		out.println(createWelcomeMsg());
		try {
			endpoint = new Endpoint(in.readLine());
			chatClient.connectTo(endpoint);
		} catch (IOException e) {
			e.printStackTrace();
		}
		new Thread(() -> {
			String inputLine;
			try {
				while ((inputLine = in.readLine()) != null) {
					processInput(inputLine);
				}
			} catch (SocketException e) {
				System.out.println("Connection lost with " + endpoint);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}).start();
	}

	private String createWelcomeMsg() {
		List<Endpoint> sessionIPs = chatServer.getEndpoints();
		sessionIPs.remove(endpoint);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < sessionIPs.size() - 1; i++) {
			sb.append(sessionIPs.get(i)).append(" ");
		}
		if (!sessionIPs.isEmpty()) {
			sb.append(sessionIPs.get(sessionIPs.size() - 1));
		}
		return sb.toString();
	}

	public Endpoint getEndpoint() {
		return endpoint;
	}

}
