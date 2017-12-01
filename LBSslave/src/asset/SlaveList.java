package asset;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

import tcpIp.SocketComm;

public class SlaveList implements Classifier,Serializable{

	//singleton pattern
	private static SlaveList sl = new SlaveList();

	private ArrayList<DeviceProperty> slaveList = new ArrayList<DeviceProperty>();

	//constructor
	private SlaveList(){
	}


	@Override
	public void readFunc(byte header, SocketComm sc) {

	}

	//public func
	//IndoorLocationに最も近いSlaveを検索
	//返り値は相当するSlaveのProperty
	//存在しないときはnullをreturn
	public static DeviceProperty slaveSearch(IndoorLocation locate){
		DeviceProperty nearest = null;
		//遠すぎる場合は見ない
		double distMin = Constant.THRETHOLD;
		for(Iterator<DeviceProperty> it = SlaveList.getInstance().slaveList.iterator(); it.hasNext();){
			DeviceProperty tmp = it.next();
			double dist = tmp.getLocation().dist(locate);
			if (distMin > dist ){
				//最小値を更新したとき
				nearest = tmp;
				distMin = dist;
			}
		}
		return nearest;
	}

    // 名前検索
    public static DeviceProperty slaveSearch(String deviceName){
        for(Iterator<DeviceProperty> it = SlaveList.getInstance().slaveList.iterator(); it.hasNext();){
            DeviceProperty tmp = it.next();
            if(tmp.getName()==deviceName){
                return tmp;
            }
        }
        return null;
    }


	public static SlaveList getInstance(){
		return sl;
	}

	public static void loadList(){
		try {
			XMLDecoder d = new XMLDecoder(new BufferedInputStream(new FileInputStream("config" + File.separator + "SlaveList.xml")));
			SlaveList.getInstance().slaveList = ((ArrayList<DeviceProperty>)d.readObject());
			d.close();
		} catch (FileNotFoundException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

	}

	public static void writeList(){
		try {
			XMLEncoder e = new XMLEncoder(new BufferedOutputStream(new FileOutputStream("config" + File.separator + "SlaveList.xml")));
			e.writeObject(SlaveList.getInstance().slaveList);
			e.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void slaveAdd(DeviceProperty a){
		slaveList.add(a);
		System.out.println(toString());
	}

	public String toString(){
		StringBuilder sb = new StringBuilder();
		for(Iterator<DeviceProperty> it = slaveList.iterator(); it.hasNext();){
			sb.append(it.next().toString());
		}
		return sb.toString();
	}

	public ArrayList<DeviceProperty> getList(){
		return slaveList;
	}


	//test method

	public static void main(String[] args) throws FileNotFoundException {
//		//write
//		ArrayList<String> func1 = new ArrayList<>();
//		ArrayList<String> func2 = new ArrayList<>();
//		func1.add("file receive");
//		func2.add("cooperation");
//		func2.add("get administration");
//
//		SlaveList.getInstance().slaveAdd(new Property(new IndoorLocation(5750,2500,1200),"test1",func1,1001));
//		SlaveList.getInstance().slaveAdd(new Property(new IndoorLocation(5750,3200,1200),"test2",func2,1002));
//		writeList();

		//read
		loadList();
		System.out.println(SlaveList.getInstance().toString());

	}


	@Override
	public String getClassName() {
		return "SlaveList";
	}

}
