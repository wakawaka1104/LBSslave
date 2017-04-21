package tcpIp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

import asset.Classifier;
import asset.IndoorLocation;
import asset.Property;
import asset._Property;

public class SlaveClient implements Runnable{

	// member
	private static final int BUF_SIZE = 1024;
	private static final int WAIT_TIME = 500;
	private Selector selector;
	private byte[] sendData;
	private boolean sendFlag = false;

	private SocketChannel channel = null;
	ByteBuffer buf = ByteBuffer.allocate(BUF_SIZE);

	// constructor
	public SlaveClient(InetAddress addr, int port) {
		try {
			System.out.println("SlaveClient:channel open");
			channel = SocketChannel.open(new InetSocketAddress(addr, port));
			System.out.println("[client]:" + "[" + channel.socket().getRemoteSocketAddress().toString() + ":" + port + "]にバインドしました。");
		} catch (IOException e) {
			System.err.println("SlaveClient:constructor()[error]");
			e.printStackTrace();
		}
	}

	public void run(){
		open();
		while(true){
			if(sendFlag){
				_asyncSend();
			}else{
				try {
					Thread.sleep(WAIT_TIME);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void open(){
		//channel open
		try {
			channel.socket().setReuseAddress(true);
			//non blocking mode
			channel.configureBlocking(false);
			selector = Selector.open();
		} catch (IOException e) {
			System.err.println("SlaveClient:open()[error]");
			e.printStackTrace();
		}
	}

	synchronized public void asyncSend(byte[] data){
		this.sendData = data;
		sendFlag = true;
	}

	//private function
	synchronized private void _asyncSend(){
		try {
			channel.register(selector,SelectionKey.OP_WRITE ,new IOHandler());
			while(selector.select() > 0){

				Set<SelectionKey> keys = selector.selectedKeys();
				for(Iterator<SelectionKey> it = keys.iterator(); it.hasNext(); ){
					SelectionKey key = it.next();
					it.remove();

					IOHandler handler = (IOHandler)key.attachment();
					handler.byteToBuf(sendData);
					handler.handle(key);
					return;
				}
			}
		} catch (IOException e) {
			System.err.println("SlaveClient;asyncSend()[error]");
			e.printStackTrace();
		}finally {
			sendFlag = false;
		}
	}

	//static function

	public static byte[] serialize(Classifier cl){
		try {
			byte[] tmp;
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutput oo = new ObjectOutputStream(baos);
			oo.writeObject(cl);
			tmp = baos.toByteArray();
			return tmp;
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		return null;

	}

	public static byte[] addHeader(byte[] data){
		byte[] tmp = new byte[data.length + 1];
		tmp[0] = (byte)0;
		for(int i = 0; i < data.length; i++){
			tmp[i+1] = data[i];
		}
		return tmp;
	}


	//デバグ用main
	public static void main(String[] args){
		_Property prop = _Property.getInstance();
		prop.setName("name");
		prop.setLocation(new IndoorLocation(1, 1, 1));

		Property p = new Property();
		byte[] tmp = serialize(p);

		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(tmp);
			ObjectInputStream ois = new ObjectInputStream(bais);
			Object tmpObject = ois.readObject();
			bais.close();
			ois.close();

			System.out.println(tmpObject.toString());
		} catch (IOException | ClassNotFoundException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}



	}

}
