package gui;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import asset.IndoorLocation;
import asset.Property;
import tcpIp.SocketClient;

public class MainWindow extends JFrame {

	private static Property myProp = new Property();

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
					myProp.setLocation(new IndoorLocation(Double.parseDouble(JOptionPane.showInputDialog("x")), Double.parseDouble(JOptionPane.showInputDialog("y")), Double.parseDouble(JOptionPane.showInputDialog("z"))));
					myProp.setName(JOptionPane.showInputDialog("name"));
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


			InetAddress addr;
			try {
				addr = InetAddress.getLocalHost();
				SocketClient sc = new SocketClient(addr, 11111);
				Thread clientThread = new Thread(sc);
				clientThread.start();
/*
				_Property prop = _Property.getInstance();
				prop.setName("name");
				prop.setLocation(new IndoorLocation(1, 1, 1));

				Property p = new Property(prop);
				byte[] tmp = SlaveClient.serialize(prop.getLocation());
				sc.asyncSend(SlaveClient.addHeader(tmp));
*/

			} catch (UnknownHostException e1) {
				System.err.println("aaa");
				e1.printStackTrace();
			}

		}
	}
}
