package javaapplication1;

import java.awt.Color;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;

@SuppressWarnings("serial")
public class Tickets extends JFrame implements ActionListener {

	// class level member objects
	Dao dao = new Dao(); // for CRUD operations
	Boolean chkIfAdmin = null;
	String user;
	

	// Main menu object items
	private JMenu mnuFile = new JMenu("File");
	private JMenu mnuAdmin = new JMenu("Admin");
	private JMenu mnuTickets = new JMenu("Tickets");

	// Sub menu item objects for all Main menu item objects
	JMenuItem mnuItemExit;
	JMenuItem mnuItemUpdate;
	JMenuItem mnuItemDelete;
	JMenuItem mnuItemCloseTicket;
	JMenuItem mnuItemDeletedTickets;
	JMenuItem mnuItemOpenTicket;
	JMenuItem mnuItemViewTicket;

	public Tickets(Boolean isAdmin, String username) {

		chkIfAdmin = isAdmin;
		createMenu();
		prepareGUI();
		user = username;

	}

	private void createMenu() {

		/* Initialize sub menu items **************************************/

		// initialize sub menu item for File main menu
		mnuItemExit = new JMenuItem("Exit");
		// add to File main menu item
		mnuFile.add(mnuItemExit);

		// initialize first sub menu items for Admin main menu
		mnuItemUpdate = new JMenuItem("Update Ticket");
		// add to Admin main menu item
		mnuAdmin.add(mnuItemUpdate);

		// initialize second sub menu items for Admin main menu
		mnuItemDelete = new JMenuItem("Delete Ticket");
		// add to Admin main menu item
		mnuAdmin.add(mnuItemDelete);
		
		// initialize third sub menu items for Admin main menu
		mnuItemCloseTicket = new JMenuItem("Close Ticket");
		// add to Admin main menu item
		mnuAdmin.add(mnuItemCloseTicket);
		
		// initialize fourth sub menu items for Admin main menu
		mnuItemDeletedTickets = new JMenuItem("View deleted tickets");
		// add to Admin main menu item
		mnuAdmin.add(mnuItemDeletedTickets);

		// initialize first sub menu item for Tickets main menu
		mnuItemOpenTicket = new JMenuItem("Open Ticket");
		// add to Ticket Main menu item
		mnuTickets.add(mnuItemOpenTicket);

		// initialize second sub menu item for Tickets main menu
		mnuItemViewTicket = new JMenuItem("View Ticket");
		// add to Ticket Main menu item
		mnuTickets.add(mnuItemViewTicket);

		// initialize any more desired sub menu items below

		/* Add action listeners for each desired menu item *************/
		mnuItemExit.addActionListener(this);
		mnuItemUpdate.addActionListener(this);
		mnuItemDelete.addActionListener(this);
		mnuItemOpenTicket.addActionListener(this);
		mnuItemViewTicket.addActionListener(this);
		mnuItemCloseTicket.addActionListener(this);
		mnuItemDeletedTickets.addActionListener(this);

		 /*
		  * continue implementing any other desired sub menu items (like 
		  * for update and delete sub menus for example) with similar 
		  * syntax & logic as shown above*
		 */


	}

	private void prepareGUI() {

		// create JMenu bar
		JMenuBar bar = new JMenuBar();
		bar.add(mnuFile); // add main menu items in order, to JMenuBar
		if (chkIfAdmin)
			bar.add(mnuAdmin);
		bar.add(mnuTickets);
		// add menu bar components to frame
		setJMenuBar(bar);

		addWindowListener(new WindowAdapter() {
			// define a window close operation
			public void windowClosing(WindowEvent wE) {
				System.exit(0);
			}
		});
		// set frame options
		setSize(400, 400);
		getContentPane().setBackground(Color.LIGHT_GRAY);
		setLocationRelativeTo(null);
		setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// implement actions for sub menu items
		if (e.getSource() == mnuItemExit) {
			System.exit(0);
		} else if (e.getSource() == mnuItemOpenTicket) {

			// get ticket information
			String ticketDesc = JOptionPane.showInputDialog(null, "Enter a ticket description");
			String sDate = JOptionPane.showInputDialog(null, "Enter the start date ex: YYYY-MM-DD");

			// insert ticket information to database

			int id = dao.insertRecords(user, ticketDesc, sDate);

			// display results if successful or not to console / dialog box
			if (id != 0) {
				System.out.println("Ticket ID : " + id + " created successfully!!!");
				JOptionPane.showMessageDialog(null, "Ticket id: " + id + " created");
			} else
				System.out.println("Ticket cannot be created!!!");
		}

		else if (e.getSource() == mnuItemViewTicket) {

			// retrieve all tickets details for viewing in JTable
			try {

				// Use JTable built in functionality to build a table model and
				// display the table model off your result set!!!
				JTable jt = new JTable(ticketsJTable.buildTableModel(dao.readRecords(chkIfAdmin,user)));
				jt.setBounds(30, 40, 200, 400);
				JScrollPane sp = new JScrollPane(jt);
				add(sp);
				setVisible(true); // refreshes or repaints frame on screen
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		else if (e.getSource() == mnuItemDelete) {
			String ticketNum = JOptionPane.showInputDialog(null, "Enter a ticket number");
			String ticketName = JOptionPane.showInputDialog(null, "Enter name of issuer of ticket");
			String ticketReason= JOptionPane.showInputDialog(null, "Enter reason for delete");
			dao.insertRecordstoDelete(ticketName,ticketReason);
			dao.deleteRecords(ticketNum);
		}
		else if (e.getSource() == mnuItemUpdate) {
			String ticketNum = JOptionPane.showInputDialog(null, "Enter a ticket number");
			String ticketDesc = JOptionPane.showInputDialog(null, "Enter updated ticket description");
			dao.updateRecords(chkIfAdmin,user,ticketNum, ticketDesc);
			
										
		}
		else if (e.getSource() == mnuItemCloseTicket) {
			String ticketNum = JOptionPane.showInputDialog(null, "Enter a ticket number");
			String ticketClose = JOptionPane.showInputDialog(null, "Enter close date for the ticket ex: YYYY-MM-DD");
			dao.closeRecords(ticketNum, ticketClose);

		}
		else if (e.getSource() == mnuItemDeletedTickets) {
			try {

				// Use JTable built in functionality to build a table model and
				// display the table model off your result set!!!
				JTable jt = new JTable(ticketsJTable.buildTableModel(dao.readRecordsDeleted()));
				jt.setBounds(30, 40, 200, 400);
				JScrollPane sp = new JScrollPane(jt);
				add(sp);
				setVisible(true); // refreshes or repaints frame on screen
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		/*
		 * continue implementing any other desired sub menu items (like for update and
		 * delete sub menus for example) with similar syntax & logic as shown above
		 */

	}

}
