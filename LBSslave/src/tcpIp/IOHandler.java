package tcpIp;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class IOHandler implements Handler{
	private final static int BUF_SIZE = 1024;
	private List<ByteBuffer> readBuffer;
	private ByteBuffer writeBuffer;

	public IOHandler(){
		readBuffer = new ArrayList<ByteBuffer>();
		setWriteBuffer(ByteBuffer.allocate(BUF_SIZE));
	}

	public void handle(SelectionKey key) throws ClosedChannelException, IOException {
		//SocketChannel channel = (SocketChannel)key.channel();

		if(key.isReadable()){
			read(key);
		}

		if(key.isWritable() & key.isValid()){
			write(key);
		}

	}

	private void read(SelectionKey key) throws ClosedChannelException, IOException{
		SocketChannel channel = (SocketChannel)key.channel();

		ByteBuffer buffer = ByteBuffer.allocate(BUF_SIZE);
		channel.read(buffer);

		buffer.flip();
		readBuffer.add(buffer);

		if(key.interestOps() != (SelectionKey.OP_READ | SelectionKey.OP_WRITE)){
			key.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
		}
	}

	private void write(SelectionKey key) throws ClosedChannelException, IOException{
		SocketChannel channel = (SocketChannel)key.channel();
		if(writeBuffer.hasRemaining()){
			channel.write(writeBuffer);
			writeBuffer.clear();
		}else{
			key.interestOps(SelectionKey.OP_READ);
		}
	}

	public String bufToString(){

		String tmp = "";
		byte[] byteArray;
		ByteBuffer buf;
		for(Iterator<ByteBuffer> it = readBuffer.iterator(); it.hasNext() ;){
			buf = it.next();
			byteArray = new byte[buf.remaining()];
			buf.get(byteArray);
			tmp += new String(byteArray) + "/";
		}
		return tmp;
	}

	public void stringToBuf(String data){
		byte[] tmp = data.getBytes();
		writeBuffer.clear();
		for(int i = 0; i < tmp.length ; i++ ){
			writeBuffer.put(tmp[i]);
		}

		writeBuffer.flip();

	}

	public void byteToBuf(byte[] data){
		writeBuffer.clear();
		writeBuffer.put(data);
		writeBuffer.flip();
	}

	public ByteBuffer getWriteBuffer() {
		return writeBuffer;
	}

	public void setWriteBuffer(ByteBuffer writeBuffer) {
		this.writeBuffer = writeBuffer;
	}

}
