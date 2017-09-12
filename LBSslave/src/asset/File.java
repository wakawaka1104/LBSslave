package asset;

import java.io.Serializable;

import javax.swing.ImageIcon;

import tcpIp.SocketComm;

public class File implements Serializable, Classifier {

	private ImageIcon ii;
	private static final long serialVersionUID = 3L;

	@Override
	public void readFunc(byte header, SocketComm sc) {

	}

	public void setIi(ImageIcon ii) {
		this.ii = ii;
	}

}
