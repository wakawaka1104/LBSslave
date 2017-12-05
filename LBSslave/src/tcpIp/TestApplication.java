package tcpIp;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class TestApplication {

	public static void main(String[] args) {
//		String addr = "localhost";
//		SocketServer ss = new SocketServer(addr,22222);
//		Thread serverThread = new Thread(ss);
//		serverThread.start();


		try {
			System.out.println(InetAddress.getLocalHost().getHostAddress());
		} catch (UnknownHostException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

}
