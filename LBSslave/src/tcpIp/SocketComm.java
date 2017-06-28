package tcpIp;

import asset.Classifier;

public abstract class SocketComm {
	public String remoteAddress = "";
	abstract public void asyncSend(byte[] data);
	abstract public void asyncSend(Classifier ob, byte header);
}
