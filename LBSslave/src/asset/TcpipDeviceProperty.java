package asset;

import java.io.Serializable;
import java.util.ArrayList;

import tcpIp.SocketComm;

public class TcpipDeviceProperty extends DeviceProperty implements Classifier,Serializable{
	//member
	private String ip;
	private int port;

	//public func
	@Override
	public void readFunc(byte header, SocketComm sc) {
		SlaveList.getInstance().slaveAdd(this);
	}

	public TcpipDeviceProperty(IndoorLocation location, String ip,int port,String name, ArrayList<String> function,int classify){
		super(location,name,function,classify);
		this.ip = ip;
		this.port = port;
	}

	public TcpipDeviceProperty(TcpipDeviceProperty prop){
		super(prop.getLocation(),prop.getName(),prop.getFunction(),prop.getClassify());
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

	public String toString(){
		return "Location:"+this.getLocation().toString()+"\n"+"ip:"+ip.toString()+"\n"+"port:"+port+"\n"+"name:"+this.getName()+"\n" ;
	}
	public String getClassName(){
		return "TcpIpProperty";
	}
}
