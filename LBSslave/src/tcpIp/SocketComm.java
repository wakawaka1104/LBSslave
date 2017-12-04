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
	protected final static int BUF_SIZE = 4096;
	protected String addr;
	protected int port;
	protected Selector selector;
	//sendDataはFOLIの待ち行列
	protected LinkedList<byte[]> sendData;
	//開始文字列段階判別
	private byte initialProcess = 0;
	private boolean initial = false;
	//修了文字列段階判別
	private byte endProcess = 8;
	private boolean end = false;
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
			while(writeBuffer.hasRemaining()){
				int writeSize = channel.write(writeBuffer);
//				System.out.println("writeSize = "+writeSize);
//				System.out.println("writeRemainng = "+writeBuffer.remaining());
			}
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
			initial = false;
			initialProcess = (byte)0;
			end = false;
			endProcess = (byte)8;

			while(true){
				tmp.clear();
				int readSize = channel.read(tmp);
//				System.out.println("readSize:"+readSize);
				if(readSize < 0) break;
				else if(readSize == 0) continue;

				tmp.flip();

				if(!initial){
					while(tmp.hasRemaining()){
						byte a = tmp.get();
						processUpdate(a);
						if(initial) break;
					}
				}

				if(initial){
					while(tmp.hasRemaining()){
						byte a = tmp.get();
						contentsList.add(a);
						processUpdate(a);
						if(end) break;
					}
				}
				if(end) break;
				tmp.clear();
			}

			//channelからのreadが負->強制終了
			if(!end) return;

			int contentsSize = contentsList.size();

			byte[] contents = new byte[contentsSize];
//			System.out.println("contentsSize = "+contentsSize);
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
	private void processUpdate(byte a) {
		//initial process update
		if(!initial){
			if(a == initialProcess){
				initialProcess++;
				if(initialProcess == (byte)8){
					initial = true;
					initialProcess = (byte)0;
				}
			}else{
				initialProcess = (byte)0;
			}
		}

		//end process update
		if(!end){
			if(initial){
				if(a == endProcess){
					endProcess--;
					if(endProcess == (byte)0){
						end = true;
						endProcess = (byte)8;
					}
				}else{
					endProcess = (byte)8;
				}
			}
		}
	}

}
