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
            if(tmp.getName().equals(deviceName)){
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

	public static void clearList(){
		SlaveList.getInstance().getList().clear();
	}


	synchronized public static void listUpdate(String[] posPacket){
		//posPacket
		//[0]:"POS"
		//[1]:固定機シリアル番号
		//[2]:移動機シリアル番号
		//[3]:測位計算結果座標x
		//[4]:y
		//[5]:z
		//[6]:有効な測距結果を得た固定機数
		//[7]:測位年月日 yy/mm/dd
		//[8]:測位時刻 hh:mm::ss.msms
		//[9]:ignored data

		//name = シリアル番号
		//リスト中に同名のクライアントがあれば、それを更新
		//なければadd

		IndoorLocation loc = new IndoorLocation(Double.parseDouble(posPacket[3]),Double.parseDouble(posPacket[4]),Double.parseDouble(posPacket[5]));

		for(Iterator<DeviceProperty> it = SlaveList.getInstance().slaveList.iterator();it.hasNext();){
			DeviceProperty tmp = it.next();
			if(posPacket[2].equals(tmp.getName())){
				//更新
				tmp.setLocation(loc);
				SlaveList.writeList();
				System.out.println("SlaveListUpdated:" + SlaveList.getInstance().toString());
				return;
			}
		}
		//同名なしならadd
		SlaveList.add(new DeviceProperty(loc,posPacket[1],new ArrayList<String>(),0));
		SlaveList.writeList();
		System.out.println("SlaveListUpdated:" + SlaveList.getInstance().toString());

	}


	public static void add(DeviceProperty a){
		SlaveList.getInstance().getList().add(a);
		SlaveList.writeList();
		System.out.println("SlaveListUpdated:\n" + SlaveList.getInstance().toString());
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
//		loadList();
//		System.out.println(SlaveList.getInstance().toString());

		//clear
		clearList();
		System.out.println(SlaveList.getInstance().toString());

//		//ADD test method
//
//		SlaveList.loadList();
//
//		ArrayList<String> func1 = new ArrayList<>();
//		func1.add("file receive");
//		DeviceProperty prop1 = new DeviceProperty(new IndoorLocation(5750,2500,1200),"test1",func1,1001);
//		prop1.readFunc((byte)0, null);
//
//		ArrayList<String> func2 = new ArrayList<>();
//		func2.add("cooperation");
//		func2.add("get administration");
//		DeviceProperty prop2 = new DeviceProperty(new IndoorLocation(5750,3200,1200),"test2",func2,1002);
//
//		//update test
//		System.out.println("[Update test]");
//		System.out.println("*****preview*****");
//		System.out.println(SlaveList.getInstance().toString());
//		System.out.println("*****************");
//
//		new DeviceProperty(new IndoorLocation(1,1,1),"test1",func1,1001).readFunc((byte)0,null);
//
//		System.out.println("*****result*****");
//		System.out.println(SlaveList.getInstance().toString());
//		System.out.println("*****************");
//
//		//add test
//		System.out.println("[add test]");
//		System.out.println("*****preview*****");
//		System.out.println(SlaveList.getInstance().toString());
//		System.out.println("*****************");
//
//		prop2.readFunc((byte)0, null);
//
//		System.out.println("*****result*****");
//		System.out.println(SlaveList.getInstance().toString());
//		System.out.println("*****************");
//
//		SlaveList.writeList();


	}


	@Override
	public String getClassName() {
		return "SlaveList";
	}

}
