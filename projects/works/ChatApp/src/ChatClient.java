import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class ChatClient {

	List<PrintWriter> partnerWriters = new ArrayList<>();
	List<Socket> sockets = new ArrayList<>();
	private Endpoint receive;


	public static void main(String[] args) throws IOException {
		ChatClient chatClient = new ChatClient();
		ChatServer server = new ChatServer(chatClient);
		System.out.println("I'm on " + args[0] + ", trying to connect to " + args[1]);
		Thread serverThread = new Thread(() -> {
			server.start(args[0]);
		});
		serverThread.setName("ServerThread");
		serverThread.start();
		chatClient.start(args[1], args[0]);
	}
	
	public void start(String target, String receive) throws UnknownHostException {
		this.receive = new Endpoint(receive);
		while (true) {
			try {		
				connectTo(new Endpoint(target));

				String fromUser = null;
				BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

				while (true) {
					fromUser = stdIn.readLine();
					if (fromUser != null) {
						for (PrintWriter partner : partnerWriters) {
							partner.println(fromUser);
						}
					}
				}
			} catch (IOException e) {
				System.err.println("Host seems to be unavailable");
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			} finally {
				for (Socket socket : sockets) {
					try {
						socket.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	public synchronized void connectTo(Endpoint endpoint) throws IOException {
		if (!hasConnectionWith(endpoint)) {
			Socket partnerSocket = new Socket(endpoint.getAddress(), endpoint.getPort());
			sockets.add(partnerSocket);
			PrintWriter writer = new PrintWriter(partnerSocket.getOutputStream(), true);
			partnerWriters.add(writer);
			BufferedReader in = new BufferedReader(new InputStreamReader(partnerSocket.getInputStream()));
			System.out.println("Connected to: " + endpoint);
			synchPartnerLists(writer, in);
		}
	}

	private boolean hasConnectionWith(Endpoint endpoint) {
		for (Socket socket : sockets) {
			if (socket.getInetAddress().equals(endpoint.getAddress()) && socket.getPort() == endpoint.getPort()) {
				return true;
			}
			if (socket.getLocalAddress().equals(endpoint.getAddress()) && receive.getPort() == endpoint.getPort()) {
				return true;
			}
		}
		return false;
	}

	private void synchPartnerLists(PrintWriter out, BufferedReader in)
			throws IOException, UnknownHostException {
		String morePartners = in.readLine();
		if (!morePartners.isEmpty()) {
			String[] split = morePartners.split(" ");
			for (String partner : split) {
				connectTo(new Endpoint(partner));
			}
		}

		out.println(receive);
	}

}
