package asset;

import tcpIp.SocketComm;

public interface Classifier {

	//受信時の挙動を表す関数
	public void readFunc(byte header, SocketComm sc);
	public String toString();
}
