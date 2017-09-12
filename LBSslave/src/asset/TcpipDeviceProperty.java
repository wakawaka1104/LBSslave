package asset;

import tcpIp.SocketClient;
import tcpIp.SocketComm;

public class TcpipDeviceProperty extends Property {
	//member
	private String ip;
	private int port;
	private SocketClient sc;

	//public func
	@Override
	public void readFunc(byte header, SocketComm sc) {
		this.sc = new SocketClient(ip,port);
		Thread clientThread = new Thread(this.sc);
		clientThread.start();
	}

	public TcpipDeviceProperty(IndoorLocation lo, String ip,int port,String name){
		super(lo,name);
		this.ip = ip;
		this.port = port;
	}

	public TcpipDeviceProperty(TcpipDeviceProperty prop){
		super(prop.getLocation(),prop.getName());
		this.ip = getIp();
		this.port = getPort();
	}

	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public SocketClient getSocketClient() {
		if(sc != null)	{
			return sc;
		}else{
			try{
					sc = new SocketClient(ip, port);
					Thread clientThread = new Thread(sc);
					clientThread.start();
					return sc;
				}catch (Exception e) {
					System.err.println("Property:getSocketClient:connection()[error]");
					return null;
			}
		}
	}
	public String toString(){
		return "Location:"+this.getLocation().toString()+"\n"+"ip:"+ip.toString()+"\n"+"port:"+port+"\n"+"name:"+this.getName()+"\n" ;
	}
}