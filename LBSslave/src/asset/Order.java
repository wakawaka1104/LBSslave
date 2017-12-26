package asset;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

import javax.imageio.ImageIO;

import com.github.sarxos.webcam.Webcam;

import tcpIp.SocketClient;
import tcpIp.SocketComm;

public class Order implements Serializable, Classifier {

	private static final long serialVersionUID = 1L;
	private String order = "";
	private ByteFile file;

	//A = 自分
	private Property propA;
	private Property propB;

	public Order(String order,Property a, Property b){
		this.order = order;
		this.propA = a;
		this.propB = b;
	}

	@Override
	public void readFunc(byte header, SocketComm sc) {
		switch (order) {
		case "file send":
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			try {
				ImageIO.write(ImageIO.read(new File("img"+File.separator+"Flower1.jpg")), "jpg", baos);
			} catch (IOException e) {
				System.err.println("Order.readFunc()[error]:can't find the file");
				e.printStackTrace();
			}
			sc.asyncSend(new ByteFile(baos.toByteArray(),"png"),(byte)0);
			
			System.out.println("File sent");
			break;
		case "camera":
//			if(functionSearch("camera")){
//			if(true){
				Webcam webcam = null;
//				webcam = Webcam.getDefault();
				webcam = Webcam.getWebcams().get(1);
				if (webcam != null) {
					System.out.println("Webcam : " + webcam.getName());
					webcam.open();
					SocketClient propBsocket = new SocketClient( ((TcpipDeviceProperty)propB).getIp(),((TcpipDeviceProperty)propB).getPort());
					Thread socketThreaad = new Thread(propBsocket);
					socketThreaad.start();

//					while(true){
						BufferedImage image = webcam.getImage();
						ByteArrayOutputStream _baos = new ByteArrayOutputStream();
						try {
							ImageIO.write(image, "PNG", _baos);
						} catch (IOException e) {
							// TODO 自動生成された catch ブロック
							e.printStackTrace();
						}
						propBsocket.asyncSend(new ByteFile(_baos.toByteArray(),"png"),(byte)1);
//					}
				} else {
					System.out.println("Failed: Webcam Not Found Error");
				}

//			}else if(functionSearch("display")){			}
			break;
		case "file receive":

			break;
		case "greeting":
			sc.asyncSend(new Message("["+ MyProperty.getName() +"]:Hello,Client!"), (byte)0);
			break;
		default:
			break;
		}

	}

	public void setFile(ByteFile file){
		this.file = file;
	}

	private boolean functionSearch(String key){
		ArrayList<String> tmp = MyProperty.getFunction();
		for(Iterator<String> it = tmp.iterator(); it.hasNext();){
			if(key.equals(it.next())){
				return true;
			}
		}
		return false;
	}


	@Override
	public String getClassName() {
		return "Order";
	}


	public static void main(String[] args) {
		Order tmp = new Order("test",
				new TcpipDeviceProperty(new IndoorLocation(0,0,0),"localhost",11111,"test",new ArrayList<String>(),0),
				new TcpipDeviceProperty(new IndoorLocation(0,0,0),"localhost",11111,"test",new ArrayList<String>(),0)
				);
		try {
			tcpIp.Converter.serialize(tmp, (byte)0);
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		System.out.println("end");
	}
}
