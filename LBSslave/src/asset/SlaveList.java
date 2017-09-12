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
	private static ArrayList<Property> slaveList = new ArrayList<Property>();

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
	public static Property slaveSearch(IndoorLocation locate){
		Property nearest = null;
		//遠すぎる場合は見ない
		double distMin = Constant.THRETHOLD;
		for(Iterator<Property> it = slaveList.iterator(); it.hasNext();){
			Property tmp = it.next();
			double dist = tmp.getLocation().dist(locate);
			if (distMin > dist ){
				//最小値を更新したとき
				nearest = tmp;
				distMin = dist;
			}
		}
		return nearest;
	}

	public static SlaveList getInstance(){
		return sl;
	}

	public static void loadList(){
		try {
			XMLDecoder d = new XMLDecoder(new BufferedInputStream(new FileInputStream("config" + File.separator + "SlaveList.xml")));
			slaveList = ((ArrayList<Property>)d.readObject());
			d.close();
		} catch (FileNotFoundException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

	}

	public static void writeList(){
		try {
			XMLEncoder e = new XMLEncoder(new BufferedOutputStream(new FileOutputStream("config" + File.separator + "SlaveList.xml")));
			e.writeObject(slaveList);
			e.close();
		} catch (FileNotFoundException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

	public void slaveAdd(Property a){
		slaveList.add(a);
		System.out.println(toString());
	}

	public String toString(){
		StringBuilder sb = new StringBuilder();
		for(Iterator<Property> it = slaveList.iterator(); it.hasNext();){
			sb.append(it.next().toString());
		}
		return sb.toString();
	}


	//test method

	public static void main(String[] args) throws FileNotFoundException {
//		//write
//		SlaveList.getInstance().slaveAdd(new Property(new IndoorLocation(10,10,10),"test1"));
//		SlaveList.getInstance().slaveAdd(new Property(new IndoorLocation(-50,50,50),"test2"));
//		writeList();

		//read
		loadList();
		System.out.println(SlaveList.getInstance().toString());
	}

}
