package asset;

import java.util.ArrayList;
import java.util.Iterator;

import tcpIp.SocketComm;

public class DeviceProperty extends Property {

	private static final long serialVersionUID = 2L;

	//member
	private int classify;
	private IndoorLocation location;
	private String name;
	private ArrayList<String> function;
	private String selection ="";


	@Override
	public void readFunc(byte header, SocketComm sc) {
		switch(header){
			case (byte)0:
				SlaveList.getInstance().slaveAdd(this);
				break;
			case (byte)1:
				System.out.println(toString());
		}
	}

	//constructor
	public DeviceProperty(){
	}

	public DeviceProperty(IndoorLocation lo,String name, ArrayList<String> function,int classify){
		this.location = lo;
		this.name = name;
		this.function = function;
		this.classify = classify;
	}

	public DeviceProperty(DeviceProperty prop){
		this.location = prop.getLocation();
		this.name = prop.getName();
		this.function = prop.getFunction();
		this.selection = prop.getSelection();
		this.classify = prop.classify;
	}


	//getter/setter
	public IndoorLocation getLocation() {
		return location;
	}
	public void setLocation(IndoorLocation location) {
		this.location = location;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ArrayList<String> getFunction() {
		return function;
	}
	public void setFunction(ArrayList<String> function) {
		this.function = function;
	}
	public String getSelection() {
		return selection;
	}
	public void setSelection(String selection) {
		this.selection = selection;
	}
	public int getClassify() {
		return classify;
	}
	public void setClassify(int classify) {
		this.classify = classify;
	}

	public String toString(){
		String tmp = "Location:"+location.toString()+"\n"+"name:"+name+"\n";
		tmp += "Function:[";
		for(Iterator<String> it = function.iterator(); it.hasNext();){
			tmp += it.next() + ",";
		}
		tmp += "]\n";
		return tmp;
	}

	@Override
	public String getClassName() {
		return "DeviceProperty";
	}

}
