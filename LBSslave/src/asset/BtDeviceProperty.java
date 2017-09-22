package asset;

import java.io.IOException;
import java.util.ArrayList;

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;

import tcpIp.SocketComm;

public class BtDeviceProperty extends Property {

	//member
	LocalDevice local;
	LocalDevice remote;

	//constructor
	public BtDeviceProperty() {
	}
	public BtDeviceProperty(IndoorLocation location, String name, ArrayList<String> function, int classify, LocalDevice local){
		super(location,name,function,classify);
		this.local = local;
	}

	//public method
	@Override
	public void readFunc(byte header, SocketComm sc){

	}

	public void setRemote(LocalDevice remote){
		this.remote = remote;
	}

	public String toString(){
		if(remote != null){
			return super.toString() + "LocalBtDevice:" + local.getBluetoothAddress() + "," + local.getFriendlyName() + "\n" + "RemoteBtDevice: " + remote.getBluetoothAddress() + "," + remote.getFriendlyName() + "\n";
		}else{
			return super.toString() + "LocalBtDevice:" + local.getBluetoothAddress() + "," + local.getFriendlyName() + "\n" + "RemoteBtDevice: null\n";
		}
	}

	//static method
	//自身のBt情報をPropertyに変換
	static BtDeviceProperty getLocalBtProperty(Property prop){
		try {
			return new BtDeviceProperty(prop.getLocation(),prop.getName(),prop.getFunction(),prop.getClassify(),LocalDevice.getLocalDevice());
		} catch (BluetoothStateException e) {
			System.err.println("BtDeviceProperty:getLocalBtProperty[error]:the Bluetooth system could not be initialized\n");
			e.printStackTrace();
			return null;
		}
	}

	//test method
	public static void main(String[] args) {
//		System.out.println(getLocalBtProperty(new IndoorLocation(0,0,0),"test").toString());
		try {
			RemoteDevice[] preknownDevices = LocalDevice.getLocalDevice().getDiscoveryAgent()
					.retrieveDevices(DiscoveryAgent.PREKNOWN);
			RemoteDevice[] cachedDevices = LocalDevice.getLocalDevice().getDiscoveryAgent()
					.retrieveDevices(DiscoveryAgent.CACHED);

			if (preknownDevices != null) {
				System.out.println("preknownDevices\n");
				for (int i = 0; i < preknownDevices.length; i++) {
					System.out.println(preknownDevices[i].getBluetoothAddress() + ":"
							+ preknownDevices[i].getFriendlyName(true) + "\n");
				}
			}
			if (cachedDevices != null) {
				System.out.println("cachedDevices\n");
				for (int i = 0; i < cachedDevices.length; i++) {
					System.out.println(cachedDevices[i].getBluetoothAddress() + ":"
							+ cachedDevices[i].getFriendlyName(true) + "\n");
				}
			}
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}
}
