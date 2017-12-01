package asset;

import java.io.Serializable;

import tcpIp.SocketComm;

public class ContentProperty extends Property implements Classifier,Serializable{

	//コンテンツの親を示す
	private DeviceProperty parent;
	//コンテンツが何の種類なのかを示す
	private String type;

	@Override
	public void readFunc(byte header, SocketComm sc) {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public String getClassName() {
		return "ContentProperty";
	}

}
