package gui;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import asset.ByteFile;
import asset.DeviceProperty;
import asset.IndoorLocation;
import tcpIp.Converter;
import tcpIp.SocketClient;

/**
 * @author shun
 *
 */
public class TestWindow extends JFrame {

	private static final int SERVER_PORT = 11111;

	private static DeviceProperty myProp = new DeviceProperty();
	private static int port;

	private JPanel contentPane;
	private JLabel stateLabel = new JLabel("none");
	private final JButton funcTestButton = new JButton("func test");
	private final Action funcBtnAction = new FuncBtnSwingAction();


	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					TestWindow frame = new TestWindow();
					frame.setVisible(true);
//					myProp.setLocation(new IndoorLocation(Double.parseDouble(JOptionPane.showInputDialog("x")), Double.parseDouble(JOptionPane.showInputDialog("y")), Double.parseDouble(JOptionPane.showInputDialog("z"))));
					myProp.setLocation(new IndoorLocation(1,1,1));
//					myProp.setName(JOptionPane.showInputDialog("name"));
					myProp.setName("test");
//					port = (Integer.parseInt(JOptionPane.showInputDialog("port number")));
					port = 11111;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public TestWindow() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		stateLabel.setBounds(5, 5, 333, 231);


		contentPane.add(stateLabel);
		funcTestButton.setAction(funcBtnAction);
		funcTestButton.setBounds(320, 5, 109, 41);
		contentPane.add(funcTestButton);
	}

	private class FuncBtnSwingAction extends AbstractAction {
		public FuncBtnSwingAction() {
			putValue(NAME, "FuncBtnSwingAction");
			putValue(SHORT_DESCRIPTION, "func test action");
		}
		public void actionPerformed(ActionEvent e) {

//			System.out.println(Property.getInstance().getLocation().toString());


			String addr;
			try {

				SocketClient sc = new SocketClient("localhost", 11111);
				Thread t = new Thread(sc);
				t.start();

				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				try {
					ImageIO.write(ImageIO.read(new File("img"+File.separator+"screenshot.png")), "png", baos);
				} catch (IOException _e) {
					System.err.println("Order.readFunc()[error]:can't find the file");
					_e.printStackTrace();
				}

				ByteFile bf = new ByteFile(baos.toByteArray(), "png");
				System.out.println(	"bfSize = "+Converter.serialize(bf, (byte)0).length );
				sc.asyncSend(bf,(byte)0);

			} catch (Exception e1) {
				System.err.println("aaa");
				e1.printStackTrace();
			}

		}
	}
}
