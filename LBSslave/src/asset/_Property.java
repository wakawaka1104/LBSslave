package asset;

import java.io.Serializable;
import java.net.InetAddress;

public class _Property implements Serializable,Classifier{

	//magic number
	private static final int _port = 11111;
	//singleton pattern

	private static final long serialVersionUID = 1L;

	//member
	private static _Property prop = new _Property();
	private IndoorLocation lo;
	private InetAddress ip;
	private int port = _port;
	private String name;

	private _Property(){
		try{
			ip = InetAddress.getLocalHost();
		}catch (Exception e) {
			// TODO: handle exception
		}
	}

	public void readFunc(byte header){
		
	}


	//getter/setter
	public static _Property getInstance(){
		return prop;
	}
	public IndoorLocation getLocation() {
		return lo;
	}
	public void setLocation(IndoorLocation lo) {
		this.lo = lo;
	}
	public InetAddress getIp() {
		return ip;
	}
	public void setIp(InetAddress ip) {
		this.ip = ip;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String toString(){
		return "Location:"+lo.toString()+"\n"+"ip:"+ip.toString()+"\n"+"port:"+port+"\n"+"name:"+name+"\n" ;
	}

	//デバグ用main
	public static void main(String[] args) {
		_Property prop = _Property.getInstance();
		System.out.println("location:" + prop.getLocation().toString() + "\n" + "IPAddress:" + prop.getIp().getHostAddress());
	}

}
