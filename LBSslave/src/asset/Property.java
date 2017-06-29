package asset;

import java.io.Serializable;

import tcpIp.SocketClient;
import tcpIp.SocketComm;

public class Property implements Classifier,Serializable{

	private static final long serialVersionUID = 1L;

	//member
	private IndoorLocation location;
	private String ip;
	private int port;
	private String name;

	public void readFunc(byte header, SocketComm sc){
		switch(header){
		case 0x00:
			//header == 0x00
			//提供されたPropertyを持つ端末とtcpip通信を確立
			try{
				SocketClient _sc = new SocketClient(ip, port);
				Thread clientThread = new Thread(_sc);
				clientThread.start();
				sc.asyncSend(new Message("Property Received"),(byte)0);
			}catch (Exception e) {
				System.err.println("Property:readFunc()[error]");
				return;
			}
		default:
		}
	}

	//constructor
	public Property(){

	}

	public Property(IndoorLocation lo, String ip,int port,String name){
		this.location = lo;
		this.ip = ip;
		this.port = port;
		this.name = name;
	}

	public Property(Property prop){
		this.location = prop.getLocation();
		this.ip = prop.getIp();
		this.port = prop.getPort();
		this.name = prop.getName();
	}

	//getter/setter
	public IndoorLocation getLocation() {
		return location;
	}
	public String getIp() {
		return ip;
	}
	public int getPort() {
		return port;
	}
	public String getName() {
		return name;
	}
	public void setLocation(IndoorLocation location) {
		this.location = location;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String toString(){
		return "Location:"+location.toString()+"\n"+"ip:"+ip.toString()+"\n"+"port:"+port+"\n"+"name:"+name+"\n" ;
	}

}
