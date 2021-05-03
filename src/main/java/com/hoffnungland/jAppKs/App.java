package com.hoffnungland.jAppKs;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.util.Enumeration;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import javax.swing.SpringLayout;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.ListSelectionModel;
import javax.swing.JScrollPane;

public class App implements ActionListener {

	private static final Logger logger = LogManager.getLogger(App.class);
	private JFrame frame;
	private AppKeyStoreManager appKsManager;

	private static final String LOAD_JKS_ACTION = "Load JKS Action";
	private JTable entryTable;
	private JTable table;
	private DefaultTableModel entryTableModel;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		logger.traceEntry();

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			logger.error(e);
			e.printStackTrace();
		}

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					App window = new App();
					window.frame.setVisible(true);
				} catch (Exception e) {
					logger.error(e);
					e.printStackTrace();
				}
			}
		});
		logger.traceExit();
	}

	/**
	 * Create the application.
	 */
	public App() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		logger.traceEntry();
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		SpringLayout springLayout = new SpringLayout();
		frame.getContentPane().setLayout(springLayout);

		JButton loadJksButton = new JButton("Load *.jks");
		loadJksButton.setActionCommand(LOAD_JKS_ACTION);
		loadJksButton.addActionListener(this);

		springLayout.putConstraint(SpringLayout.NORTH, loadJksButton, 10, SpringLayout.NORTH, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, loadJksButton, 10, SpringLayout.WEST, frame.getContentPane());
		frame.getContentPane().add(loadJksButton);
		
		this.entryTableModel = new DefaultTableModel();
		
		entryTable = new JTable(entryTableModel);
		this.entryTableModel.addColumn("Alias");
		this.entryTableModel.addColumn("Value");
		
		entryTable.setFillsViewportHeight(true);
		entryTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		entryTable.setCellSelectionEnabled(true);
		
		entryTable.addMouseListener(new EntryTableMouseAdapter(this, frame, entryTable));
		
		JScrollPane scrollPane = new JScrollPane(entryTable);
		scrollPane.setRowHeaderView(entryTable);
		springLayout.putConstraint(SpringLayout.SOUTH, scrollPane, -6, SpringLayout.SOUTH, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, scrollPane, -22, SpringLayout.EAST, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.NORTH, scrollPane, 6, SpringLayout.SOUTH, loadJksButton);
		springLayout.putConstraint(SpringLayout.WEST, scrollPane, 22, SpringLayout.WEST, frame.getContentPane());
		frame.getContentPane().add(scrollPane);
		
		
		springLayout.putConstraint(SpringLayout.SOUTH, entryTable, 0, SpringLayout.SOUTH, scrollPane);
		springLayout.putConstraint(SpringLayout.EAST, entryTable, 0, SpringLayout.EAST, scrollPane);
		springLayout.putConstraint(SpringLayout.NORTH, entryTable, 0, SpringLayout.SOUTH, scrollPane);
		springLayout.putConstraint(SpringLayout.WEST, entryTable, 0, SpringLayout.WEST, scrollPane);
		//scrollPane.add(entryTable);
		
		//frame.getContentPane().add(entryTable);
		
		logger.traceExit();
	}
	
	public String getEntryValue(String entryName, String passwordEntry) throws NoSuchAlgorithmException, UnrecoverableEntryException, KeyStoreException, InvalidKeySpecException {
		return appKsManager.readPasswordFromKeyStore(entryName, passwordEntry);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		logger.traceEntry();

		switch (e.getActionCommand()) {
		case LOAD_JKS_ACTION:
			this.loadJksFile();
			break;
		}
		logger.traceExit();
	}

	private void loadJksFile() {

		logger.traceEntry();

		JFileChooser fc = new JFileChooser();
		JksFilter fcJsonFiler = new JksFilter();
		fc.setMultiSelectionEnabled(false);
		fc.setFileFilter(fcJsonFiler);
		fc.addChoosableFileFilter(fcJsonFiler);
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);

		int returnVal = fc.showOpenDialog(this.frame);

		if(returnVal == JFileChooser.APPROVE_OPTION) {
			String jsonFilePath = fc.getSelectedFile().getPath();
			
			PasswordPanel passwordPanel = new PasswordPanel();
			int option = JOptionPane.showOptionDialog(null, passwordPanel, "Vault password", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
			if(option == JOptionPane.OK_OPTION) { // pressing OK button
				char[] passwd = passwordPanel.getPasswordField().getPassword();
				String passwordKs = new String(passwd);
				
				this.appKsManager = new AppKeyStoreManager(jsonFilePath, passwordKs);
				try {
					this.appKsManager.init();
					
					for(int rowIdx = this.entryTableModel.getRowCount() -1; rowIdx >= 0; rowIdx--) {
						this.entryTableModel.removeRow(rowIdx);
					}
					
					for (Enumeration<String> listAliasesEnum = this.appKsManager.listAliases(); listAliasesEnum.hasMoreElements();) {
						Object[] rowValues = new Object[] {listAliasesEnum.nextElement()};
						this.entryTableModel.addRow(rowValues);
					}
					

				} catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException e) {
					logger.error(e);
					JOptionPane.showMessageDialog(this.frame, e.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
				}
			} else {
				logger.traceExit();
				return;
			}
			
		}

		logger.traceExit();
	}
}
