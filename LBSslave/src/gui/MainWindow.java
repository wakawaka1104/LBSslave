package gui;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.net.InetAddress;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import asset.DeviceProperty;
import asset.IndoorLocation;
import asset.MyProperty;
import asset.TcpipDeviceProperty;
import tcpIp.SocketClient;
import tcpIp.SocketServer;

/**
 * @author shun
 *
 */
public class MainWindow extends JFrame {

	private static final int SERVER_PORT = 11111;

	public  static DeviceProperty myProp = new DeviceProperty();
	private static int port;
	public static  String SERVER_IP = "";

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
					MainWindow frame = new MainWindow();
					frame.setVisible(true);
//					myProp.setLocation(new IndoorLocation(Double.parseDouble(JOptionPane.showInputDialog("x")), Double.parseDouble(JOptionPane.showInputDialog("y")), Double.parseDouble(JOptionPane.showInputDialog("z"))));
					myProp.setLocation(new IndoorLocation(1,1,1));
					myProp.setName(JOptionPane.showInputDialog("name"));
//					myProp.setName("test");
					port = (Integer.parseInt(JOptionPane.showInputDialog("port number")));
//					port = 11111;
					SERVER_IP = (JOptionPane.showInputDialog("ServerIP"));

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public MainWindow() {
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


			try {

				MyProperty.setFunction("camera");
				MyProperty.setName(myProp.getName());

				SocketServer ss = new SocketServer("localhost",port);
				Thread serverThread = new Thread(ss);
				serverThread.start();

				SocketClient sc = new SocketClient(SERVER_IP, SERVER_PORT);
				Thread clientThread = new Thread(sc);
				clientThread.start();

				sc.asyncSend(new TcpipDeviceProperty(new IndoorLocation(0,0,0),
						InetAddress.getLocalHost().getHostAddress(),
						port,
						myProp.getName(),
						myProp.getFunction(),
						1), (byte)0);

			} catch (Exception e1) {
				System.err.println("aaa");
				e1.printStackTrace();
			}

		}
	}
}
