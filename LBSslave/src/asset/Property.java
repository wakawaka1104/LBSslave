package asset;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

import tcpIp.SocketComm;

public class Property implements Classifier,Serializable{

	private static final long serialVersionUID = 2L;

	//member
	private IndoorLocation location;
	private String name;
	private ArrayList<String> function;
	private String selection ="";


	@Override
	public void readFunc(byte header, SocketComm sc) {
		SlaveList.getInstance().slaveAdd(this);
	}

	public Order makeOrder(){
		return new Order(this);
	}

	//constructor
	public Property(){
	}

	public Property(IndoorLocation lo,String name, ArrayList<String> function){
		this.location = lo;
		this.name = name;
		this.function = function;
	}

	public Property(Property prop){
		this.location = prop.getLocation();
		this.name = prop.getName();
		this.function = prop.getFunction();
		this.selection = prop.getSelection();
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

	public String toString(){
		String tmp = "Location:"+location.toString()+"\n"+"name:"+name+"\n";
		tmp += "Function:[";
		for(Iterator<String> it = function.iterator(); it.hasNext();){
			tmp += it.next() + ",";
		}
		tmp += "]\n";
		return tmp;
	}




}
