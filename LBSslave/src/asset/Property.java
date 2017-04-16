package asset;

import java.net.InetAddress;

public class Property {

	//magic number
	private static final int _port = 11111;
	//singleton pattern

	//member
	private static Property prop = new Property();
	private IndoorLocation lo;
	private InetAddress ip;
	private int port = _port;
	private String name;

	private Property(){
		try{
			ip = InetAddress.getLocalHost();
		}catch (Exception e) {
			// TODO: handle exception
		}
	}

	//getter/setter
	public static Property getInstance(){
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

	//デバグ用main
	public static void main(String[] args) {
		Property prop = Property.getInstance();
		System.out.println("location:" + prop.getLocation().toString() + "\n" + "IPAddress:" + prop.getIp().getHostAddress());
	}

}
