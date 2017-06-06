package tcpIp;

import java.nio.channels.SocketChannel;

public abstract class SocketComm {
	public SocketChannel SChannel;
	abstract public void asyncSend(byte[] data);
}
