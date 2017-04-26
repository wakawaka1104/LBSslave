package asset;

import java.io.Serializable;
import java.net.InetAddress;

public class Property implements Classifier,Serializable{

	private static final long serialVersionUID = 1L;

	//member
	private IndoorLocation lo;
	private InetAddress ip;
	private int port;
	private String name;

	public IndoorLocation getLo() {
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
