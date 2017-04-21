package tcpIp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.ImageIcon;

public class ByteConverter {

	//singleton pattern
	private static ByteConverter byteConverter = new ByteConverter();

	private ByteConverter(){}

	public static String bufToString(List<ByteBuffer> readBuffer){

		byte[] byteArray;
		ByteBuffer buf;
		StringBuilder sb = new StringBuilder();
		for(Iterator<ByteBuffer> it = readBuffer.iterator(); it.hasNext() ;){
			buf = it.next();
			byteArray = new byte[buf.remaining()];
			buf.get(byteArray);
			sb.append(new String(byteArray));
		}
		String tmp = sb.toString();
		readBuffer.clear();
		return tmp;
	}

	public static ImageIcon bufToImageIcon(List<ByteBuffer> readBuffer){
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		for(Iterator<ByteBuffer> it = readBuffer.iterator(); it.hasNext() ;){
			try {
				baos.write(it.next().array());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		byte[] imageByteArray = baos.toByteArray();
		readBuffer.clear();

		return new ImageIcon(imageByteArray);
	}

	public ByteConverter getInstance(){
		return byteConverter;
	}

	//デバグ用main
	public static void main(String[] args) {
		//bufToString

		List<ByteBuffer> readBuffer = new ArrayList<ByteBuffer>();
		ByteBuffer buf = ByteBuffer.allocate(1024);
		ByteBuffer buf2 = ByteBuffer.allocate(1024);
		readBuffer.add(buf);
		readBuffer.add(buf2);
		String test = "test message abcdef";
		byte[] tmp = test.getBytes();
		String test2 = "2nd test string";
		byte[] tmp2 = test2.getBytes();

		for(int i = 0; i < tmp.length ; i++ ){
			readBuffer.get(0).put(tmp[i]);
		}
		for(int i= 0; i< tmp2.length ; i++){
			readBuffer.get(1).put(tmp2[i]);
		}
		readBuffer.get(0).flip();
		readBuffer.get(1).flip();

		System.out.println(bufToString(readBuffer));


		//bufToImageIcon
/*
		ByteConverter bb = new ByteConverter().getInstance();
		List<ByteBuffer> readBuffer = new ArrayList<ByteBuffer>();
		ByteBuffer buf = ByteBuffer.allocate(1024);
		BufferedImage readImage;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try{
			readImage = ImageIO.read(new File("./img/poputepic.png"));
			ImageIO.write(readImage, "png", baos);
		}catch(Exception e){
		}
		byte[] imageByteArray = baos.toByteArray();
		readBuffer.add(buf);
		int index = 0;
		for(int i = 0; i < imageByteArray.length ; i++ ){
			if(!readBuffer.get(index).hasRemaining()){
				readBuffer.get(index).flip();
				index++;
				readBuffer.add(ByteBuffer.allocate(1024));
			}
			readBuffer.get(index).put(imageByteArray[i]);
		}
		readBuffer.get(index).flip();


		ImageIcon ii = bb.bufToImageIcon(readBuffer);

		FilePresentationWindow frame = new FilePresentationWindow();
		frame.setVisible(true);
		frame.ImagePresenter(ii);
		*/
	}

}
