package asset;

import java.util.ArrayList;

public class MyProperty {

	private static MyProperty instance = new MyProperty();

	private static ArrayList<String> function = new ArrayList<String>();



	private MyProperty(){

	}

	public static MyProperty getInstance(){
		return instance;
	}

	public static void setFunction(String func){
		function.add(func);
	}

	public static ArrayList<String> getFunction(){
		return function;
	}

}
