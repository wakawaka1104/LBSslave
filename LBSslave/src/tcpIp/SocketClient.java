package tcpIp;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;

import javax.imageio.ImageIO;

import asset.ByteFile;
import asset.Classifier;
import asset.DeviceProperty;
import asset.IndoorLocation;

public class SocketClient extends SocketComm implements Runnable,Serializable{

	//***********private member
	private SocketChannel channel;
	private String remoteAddress;
	//***********

	//************constructor
	public SocketClient(String addr, int port) {
		super(addr,port);
	}
	//************************

	//run():タスク実行時関数
	//sendDataにデータがあれば送信処理
	//それ以外はread待機
	public void run() {
		//channel open処理
		try {
			System.out.println("SocketClient:channel open");
			channel = SocketChannel.open(new InetSocketAddress(addr, port));
			System.out.println("[client]:" + "[" + channel.socket().getRemoteSocketAddress().toString()	+ "]にバインドしました。");
			channel.socket().setReuseAddress(true);
			// non blocking mode
			channel.configureBlocking(false);
			selector = Selector.open();
			channel.register(selector, SelectionKey.OP_WRITE|SelectionKey.OP_READ);
			remoteAddress = channel.socket().getRemoteSocketAddress().toString();
		} catch (Exception e) {
			System.err.println("SocketClient:open()[error]");
			e.printStackTrace();
			try {
				channel.close();
			} catch (IOException e1) {
				// TODO 自動生成された catch ブロック
				e1.printStackTrace();
			}
			return;
		}

		//送受信待機ループ
		try {
			while (selector.select() > 0 || selector.selectedKeys().size() > 0 ){
				for (Iterator<SelectionKey> it = selector.selectedKeys().iterator(); it.hasNext();) {
					SelectionKey key = (SelectionKey) it.next();
					it.remove();
					if (!sendData.isEmpty() && key.isWritable()) {
						doSend((SocketChannel)key.channel());
					}
					if (key.isReadable()) {
						doRead((SocketChannel) key.channel());
					}
				}
			}
		} catch (Exception e) {
			System.err.println("SocketClient:run()[error]");
			e.printStackTrace();
			try {
				channel.close();
			} catch (IOException e1) {
				// TODO 自動生成された catch ブロック
				e1.printStackTrace();
			}
			return;
		}
	}

	public String getClassName(){
		return "SocketClient";
	}



	public void asyncSend(byte[] data){
		//last in
		sendData.add(data);
	}

	public void asyncSend(Classifier ob, byte header){
		//last in
		try {
			sendData.add(Converter.serialize(ob,header));
		} catch (IOException e) {
			try {
				channel.close();
			} catch (IOException e1) {
				// TODO 自動生成された catch ブロック
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
		System.out.println("sendList add:" + ob.getClassName());
	}





	//test function
	//SocketServerと同時に実行すること
	//localhostでの仮テスト
	public static void main(String[] args) {

		//クライアント起動
		SocketClient sc = new SocketClient("localhost",11111);
		Thread client = new Thread(sc);
		client.start();

		try{
			//sendテスト1
			byte[] tmp = {0,1,1,1,1,1,0,1,0,0,0,1};
			sc.asyncSend(tmp);
			System.out.println("test1 completed");
			Thread.sleep(500);

			DeviceProperty prop = new DeviceProperty(new IndoorLocation(1,1,1),"testDevice",new ArrayList<String>(), 1);
			sc.asyncSend(prop, (byte)1);
			System.out.println("test1-2 completed");
			Thread.sleep(500);

			//連続sendテスト2
			for(int i=0 ; i < 5 ; i++){
				sc.asyncSend(new DeviceProperty(new IndoorLocation(1,1,1),"testDevice"+ i,new ArrayList<String>(),1),(byte)1);
				//現状同一channelへの書き込みはある程度のインターバルを空ける必要あり
				Thread.sleep(500);
			}
			System.out.println("test2 completed");
			Thread.sleep(500);

			//サイズ大のsendテスト3
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(ImageIO.read(new File("img"+File.separator+"poputepic.png")), "png", baos);
			sc.asyncSend(new ByteFile(baos.toByteArray(),"png"), (byte)0);
			System.out.println("test3 completed");

		}catch(Exception e){
			e.printStackTrace();
		}
	}

}