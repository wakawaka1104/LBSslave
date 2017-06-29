package tcpIp;

public class TestApplication {

	public static void main(String[] args) {
		String addr = "localhost";
		SocketServer ss = new SocketServer(addr,22222);
		Thread serverThread = new Thread(ss);
		serverThread.start();

	}

}
