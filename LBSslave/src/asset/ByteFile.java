package asset;

import java.io.Serializable;

import javax.swing.ImageIcon;

import gui.FilePresentationWindow;
import tcpIp.SocketComm;

public class ByteFile implements Serializable, Classifier {

	private byte[] file;
	private String extension = "";
	private static final long serialVersionUID = 3L;

	public ByteFile(byte[] file, String extension){
		this.file = file;
		this.extension = extension;
	}

	@Override
	public void readFunc(byte header, SocketComm sc) {
		FilePresentationWindow fpw = new FilePresentationWindow();
		fpw.setImageIcon(new ImageIcon(file));
		fpw.setVisible(true);
	}

	public byte[] getFile() {
		return file;
	}

	public void setFile(byte[] file) {
		this.file = file;
	}

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

	@Override
	public String getClassName() {
		return "ByteFile";
	}


	//test method
//	public static void main(String[] args) {
//		ByteArrayOutputStream baos = new ByteArrayOutputStream();
//		try {
//			ImageIO.write(ImageIO.read(new File("img"+File.separator+"poputepic.png")), "png", baos);
//		} catch (IOException e) {
//			System.err.println("Order.readFunc()[error]:can't find the file");
//			e.printStackTrace();
//		}
//		System.out.println("start");
//		new ByteFile(baos.toByteArray(),"png").readFunc((byte)0, null);
//		System.out.println("end");
//	}


}
