import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * CreditUnion Program
 * Author:      Pawel Wodyk
 * Student ID:  B00122935
 * Written on:  22/04/19
 * Description:
 * 	    This Program manages the accounts for the Fictional Credit Union Compnay.
 *      It provides basic database functionality and allowes user to save and retrive the data between sessions.
 * Classes:
 * 	  - CreditUnion class is responsible for running the program, it displays the GUI, handles exceptions and 
 *          provide the functionality to the user.
 *    - Account class is responsible for holding the instance values of the account and ensuring the data integrity.
 * 
 * CreditUnion Methods:
 *      void main (String[])   Main function creates the instance of the program.
 * 
 *      === GUI Methods ===
 *      void initializeGUI()    Initialize the GUI elements and add them to correct container elements, as appropriate. 
 *                               Adds the ChangeListener to the tabbed panel element so the correct panels are displayed when tabs are clicked. 
 *      void initAddAcc()       Helper method, displays elements for Create Account panel.
 *      void initRemAcc()       Helper method, displays elements for Delete Account panel.
 *      void initMngAcc()		Helper method, displays elements for Manage Account panel.
 *      void setColorScheme()   Helper method sets colors for all the elements.
 *      
 *      === Events Handlers ===
 *      void actionPerformed(ActionEvent) 	Waits for the button press events and calls appropriate functions.
 *      void windowClosing(WindowEvent)     Listens for the close window button pess and handles the program termination.
 *      void stateChanged(ChangeListener)   Listens for changes in tabbed pannel. when the new tab is pressed it calls the appropriate function.
 * 
 *      === Data Validation Methods ===
 *      boolean verifyAccountNumber()   Verifies that the account number provided by the user, in the text field numberTxF, is in correct format. 
 *                                       If the format is incorrect the user is informed by the message dialog and the function returns false.
 *      boolean verifyInput()           Works similar to the verifyAccountNumber function however it check the input of the remaining text fields
 *                                       ensuring the data is correct before it could be used to create new account. 
 *                                       Returns true when data is correct or false if the exception occured
 * 
 *      === Methods working on Account instance ===
 *      void openAccount()      Use the data provided in the text fields to create new account. 
 *                               Saves the data to local Account instance then calls the writeAccToFile method to save the data to file.
 *      void closeAccount()     Erase the Account data from the file. If the balance is 0, the function writes the blank Account instance to the file.
 *      void makeLodgement()    Displays the prompt to enter amount to be lodged, calls appropriate Account function on local account.
 *                               Then calls the writeAccToFile to save the data to file.
 *      void makeWithdrawal()   Displays the prompt to enter amount to be withdrawn, calls appropriate Account function on local account.
 *                               Then calls the writeAccToFile to save the data to file.
 *      void requestOverdraft() Displays the prompt to enter new Overdraft limit, calls appropriate function on local account.
 *                               Then calls the writeAccToFile to save the data to file.
 *      void getData()          Takes the index position from the account number text field and pulls the correct account from the file.
 *      void nextAccount()      Display next account in the file based on the current pointer value.
 *      void prevAccount()      Display previous account in the file based on the current pointer value.
 * 
 *      === Helper Methods ===
 *      void clearFields()      Clear all the text fields.
 *      void reset()            Reset the pointer and erase the local account instance.
 *      void populateFields()   Populate all the text fields based on the local account instance.
 * 
 *      === IO Methods ===
 *      boolean recordAvailable()       Checks the location on the file and returns true if the account is populated or false if the location is empty.
 *      void writeAccToFile(Account)    Saves the data from local instance of the Account to file.
 *      Account readAccFromFile(int)    Reads the data from file in order and assign it to the corresponding variables on the Account instance.
 * 
 * Account Methods:
 *      === Getter Methods ===
 *      int getNumber()         Returns value of number variable.
 *      String getName()        Returns value of name variable.
 *      double balance()       	Returns value of balance variable.
 *      double overdraftLimit() Returns value of overdraft limit (odLimit) variable
 * 
 *      === Setter Methods === 
 *      void changeName(String)     sets the name variable to the passed value.
 *      int lodgement(double)	    Adds double value to balance,
 *                                      Returns code 0 if successful,
 *                                      Returns code 1 if the amount is less than or equals to 0.
 *      int withdrawal(double)      Subtracts double value from balance,
 *                                      Returns code 0 if successful,
 *                                      Returns code 1 if the amount is less than or equals to 0,
 *                                      Returns code 2 if the amount exceeds balance + overdraft limit.
 *      int setNewOverdraft(double)	Sets the overdraft limit (odLimit) variable.
 *                                      Returns code 0 if successful,
 *                                      Returns code 1 if the newLimit is less than 0.
 * 
 *     === Other Methods === 
 *      String toString()   Returns String representation of the Account object. Displaying all variables values in one row.
 * 
 */

