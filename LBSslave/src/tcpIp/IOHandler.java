package tcpIp;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

public class IOHandler implements Handler{
	private final static int BUF_SIZE = 1024;
	private List<ByteBuffer> buffers;

	public IOHandler(){
		buffers = new ArrayList<ByteBuffer>();
	}

	public void handle(SelectionKey key) throws ClosedChannelException, IOException {
		//SocketChannel channel = (SocketChannel)key.channel();

		if(key.isReadable()){
			read(key);
		}

		if(key.isWritable()){
			write(key);
		}

	}

	private void read(SelectionKey key) throws ClosedChannelException, IOException{
		SocketChannel channel = (SocketChannel)key.channel();

		ByteBuffer buffer = ByteBuffer.allocate(BUF_SIZE);
		channel.read(buffer);

		buffer.flip();
		buffers.add(buffer);

		if(key.interestOps() != (SelectionKey.OP_READ | SelectionKey.OP_WRITE)){
			key.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
		}
	}

	private void write(SelectionKey key) throws ClosedChannelException, IOException{
		SocketChannel channel = (SocketChannel)key.channel();
		if(!buffers.isEmpty()){
			ByteBuffer buffer = buffers.get(0);
			channel.write(buffer);
			buffers.remove(0);
		}else{
			key.interestOps(SelectionKey.OP_READ);
		}
	}
}
