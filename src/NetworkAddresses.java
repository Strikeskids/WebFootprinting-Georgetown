import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;

public class NetworkAddresses {

	private static InetAddressNode addresses;
	private static final Object addressLock = new Object();

	public static void modifySocket(Socket s) {
		InetAddress add = nextAddress();
		try {
			s.bind(new InetSocketAddress(add, 0));
		} catch (IOException e) {
			System.out.println("Failed to bind socket");
		}
	}

	private static InetAddress nextAddress() {
		loadAddresses();
		synchronized (addressLock) {
			InetAddress ret = addresses.current;
			addresses = addresses.next;
			return ret;
		}
	}

	private static void loadAddresses() {
		if (addresses != null)
			return;
		synchronized (addressLock) {
			try {
				for (NetworkInterface net : Collections.list(NetworkInterface.getNetworkInterfaces())) {
					if (!net.getDisplayName().contains("lo")) {
						addresses = new InetAddressNode(net.getInetAddresses());
						break;
					}
				}
			} catch (SocketException e) {
				e.printStackTrace();
			}
		}
	}

	private static class InetAddressNode {
		private InetAddress current;
		private InetAddressNode next;

		public InetAddressNode(Enumeration<InetAddress> addresses) {
			if (!addresses.hasMoreElements())
				throw new IllegalArgumentException();
			current = addresses.nextElement();
			if (!addresses.hasMoreElements()) {
				next = this;
			} else {
				next = new InetAddressNode(addresses);
				InetAddressNode tmp = next;
				while (tmp.next != tmp) {
					tmp = tmp.next;
				}
				tmp.next = this;
			}
		}
	}
}
