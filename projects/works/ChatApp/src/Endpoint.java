import java.net.InetAddress;
import java.net.UnknownHostException;

public class Endpoint {

	public Endpoint(InetAddress address, Integer portNum) {
		this.address = address;
		this.portNum = portNum;
	}
	
	public Endpoint(String str) throws UnknownHostException {
		String[] addrPort = str.split(":");
		address = InetAddress.getByName(addrPort[0]);
		portNum = Integer.parseInt(addrPort[1]);
	}
	
	private InetAddress address;
	private Integer portNum;
	
	@Override
	public String toString() {
		return address.getHostAddress() + ":" + portNum;
	}
	
	public InetAddress getAddress() {
		return address;
	}
	
	public Integer getPort() {
		return portNum;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Endpoint)) {
			return false;
		}
		Endpoint other = (Endpoint) obj;
		return address.equals(other.address) && portNum.equals(other.portNum);
	}
	
}
