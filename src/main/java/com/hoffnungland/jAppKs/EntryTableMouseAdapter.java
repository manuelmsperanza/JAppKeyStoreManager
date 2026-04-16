package com.hoffnungland.jAppKs;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.spec.InvalidKeySpecException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Mouse listener used by the entry table to lazily reveal stored values.
 */
public class EntryTableMouseAdapter extends MouseAdapter {
	
	private static final Logger logger = LogManager.getLogger(EntryTableMouseAdapter.class);
	
	private App app;
	private JFrame frame;
	private JTable entryTable;

	/**
	 * Creates a table mouse adapter instance.
	 *
	 * @param app main application facade
	 * @param frame owner frame for dialogs
	 * @param entryTable table holding aliases and values
	 */
	public EntryTableMouseAdapter(App app, JFrame frame, JTable entryTable) {
		this.app = app;
		this.frame = frame;
		this.entryTable = entryTable;
	}

	/**
	 * Handles clicks by asking for an entry password and resolving the value.
	 *
	 * @param event click event
	 */
	@Override
	public void mouseClicked(MouseEvent event) {
    	
    	int selectedRowIdx = this.entryTable.getSelectedRow();
    	//entryTable.getSelectedColumn();
    	String entryValue = (String) this.entryTable.getValueAt(selectedRowIdx, 1);
    	if(entryValue == null || "".equals(entryValue)) {
    		String entryName = (String) this.entryTable.getValueAt(selectedRowIdx, 0);
    		
    		PasswordPanel passwordPanel = new PasswordPanel();
			int option = JOptionPane.showOptionDialog(null, passwordPanel, "Entry password", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
			if(option == JOptionPane.OK_OPTION) { // pressing OK button
				char[] passwd = passwordPanel.getPasswordField().getPassword();
				String passwordEntry = new String(passwd);
				try {
					entryValue = this.app.getEntryValue(entryName, passwordEntry);
					this.entryTable.setValueAt(entryValue, selectedRowIdx, 1);
				} catch (NoSuchAlgorithmException | UnrecoverableEntryException | KeyStoreException | InvalidKeySpecException e) {
					logger.error(e);
					JOptionPane.showMessageDialog(this.frame, e.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
					e.printStackTrace();
				}
				
			}  		
    	}
    }
}
