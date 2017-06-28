package tcpIp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import asset.Classifier;

public class Converter {

	//singleton pattern
	private static Converter byteConverter = new Converter();

	private Converter(){}

	public Converter getInstance(){
		return byteConverter;
	}

	public static byte[] serialize(Classifier cl, byte header) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			baos.write(header);
			ObjectOutput oo = new ObjectOutputStream(baos);
			oo.writeObject(cl);
			return baos.toByteArray();
		} catch (IOException e) {
			System.err.println("Converter:serialize()[error]");
			e.printStackTrace();
			return null;
		}
	}

	public static Object deserialize(byte[] contents){
		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(contents,1,contents.length-1);
			ObjectInputStream ois = new ObjectInputStream(bais);
			Object tmp = ois.readObject();
			bais.close();
			ois.close();
			return tmp;
		} catch (Exception e) {
			System.err.println("Converter:deserialize()[error]");
			e.printStackTrace();
			return null;
		}
	}
}
