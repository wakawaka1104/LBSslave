package asset;

import tcpIp.SocketComm;

public class Message implements Classifier {

	private String message = "";

	public Message(String message){
		this.message = message;
	}

	@Override
	public void readFunc(byte header, SocketComm sc) {
		switch(header){
		case 0x00:
			//header == 0x00
			//デバグ用メッセージの表示
			System.out.println("[" + sc.SChannel.socket().getRemoteSocketAddress().toString() + "]" + message);
		}
	}

	public void setMessage(String message){
		this.message = message;
	}

}