public class CreditUnion extends JFrame implements ActionListener {

    // database global variables
    private final int MAX_ACC = 100;
    private final int NAME_MAX_LENGTH = 30;
    private final int RECORD_SIZE = 80;

    private File file;
    private RandomAccessFile raf;
    int pointer;

    Account currentAccount;

    // GUI global variables
    JTabbedPane mainPnl;
    JPanel addAccPnl, remAccPnl, mngAccPnl, infoPnl, btnPnl, idPnl, navPnl;
    JTextField numberTxF, nameTxF, balanceTxF, overdraftTxF;
    JButton addBtn, remBtn, getDataBtn, lodgeBtn, withdrawBtn, requestODBtn, clrBtn, nextBtn, prevBtn;

    /* ==== Constructors ==== */

    public CreditUnion() {
        super("Credit Union App");

        pointer = 0;
        currentAccount = new Account(0, "");

        try {
            file = new File("data.dat");

            if (!file.exists()) {
                System.out.println("Creating file: data.dat");
                file.createNewFile();
            }

            raf = new RandomAccessFile(file, "rw");
            raf.setLength(MAX_ACC * RECORD_SIZE);

            //throw new IOException("Test Error");
        } catch (IOException ioEx) {
            ioEx.printStackTrace(System.err);
            JOptionPane
                    .showMessageDialog(
                            null, "Could not establish connection with database file: data.dat\n\nERROR : "
                                    + ioEx.toString() + "\n\nProgram will terminate....",
                            "CRITICAL ERROR", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        // handles the close button
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent wEvent) {
                try {
                    raf.close();

                    //throw new IOException("Test Error");
                } catch (IOException ioEx) {
                    ioEx.printStackTrace(System.err);
                    JOptionPane.showMessageDialog(
                            null, "Could not close file correctlly because Exception occured\n\nERROR: "
                                    + ioEx.toString() + "\n\nData may become corrupted",
                            "CRITICAL ERROR", JOptionPane.ERROR_MESSAGE);
                    System.exit(1);
                }

                System.exit(0);
            }
        });

