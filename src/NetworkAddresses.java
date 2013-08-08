

import java.net.InetSocketAddress;
import java.net.Socket;

public class NetworkAddresses {
	public static InetSocketAddress sockAddr = new InetSocketAddress("192.168.1.8", 9999);

	public static void bindSocket(Socket s) {
		System.out.println("BIND SOCKET");
	}
}
