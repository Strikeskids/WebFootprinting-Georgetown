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
						addresses = InetAddressNode.create(net.getInetAddresses());
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

		public InetAddressNode(InetAddress ad) {
			this.current = ad;
		}

		private static InetAddressNode create(Enumeration<InetAddress> addresses) {
			if (!addresses.hasMoreElements())
				throw new IllegalArgumentException();
			InetAddressNode first = null;
			InetAddressNode prev = null;
			InetAddressNode node = null;
			int count = 0;
			while (addresses.hasMoreElements()) {
				InetAddress add = addresses.nextElement();
				if (add.getHostName().contains("borrowed-icon.local"))
					continue;
				count++;
				node = new InetAddressNode(add);
				if (first == null)
					first = node;
				if (prev != null)
					prev.next = node;
			}
			System.out.printf("Loaded %d ip addresses%n", count);
			node.next = first;
			return first;
		}
	}
}