        initializeGUI();
        clearFields();

    }

    /* ==== Methods responsible for GUI ==== */

    private void initializeGUI() {
        // initialising components

        this.setLayout(new BorderLayout());

        mainPnl = new JTabbedPane();

        FlowLayout layout = new FlowLayout(FlowLayout.CENTER);
        addAccPnl = new JPanel(layout);
        remAccPnl = new JPanel(layout);
        mngAccPnl = new JPanel(layout);

        infoPnl = new JPanel(new GridLayout(5, 2, 20, 10));
        idPnl = new JPanel(new GridLayout(1, 2, 10, 0));

        navPnl = new JPanel(new GridLayout(1, 2, 50, 0));

        // inisialize Text Fields

        numberTxF = new JTextField(5);
        nameTxF = new JTextField(15);
        balanceTxF = new JTextField(15);
        overdraftTxF = new JTextField(15);

        // inicializing buttons

        addBtn = new JButton("Create Account");
        remBtn = new JButton("Close Account");
        getDataBtn = new JButton("Get");
        lodgeBtn = new JButton("Make Lodgement");
        withdrawBtn = new JButton("Make Withdrawal");
        requestODBtn = new JButton("Request Overdraft");
        clrBtn = new JButton("Clear Fields");
        nextBtn = new JButton("Next");
        prevBtn = new JButton("Prev");

        // inicializing infoPnl
        infoPnl.add(new JLabel("Account Number:"));
        idPnl.add(numberTxF);
        idPnl.add(getDataBtn);
        infoPnl.add(idPnl);
        infoPnl.add(new JLabel("Account Name:"));
        infoPnl.add(nameTxF);
        infoPnl.add(new JLabel("Current Balance:"));
        infoPnl.add(balanceTxF);
        infoPnl.add(new JLabel("Overdraft Limit:"));
        infoPnl.add(overdraftTxF);
        infoPnl.add(new JLabel());
        infoPnl.add(clrBtn);

        // adding tabs
        mainPnl.addTab("Create Account", addAccPnl);
        mainPnl.addTab("Close Account", remAccPnl);
        mainPnl.addTab("Manage Account", mngAccPnl);

        // adding navigation buttons to navigation panel

        navPnl.add(prevBtn);
        navPnl.add(nextBtn);

        // adding panels to the jframe
        this.add(mainPnl, BorderLayout.CENTER);
        this.add(navPnl, BorderLayout.SOUTH);

        // in vesion 1 the buttons were placed directlly to the left nad right of the panel
        //this.add(nextBtn, BorderLayout.EAST);
        //this.add(prevBtn, BorderLayout.WEST);

        //adds change listener for changeing the tabs
        ChangeListener cl = new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent cEvent) {

                JTabbedPane tPane = (JTabbedPane) cEvent.getSource();
                System.out.println("You've selected Tab " + tPane.getSelectedIndex());
                if (tPane.getSelectedIndex() != -1) {

                    if (tPane.getSelectedComponent() == addAccPnl) {
                        initAddAcc();
                    } else if (tPane.getSelectedComponent() == remAccPnl) {
                        initRemAcc();
                    } else if (tPane.getSelectedComponent() == mngAccPnl) {
                        initMngAcc();
                    } else {
                        System.err.println("no element selected");
                    }
                    populateFields();
                    //clearFields();
                }
            }
        };

        // add event listeners
        mainPnl.addChangeListener(cl);

        addBtn.addActionListener(this);
        remBtn.addActionListener(this);
        getDataBtn.addActionListener(this);
        lodgeBtn.addActionListener(this);
        withdrawBtn.addActionListener(this);
        requestODBtn.addActionListener(this);
        clrBtn.addActionListener(this);
        nextBtn.addActionListener(this);
        prevBtn.addActionListener(this);

        numberTxF.addActionListener(this);

        //sets color scheme and styling

        this.setColorScheme();

        infoPnl.setBorder(new CompoundBorder(BorderFactory.createTitledBorder("Account Details:"),
                new EmptyBorder(10, 20, 20, 20)));

        navPnl.setBorder(new EmptyBorder(10, 50, 10, 50));

        // setting the default tab
        mainPnl.setSelectedIndex(2);

        setResizable(false);
        setSize(500, 370);
        setVisible(true);
    }

    private void initAddAcc() {
        nameTxF.setEditable(true);
        balanceTxF.setEditable(true);
        overdraftTxF.setEditable(true);

        addAccPnl.add(infoPnl);

        addAccPnl.add(addBtn);
        //addAccPnl.add(clrBtn);

    }

    private void initRemAcc() {
        nameTxF.setEditable(false);
        balanceTxF.setEditable(false);
        overdraftTxF.setEditable(false);

        remAccPnl.add(infoPnl);

        remAccPnl.add(remBtn);
        //remAccPnl.add(clrBtn);
    }

    private void initMngAcc() {
        nameTxF.setEditable(false);
        balanceTxF.setEditable(false);
        overdraftTxF.setEditable(false);

        mngAccPnl.add(infoPnl);

        mngAccPnl.add(lodgeBtn);
        mngAccPnl.add(withdrawBtn);
        mngAccPnl.add(requestODBtn);
        //mngAccPnl.add(clrBtn);
    }

    private void setColorScheme() {
        Color colorPri, colorS1, colorS2, colorBG, colorFG, gray;

        colorPri = new Color(108, 161, 179);
        colorS1 = new Color(163, 191, 200);
        colorS2 = new Color(115, 148, 159);
        colorBG = new Color(85, 113, 122);
        colorFG = new Color(223, 240, 245);
        gray = new Color(238, 238, 238);

        this.setBackground(colorBG);
        mainPnl.setBackground(colorS2);

        addAccPnl.setBackground(colorPri);
        remAccPnl.setBackground(colorPri);
        mngAccPnl.setBackground(colorPri);

        infoPnl.setBackground(colorPri);
        idPnl.setBackground(colorPri);

        navPnl.setBackground(colorBG);

        //  balanceTxF.setBackground(colorFG);
        //  nameTxF.setBackground(colorFG);
        //  numberTxF.setBackground(colorFG);
        //  overdraftTxF.setBackground(colorFG);

        addBtn.setBackground(colorS1);
        clrBtn.setBackground(colorS1);
        getDataBtn.setBackground(colorS1);
        lodgeBtn.setBackground(colorS1);
        withdrawBtn.setBackground(colorS1);
        remBtn.setBackground(colorS1);
        requestODBtn.setBackground(colorS1);

        //nextBtn.setBackground(gray);
        //prevBtn.setBackground(gray);

        nextBtn.setBackground(colorS2);
        nextBtn.setForeground(colorFG);
        prevBtn.setBackground(colorS2);
        prevBtn.setForeground(colorFG);

    }

    /* ==== Event Listeners ==== */

    @Override
    public void actionPerformed(ActionEvent aEvent) {

        if (aEvent.getSource() == clrBtn) {
            reset(); // reset the account instance
            clearFields(); // clear fields
        } else if (aEvent.getSource() == nextBtn) {
            nextAccount(); // load next account
        } else if (aEvent.getSource() == prevBtn) {
            prevAccount(); // load revious account
        } else if (aEvent.getSource() == getDataBtn || aEvent.getSource() == numberTxF) {
            getData(); // load data using index field
        } else if (aEvent.getSource() == remBtn) {
            closeAccount(); // close account
        } else if (aEvent.getSource() == lodgeBtn) {
            makeLodgement(); // increase balance
        } else if (aEvent.getSource() == withdrawBtn) {
            makeWithdrawal(); // decrease balance
        } else if (aEvent.getSource() == requestODBtn) {
            requestOverdraft(); // set overdraft limit
        } else if (aEvent.getSource() == addBtn) {
            openAccount(); // open account using data from fields
        } else {
            System.err.println("Unrecognized event: " + aEvent.paramString()); // fallback
        }

        populateFields();
        System.out.println(pointer + ": " + currentAccount.toString());
    }

    /* ==== Data Validation Methods ==== */

    private boolean verifyAccountNumber() {

        System.out.println("verifing the account number");

        String input = numberTxF.getText();

        if (input.isEmpty()) {
            numberTxF.setText(Integer.toString(currentAccount.getNumber()));
        } else {
            try {
                Integer.parseInt(input);
            } catch (NumberFormatException nfEx) {
                System.err.println("Input Error => incorrect data in text field: Account Number");
                JOptionPane.showMessageDialog(mainPnl,
                        "Incorrect Data entered in text field: Account Number\n" + nfEx.toString()
                                + "\nPlease enter whole number",
                        "Incorrect Account Number", JOptionPane.WARNING_MESSAGE);
                return false;
            }
        }

        return true;
    }

    private boolean verifyInput() {

        System.out.println("Verifing User Input");
        if (nameTxF.getText().isEmpty()) {
            JOptionPane.showMessageDialog(mainPnl,
                    "Incorrect Data entered in text field: Account Name\nThe Account name cannot be empty",
                    "Incorrect Account Name", JOptionPane.WARNING_MESSAGE);
            return false;
        } else if (nameTxF.getText().length() > NAME_MAX_LENGTH) {
            System.err.println("Input Error => incorrect data in text field: Account Name");
            int userInput = JOptionPane.showConfirmDialog(null,
                    "Incorrect Data entered in text field: Account Name\n Name is too long\n Do you want to trim the name to 30 characters?",
                    "Incorrect Account Name", JOptionPane.WARNING_MESSAGE);
            if (userInput == JOptionPane.YES_OPTION) {
                String temp = nameTxF.getText();
                System.out.println("before : " + nameTxF.getText().length());
                nameTxF.setText(temp.substring(0, NAME_MAX_LENGTH));
                System.out.println("after : " + nameTxF.getText().length());
            } else {
                return false;
            }
        }

        try {
            Double.parseDouble(balanceTxF.getText());
        } catch (NumberFormatException nfEx) {
            System.err.println("Input Error => incorrect data in text field: balance");
            JOptionPane.showMessageDialog(mainPnl, "Incorrect Data entered in text field: Current Balance\n"
                    + nfEx.toString() + "\nPlease enter a number", "Incorrect Data entered",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }

        try {
            Double.parseDouble(overdraftTxF.getText());
        } catch (NumberFormatException nfEx) {
            System.err.println("Input Error => incorrect data in text field: overdraft");
            JOptionPane.showMessageDialog(mainPnl,
                    "Incorrect Data entered in text field: Overdraft\n" + nfEx.toString() + "\nPlease enter a number",
                    "Incorrect Data entered", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        return true;
    }

    /* ==== Methods responsible handiling Account ==== */

    //allow user to open new CreditUnionAccount account
    private void openAccount() {
        System.out.println("Open");

        if (verifyAccountNumber()) {
            int index = Integer.parseInt(numberTxF.getText());
            if (index > 0 && index <= MAX_ACC) {
                if (!recordAvailable(index)) {
                    pointer = index;
                    if (verifyInput()) {
                        String name = nameTxF.getText();
                        double bal = Double.parseDouble(balanceTxF.getText());
                        double od = Double.parseDouble(overdraftTxF.getText());

                        currentAccount = new Account(pointer, name, bal, od);

                        try {
                            writeAccToFile(currentAccount);
                            JOptionPane.showMessageDialog(mainPnl, "Account added sucessfully!");
                            //throw new IOException("Testing exception handling in openAccount()... ");
                        } catch (IOException ioEx) {
                            System.err.println("[!!!]\nException " + ioEx.getClass().getSimpleName()
                                    + " occured while attempting to modify file.\nException Message: "
                                    + ioEx.getMessage() + "\n[!!!]");

                            JOptionPane.showMessageDialog(mainPnl,
                                    "Program could not create the account!\nException "
                                            + ioEx.getClass().getSimpleName()
                                            + " occured when attempting to edit the file.\nException Message: "
                                            + ioEx.getMessage() + "\n\nPlease ensure:"
                                            + "\n1.    The file data.dat is in the program directory"
                                            + "\n2.    You have full access to data.dat file.",
                                    "Unable to Create new account", JOptionPane.ERROR_MESSAGE);

                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(mainPnl,
                            "Account could not be created: account with this account number already exists",
                            "Account already exists", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(mainPnl, "Selected index is outside bounds", "Wrong Input",
                        JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    // allow an account holder to close an account on condition that the balance is currently zero
    private void closeAccount() {
        System.out.println("Close");

        if (pointer > 0 && pointer <= MAX_ACC) {
            if (currentAccount.balance() != 0) {
                JOptionPane.showMessageDialog(mainPnl,
                        "Account could not be closed: cannot close the account with balance",
                        "Account still has balance", JOptionPane.WARNING_MESSAGE);
            } else {
                currentAccount = new Account(0, "");
                try {
                    writeAccToFile(currentAccount);
                    JOptionPane.showMessageDialog(mainPnl, "Account closed sucessfully");
                    //throw new IOException("Testing exception handing for closeAccount()");
                } catch (IOException ioEx) {
                    System.err.println("[!!!]\nException " + ioEx.getClass().getSimpleName()
                            + " occured while attempting to modify file.\nException Message: " + ioEx.getMessage()
                            + "\n[!!!]");

                    JOptionPane.showMessageDialog(mainPnl,
                            "Program could not close the account!\nException " + ioEx.getClass().getSimpleName()
                                    + " occured when attempting to edit the file.\nError Message: " + ioEx.getMessage()
                                    + "\n\nPlease ensure:" + "\n1.    The file data.dat is in the program directory"
                                    + "\n2.    You have full access to data.dat file.",
                            "Unable to Close the account", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(mainPnl, "Selected index is outside bounds", "Wrong Input",
                    JOptionPane.WARNING_MESSAGE);
        }
        //clearFields();
    }

    // allow an account holder to lodge money
    private void makeLodgement() {
        System.out.println("Lodge");

        if (pointer > 0 && pointer <= MAX_ACC && currentAccount.getNumber() != 0) {
            String input = JOptionPane.showInputDialog(mainPnl, "Please Enter the amount you would like to lodge");
            double amount;
            if (input != null) {
                if (!input.isEmpty()) {
                    try {
                        amount = Double.parseDouble(input);
                    } catch (NumberFormatException nfEx) {
                        System.err.println("Input Error => incorrect data entered in input dialog");
                        JOptionPane.showMessageDialog(mainPnl, "Incorrect input: " + nfEx.getMessage(),
                                "Lodgement unsucessfull", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    int result = currentAccount.lodgement(amount);

                    if (result == 0) {
                        try {
                            writeAccToFile(currentAccount);
                        } catch (IOException ioEx) {
                            System.err.println("[!!!]\nException " + ioEx.getClass().getSimpleName()
                                    + " occured while attempting to modify file.\nException Message: "
                                    + ioEx.getMessage() + "\n[!!!]");

                            JOptionPane.showMessageDialog(mainPnl,
                                    "Program could not write new data to file because of " + ioEx.toString(),
                                    "Lodgement unsucessfull", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        JOptionPane.showMessageDialog(mainPnl, "Lodgement amount: " + amount, "Lodgement sucessfull",
                                JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(mainPnl,
                                "Cannot lodge amount: " + amount + "\nthe logement amount is negative",
                                "Lodgement unsucessfull", JOptionPane.INFORMATION_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(mainPnl, "Lodgement canceled: no input entered");
                }
            } else {
                JOptionPane.showMessageDialog(mainPnl, "Lodgement canceled: by User");
            }
        } else {
            JOptionPane.showMessageDialog(mainPnl, "Lodgement canceled: attempting to lodge to non existant account");
        }
    }

    // allow an account holder to withdraw money from their account
    private void makeWithdrawal() {
        System.out.println("Withdraw");

        if (pointer > 0 && pointer <= MAX_ACC && currentAccount.getNumber() != 0) {
            String input = JOptionPane.showInputDialog(mainPnl, "Please Enter the amount you would like to withdraw");
            double amount;
            if (input != null) {
                if (!input.isEmpty()) {
                    try {
                        amount = Double.parseDouble(input);
                    } catch (NumberFormatException nfEx) {
                        System.err.println("Input Error => incorrect data entered in input dialog");
                        JOptionPane.showMessageDialog(mainPnl, "Incorrect input: " + nfEx.getMessage(),
                                "Withdrawal unsucessfull", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    int result = currentAccount.withdrawal(amount);

                    if (result == 0) {
                        try {
                            writeAccToFile(currentAccount);
                        } catch (IOException ioEx) {
                            System.err.println("[!!!]\nException " + ioEx.getClass().getSimpleName()
                                    + " occured while attempting to modify file.\nException Message: "
                                    + ioEx.getMessage() + "\n[!!!]");

                            JOptionPane.showMessageDialog(mainPnl,
                                    "Program could not write new data to file because of " + ioEx.toString(),
                                    "Withdrawal unsucessfull", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        JOptionPane.showMessageDialog(mainPnl, "Widthdrawal amount: " + amount,
                                "Widthdrawal sucessfull", JOptionPane.INFORMATION_MESSAGE);
                    } else if (result == 2) {
                        JOptionPane.showMessageDialog(mainPnl,
                                "Cannot widthdraw amount: " + amount
                                        + "\nthe amount exceed the current balance and overdraft limit",
                                "Widthdrawal unsucessfull", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(mainPnl,
                                "Cannot widthdraw amount: " + amount + "\nthe withdrawal amount is negative",
                                "Widthdrawal unsucessfull", JOptionPane.INFORMATION_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(mainPnl, "Widthdrawal canceled: no input entered");
                }
            } else {
                JOptionPane.showMessageDialog(mainPnl, "Widthdrawal canceled: by User");
            }

        } else {
            JOptionPane.showMessageDialog(mainPnl,
                    "Widthdrawal canceled: attempting to withdraw from non existant account");
        }

    }

    // allow an account holder to request a new overdraft limit
    public void requestOverdraft() {
        System.out.println("Overdraft");

        if (pointer > 0 && pointer <= MAX_ACC && currentAccount.getNumber() != 0) {
            String input = JOptionPane.showInputDialog(mainPnl, "Please Enter the new overdraft limit");
            double newLimit;
            if (input != null) {
                if (!input.isEmpty()) {
                    try {
                        newLimit = Double.parseDouble(input);
                    } catch (NumberFormatException nfEx) {
                        System.err.println("Input Error => incorrect data entered in input dialog");
                        JOptionPane.showMessageDialog(mainPnl, "Incorrect input: " + nfEx.getMessage(),
                                "Setting Overdraft Limit unsucessfull", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    int result = currentAccount.setNewOverdraft(newLimit);

                    if (result == 0) {
                        try {
                            writeAccToFile(currentAccount);
                        } catch (IOException ioEx) {
                            System.err.println("[!!!]\nException " + ioEx.getClass().getSimpleName()
                                    + " occured while attempting to modify file.\nException Message: "
                                    + ioEx.getMessage() + "\n[!!!]");

                            JOptionPane.showMessageDialog(mainPnl,
                                    "Program could not write new data to file because of " + ioEx.toString(),
                                    "Setting Overdraft Limit unsucessfull", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        JOptionPane.showMessageDialog(mainPnl, "New Overdraft Limit set to: " + newLimit,
                                "Setting Overdraft Limit sucessfull", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(mainPnl,
                                "Cannot set the Overdraft Limit to: " + newLimit + "\nthe new limit has negative value",
                                "Setting Overdraft Limit unsucessfull", JOptionPane.INFORMATION_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(mainPnl, "Setting Overdraft Limit canceled: no input entered");
                }
            } else {
                JOptionPane.showMessageDialog(mainPnl, "Setting Overdraft Limit canceled: by User");
            }
        } else {
            JOptionPane.showMessageDialog(mainPnl,
                    "Setting Overdraft Limit canceled: attempting to lodge to non existant account");
        }

    }


    /* ==== Navigation Methods ==== */

    private void getData() {
        System.out.println("Pull");

        if (!numberTxF.getText().isEmpty()) {
            if (verifyAccountNumber()) {
                int input = Integer.parseInt(numberTxF.getText());
                if (input > 0 && input <= MAX_ACC) {
                    pointer = input;
                    try {
                        currentAccount = readAccFromFile(pointer);

                    } catch (IOException ioEx) {
                        System.err.println("[!!!]\nException " + ioEx.getClass().getSimpleName()
                                + " occured while loading the account from file.\nException Message: "
                                + ioEx.getMessage());
                        JOptionPane.showMessageDialog(mainPnl,
                                "Exception occured while loading the account from file.\nException: " + ioEx.toString(),
                                ioEx.getClass().getSimpleName(), JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(mainPnl, "The requested Account is outside the bounds",
                            "Unable to load the Account", JOptionPane.WARNING_MESSAGE);
                }
            }
        }

    }

    private void nextAccount() {
        System.out.println("Next");

        if (pointer < MAX_ACC) {
            try {
                currentAccount = readAccFromFile(++pointer);

            } catch (IOException ioEx) {
                System.err.println("[!!!]\nException " + ioEx.getClass().getSimpleName()
                        + " occured while loading the account from file.\nException Message: " + ioEx.getMessage());
                JOptionPane.showMessageDialog(mainPnl,
                        "Exception occured while loading the account from file.\nException: " + ioEx.toString(),
                        ioEx.getClass().getSimpleName(), JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(mainPnl,
                    "You're at the end of Database, please press [prev] to load previous account", "Last Record",
                    JOptionPane.WARNING_MESSAGE);
        }

    }

    private void prevAccount() {
        System.out.println("Previous");

        if (pointer > 1) {
            try {
                currentAccount = readAccFromFile(--pointer);

            } catch (IOException ioEx) {
                System.err.println("[!!!]\nException " + ioEx.getClass().getSimpleName()
                        + " occured while loading the previous account from file.\nException Message: "
                        + ioEx.getMessage());
                JOptionPane.showMessageDialog(mainPnl,
                        "Exception occured while loading the account from file.\nException: " + ioEx.toString(),
                        ioEx.getClass().getSimpleName(), JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(mainPnl,
                    "You're at the start of Database, please press [next] to load next account", "First Record",
                    JOptionPane.WARNING_MESSAGE);
        }

    }

    /* ==== Helper methods ==== */

    private void clearFields() {
        numberTxF.setText("");
        nameTxF.setText("");
        balanceTxF.setText("");
        overdraftTxF.setText("");
    }

    public void reset() {
        currentAccount = new Account(0, "");
        pointer = 0;
    }

    private void populateFields() {
        if (pointer != 0) {
            if (currentAccount.getNumber() != 0) {
                numberTxF.setText(Integer.toString(currentAccount.getNumber()));
                nameTxF.setText(currentAccount.getName().trim());
                balanceTxF.setText(Double.toString(currentAccount.balance()));
                overdraftTxF.setText(Double.toString(currentAccount.overdraftLimit()));
            } else {
                clearFields();
                numberTxF.setText(Integer.toString(pointer));
            }
        }
    }


    /* IO Methods */

    private boolean recordAvailable(int index) {

        try {
            raf.seek((index - 1) * RECORD_SIZE);
            if (raf.readInt() < 1) {
                return false;
            }
        } catch (Exception ex) {
            System.err.println("[!!!]\nException " + ex.getClass().getSimpleName()
                    + " occured while attempting to read from file.\nException Message: " + ex.getMessage() + "\n[!!!]");
            JOptionPane.showMessageDialog(
                    mainPnl, "Exception " + ex.getClass().getSimpleName() + " occured.\n"
                            + "Could not read from file, because: " + ex.getMessage() + " ",
                    "Cannot access data file", JOptionPane.ERROR_MESSAGE);
        }
        return true;
    }

    private void writeAccToFile(Account acc) throws IOException {

        if (pointer == acc.getNumber() || acc.getNumber() == 0) {
            StringBuilder sb = new StringBuilder(acc.getName());
            sb.setLength(NAME_MAX_LENGTH);

            raf.seek((pointer - 1) * RECORD_SIZE);
            raf.writeInt(acc.getNumber());
            raf.writeChars(sb.toString());
            raf.writeDouble(acc.balance());
            raf.writeDouble(acc.overdraftLimit());

            //throw new IOException("This is a test exception, thrown from the writeToFile() function...");

        } else {
            System.err.println("writeing to file=> internal logic error, account index does not match account pointer");
        }

    }

    private Account readAccFromFile(int index) throws IOException {
        raf.seek((index - 1) * RECORD_SIZE);
        int number = raf.readInt();
        // if (number == 0) {
        //     return new Account(index, "Empty");
        // }
        char[] tempName = new char[NAME_MAX_LENGTH];
        for (int i = 0; i < tempName.length; i++) {
            tempName[i] = raf.readChar();
        }
        double balance = raf.readDouble();
        double odLimit = raf.readDouble();

        //throw new IOException("This is a test exception, thrown from the readFromFile() function...");

        return new Account(number, new String(tempName), balance, odLimit);
    }


    /* Main Method */

    public static void main(String[] args) {
        CreditUnion app = new CreditUnion();

    }
}

class Account {
    private int number;
    private String name;
    private double balance, odLimit;

    public Account(int number, String name, double balance, double odLimit) {
        this.number = number;
        this.name = name;
        this.balance = balance;
        this.odLimit = odLimit;
    }

    public Account(int number, String name, double balance) {
        this(number, name, balance, 0.0);
    }

    public Account(int number, String name) {
        this(number, name, 0.0, 0.0);
    }

    public int getNumber() {
        return number;
    }

    public String getName() {
        return name;
    }

    public void changeName(String name) {
        this.name = name;
    }

    // make a lodgement
    public int lodgement(double amount) {
        if (amount > 0) {
            balance += amount;
            return 0;
        } else {
            return 1;
        }
    }

    // remove a sum of money from the account on condition that there are sufficient funds available to meet the request
    public int withdrawal(double amount) {
        if (amount > 0) {
            if (amount <= (balance + odLimit)) {
                balance -= amount;
                return 0;
            } else {
                return 2;
            }
        } else {
            return 1;
        }
    }

    // check current balance
    public double balance() {
        return balance;
    }

    //check current overdraft limit
    public double overdraftLimit() {
        return odLimit;
    }

    //set the overdraft limit to some given value.
    public int setNewOverdraft(double newLimit) {
        if (newLimit >= 0) {
            odLimit = newLimit;
            return 0;
        } else {
            return 1;
        }

    }

    @Override
    public String toString() {
        return "[" + this.number + ", " + this.name + ", " + this.balance + ", " + this.odLimit + "]";
    }

}