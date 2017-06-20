package tcpIp;

import java.nio.channels.SocketChannel;

import asset.Classifier;

public abstract class SocketComm {
	public SocketChannel channel;
	abstract public void asyncSend(byte[] data);
	abstract public void asyncSend(Classifier ob, byte header);
}
