package asset;

import java.io.Serializable;
import java.net.InetAddress;

import tcpIp.SlaveClient;
import tcpIp.SocketComm;

public class Property implements Classifier,Serializable{

	private static final long serialVersionUID = 1L;

	//member
	private IndoorLocation lo;
	private InetAddress ip;
	private int port;
	private String name;

	public void readFunc(byte header, SocketComm sc){

		switch(header){

		case 0x00:
			//header == 0x00
			//提供されたPropertyを持つ端末とtcpip通信を確立
			try{
				SlaveClient _sc = new SlaveClient(ip, port);
				Thread clientThread = new Thread(_sc);
				clientThread.start();
				sc.asyncSend(SlaveClient.addHeader(SlaveClient.serialize(new Message("Property Received"))));
			}catch (Exception e) {

			}

		default:

		}

	}

	public IndoorLocation getLocation() {
		return lo;
	}

	public InetAddress getIp() {
		return ip;
	}

	public int getPort() {
		return port;
	}

	public String getName() {
		return name;
	}

	//constructor
	public Property(){

	}

	public Property(IndoorLocation lo,InetAddress ip,int port,String name){
		this.lo = lo;
		this.ip = ip;
		this.port = port;
		this.name = name;
	}

	public Property(_Property prop){
		this.lo = prop.getLocation();
		this.ip = prop.getIp();
		this.port = prop.getPort();
		this.name = prop.getName();
	}

	public String toString(){
		return "Location:"+lo.toString()+"\n"+"ip:"+ip.toString()+"\n"+"port:"+port+"\n"+"name:"+name+"\n" ;
	}

}
