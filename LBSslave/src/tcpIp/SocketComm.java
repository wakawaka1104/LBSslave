package tcpIp;

import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedList;

import asset.Classifier;

public abstract class SocketComm implements Serializable{

	//**********private member
	protected final static int BUF_SIZE = 1024;
	protected String addr;
	protected int port;
	protected Selector selector;
	//sendDataはFOLIの待ち行列
	protected LinkedList<byte[]> sendData;
	//***************************

	//***********constructor
	public SocketComm(String addr,int port){
		sendData = new LinkedList<byte[]>();
		this.addr = addr;
		this.port = port;
	}
	//*********************

	//起動時命令
	abstract public void run();
	//Class名を返す(ログ用)
	abstract public String getClassName();

	abstract public void asyncSend(Classifier cl, byte header);

	protected void doSend(SocketChannel channel) {
		try {
			//first out
			byte[] tmp = sendData.removeFirst();
			ByteBuffer writeBuffer = ByteBuffer.allocate(tmp.length);

			//byte[]をwriteBufferに書き込み
			writeBuffer.clear();
			writeBuffer.put(tmp);
			writeBuffer.flip();

			//send処理
			channel.write(writeBuffer);
			System.out.println( getClassName() + ":Send:[" + new Timestamp(System.currentTimeMillis()).toString() + "]");
			writeBuffer.clear();
			return;
		} catch (Exception e) {
			System.err.println(getClassName() + ":doSend()[error]");
			e.printStackTrace();
		}
	}

	protected void doRead(SocketChannel channel) {
		try {
			//読み取ったバイト列を格納するList
			ArrayList<Byte> contentsList = new ArrayList<Byte>();
			//channelから読み取るバイトを一時保持する変数
			ByteBuffer tmp = ByteBuffer.allocate(BUF_SIZE);
//			System.out.println( getClassName() + ":Read:[" + new Timestamp(System.currentTimeMillis()).toString() + "]");
			while(true){
				if(channel.read(tmp) <= 0) break;
				tmp.flip();
				while(tmp.hasRemaining()){
					byte a = tmp.get();
					contentsList.add(a);
				}
				tmp.clear();
			}
			int contentsSize = contentsList.size();
			if(contentsSize <= 0) {
				System.out.println("contentsSize==0");
				return;
			}
			byte[] contents = new byte[contentsSize];
			//頭悪い配列結合の図
			for(int i = 0 ; i < contentsSize ; i++){
				contents[i] = contentsList.get(i);
			}
			byte header = contents[0];
			Classifier cl = (Classifier) Converter.deserialize(contents);
			System.out.println(getClassName() + ":Read:[" + new Timestamp(System.currentTimeMillis()).toString() + "]:" + cl.getClassName());
			cl.readFunc(header,this);

		}catch(ClosedChannelException e){
			System.err.println(getClassName() + ":doRead()[error]");
			System.err.println("チャネルが既にcloseされています.");
			try {
				channel.close();
			} catch (IOException e1) {
				System.err.println("チャネルクローズ時にエラーが発生しました.");
				e1.printStackTrace();
			}
		}catch(ClassifierReadException e){
			System.err.println(getClassName() + ":doRead()[error]");
			System.err.println("読み込んだデータがClassifierクラスでないか、不要な読み込みが行われました．");
		}catch (IOException e) {
			System.err.println(getClassName() + ":doRead()[error]");
			e.printStackTrace();
			System.out.println("[client]:" + channel.socket().getRemoteSocketAddress().toString() + ":[disconnect]");
			try {
				channel.close();
			} catch (Exception _e) {
				System.err.println("チャネルクローズ時にエラーが発生しました.");
				_e.printStackTrace();
			}
		}
	}

}
