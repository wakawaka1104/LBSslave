package asset;

import java.io.Serializable;

import javax.swing.ImageIcon;

import gui.FilePresentationWindow;
import tcpIp.SocketComm;

public class File implements Serializable, Classifier {

	private ImageIcon ii;
	private static final long serialVersionUID = 3L;

	@Override
	public void readFunc(byte header, SocketComm sc) {
		switch(header){

		case 0x00:
			//imageicon
			FilePresentationWindow frame = new FilePresentationWindow();
			frame.ImagePresenter(ii);
			frame.setVisible(true);

		default:
		}

	}

	public void setIi(ImageIcon ii) {
		this.ii = ii;
	}

}
