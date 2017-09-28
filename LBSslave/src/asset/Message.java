package asset;

import java.io.Serializable;

import tcpIp.SocketComm;

public class Message implements Serializable, Classifier {

	private static final long serialVersionUID = 1L;
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
			System.out.println("[" + sc.remoteAddress + "]" + message);

		}
	}

	public void setMessage(String message){
		this.message = message;
	}

	@Override
	public String getClassName() {
		return "Message";
	}

}
