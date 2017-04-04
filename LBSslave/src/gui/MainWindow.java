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
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import tcpIp.SlaveClient;
import tcpIp.TestServer;

public class MainWindow extends JFrame {

	private JPanel contentPane;
	private final Action action = new SwingAction();
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

		JButton testButton = new JButton("test button");
		testButton.setBounds(5, 236, 424, 21);
		testButton.setAction(action);


		contentPane.add(testButton);
		stateLabel.setBounds(5, 5, 333, 231);


		contentPane.add(stateLabel);
		funcTestButton.setAction(funcBtnAction);
		funcTestButton.setBounds(320, 5, 109, 41);
		contentPane.add(funcTestButton);
	}

	private class SwingAction extends AbstractAction {
		public SwingAction() {
			putValue(NAME, "testButtonAction");
			putValue(SHORT_DESCRIPTION, "testButtonAction");
		}
		public void actionPerformed(ActionEvent e) {
			stateLabel.setText("sending");
			try{
				InetAddress addr = InetAddress.getLocalHost();
				SlaveClient sc = new SlaveClient(addr,10000);
				sc.open();
//				sc.Send("test message");
			}catch (Exception error) {
				System.err.println(error);
			}

		}
	}
	private class FuncBtnSwingAction extends AbstractAction {
		public FuncBtnSwingAction() {
			putValue(NAME, "FuncBtnSwingAction");
			putValue(SHORT_DESCRIPTION, "func test action");
		}
		public void actionPerformed(ActionEvent e) {
			//test function

			/*
			//IOHandler
			IOHandler io = new IOHandler();
			String test = "test string";
			io.stringToBuf(test);

			System.out.println("stringToBuf = " + io.getWriteBuffer().toString());

			System.out.println("bufToString = " + io.bufToString());
			*/
			InetAddress addr;
			try {
				addr = InetAddress.getLocalHost();
				TestServer ts = new TestServer(addr, 11111);
				SlaveClient sc = new SlaveClient(addr, 11111);

				Thread serverThread = new Thread(ts);
				Thread clientThread = new Thread(sc);

				serverThread.start();
				clientThread.start();

				sc.asyncSend("test message");

			} catch (UnknownHostException e1) {
				System.err.println("aaa");
				e1.printStackTrace();
			}

		}
	}
}
