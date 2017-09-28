package asset;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import javax.imageio.ImageIO;

import tcpIp.SocketComm;

public class Order implements Serializable, Classifier {

	private static final long serialVersionUID = 1L;
	private String order = "";

	public Order(String order){
		this.order = order;
	}
	public Order(Property prop){
		this.order = prop.getSelection();
	}

	@Override
	public void readFunc(byte header, SocketComm sc) {
		switch (order) {
		case "file send":
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			try {
				ImageIO.write(ImageIO.read(new File("img"+File.separator+"poputepic.png")), "png", baos);
			} catch (IOException e) {
				System.err.println("Order.readFunc()[error]:can't find the file");
				e.printStackTrace();
			}
			sc.asyncSend(new ByteFile(baos.toByteArray(),"png"),(byte)0);
			System.out.println("File sent");
			break;

		default:
			break;
		}
	}

	public void setMessage(String order){
		this.order = order;
	}
	@Override
	public String getClassName() {
		return "Order";
	}
}
