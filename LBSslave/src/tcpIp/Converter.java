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

	public static byte[] serialize(Classifier cl, byte header) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		baos.write(Constants.INITIAL_BYTE);
		baos.write(header);
		ObjectOutput oo = new ObjectOutputStream(baos);
		oo.writeObject(cl);
		baos.write(Constants.END_BYTE);

		//何故かreturn baos.toByteArray()とすると、
		//有効なデータの先に謎の0埋めが発生する
		//こうして一度ローカル変数に格納してからreturnすると大丈夫
		//謎of謎
		byte[] tmp = baos.toByteArray();
		return tmp;

	}

	public static Object deserialize(byte[] contents) throws ClassifierReadException{
		try{
			//INITIAL_BYTEはdoRead()の処理で既にはじいているので、END_BYTEの末尾8バイトのみ読まない
			ByteArrayInputStream bais = new ByteArrayInputStream(contents,1,contents.length-9);
			ObjectInputStream ois = new ObjectInputStream(bais);
			Object tmp = ois.readObject();
			bais.close();
			ois.close();
			return tmp;
		}catch(Exception e){
			throw new ClassifierReadException();
		}
	}
}
