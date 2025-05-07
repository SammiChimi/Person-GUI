// Erick Lujan
// Person GUI
// Advanced Java
// OCCC Spring 2025

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.filechooser.FileFilter;

public class PersonGUI extends JFrame implements ActionListener {

    private static final int WIDTH = 600;
    private static final int HEIGHT = 300;
    private File loadedFile;
    private final FileFilter filter = new FileFilter() {
        @Override
        public boolean accept(File f) {
            if (f.isDirectory()) {
                return true;
            } else {
                return f.getName().toLowerCase().endsWith(".txt");
            }
        }

        @Override
        public String getDescription() {
            return "Text files (*.txt)";
        }
    };
    private String msg;

    JMenuBar bar;
    JMenu mnuFile, mnuHelp;
    JMenuItem mniNew, mniOpen, mniSave, mniSaveAs, mniExit, mniHowTo;
    JTextField txtFirstName, txtLastName, txtGovID, txtStudentID;
    JButton btnAddNew, btnEdit, btnDelete;
    JLabel lblClassType;
    JComboBox<Person> cbxPeople;

    public static void main(String[] args) {
        new PersonGUI();
    }

    public PersonGUI() {
        setResizable(false);
        setSize(WIDTH, HEIGHT);
        setTitle("Person Editor");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exitProgram();
            }
        });

        // File menu
        bar = new JMenuBar();
        mnuFile = new JMenu("File");
        mnuFile.setMnemonic(KeyEvent.VK_F);

        mniNew = new JMenuItem("New...");
        mniNew.setMnemonic(KeyEvent.VK_N);

        mniOpen = new JMenuItem("Open");
        mniOpen.setMnemonic(KeyEvent.VK_O);

        mniSave = new JMenuItem("Save");
        mniSave.setMnemonic(KeyEvent.VK_S);

        mniSaveAs = new JMenuItem("Save as...");
        mniSave.setMnemonic(KeyEvent.VK_A);

        mniExit = new JMenuItem("Exit");
        mniExit.setMnemonic(KeyEvent.VK_X);

        JMenuItem[] mniList = {mniNew, mniOpen, mniSave, mniSaveAs, mniExit};
        for (JMenuItem mni : mniList) {
            if (mni == mniExit) {
                mnuFile.addSeparator();
            }
            mni.addActionListener(this);
            mnuFile.add(mni);
        }

        // Help menu
        mnuHelp = new JMenu("Help");
        mnuHelp.setMnemonic(KeyEvent.VK_H);

        mniHowTo = new JMenuItem("Saving and Editing");
        mniHowTo.addActionListener(this);

        mnuHelp.add(mniHowTo);

        // Bar
        bar.add(mnuFile);
        bar.add(Box.createHorizontalGlue());
        bar.add(mnuHelp);
        setJMenuBar(bar);

        // Main panel
        setLayout(new BorderLayout());
        JPanel pnlCenter = new JPanel(new GridLayout(1,2));

        JPanel pnlInfoGrid = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Entry fields
        gbc.insets = new Insets(0,20,10,0);
        gbc.gridx = 0;
        gbc.weightx = .2;

        JLabel lblFirstName = new JLabel("First Name:");
        JLabel lblLastName = new JLabel("Last Name:");
        JLabel lblGovID = new JLabel("Government ID:");
        JLabel lblStudentID = new JLabel("StudentID:");

        int y = 0;
        JLabel[] lblList = {lblFirstName, lblLastName, lblGovID, lblStudentID};
        for (JLabel lbl : lblList) {
            gbc.gridy = y++;
            pnlInfoGrid.add(lbl, gbc);
        }

        gbc.insets = new Insets(0,0,10,20);
        gbc.gridx = 1;
        gbc.weightx = .3;

        y = 0;
        JTextField[] txtList = {txtFirstName, txtLastName, txtGovID, txtStudentID};
        for (JTextField txt : txtList) {
            gbc.gridy = y++;
            txt = new JTextField();
            pnlInfoGrid.add(txt, gbc);
        }

        // ComboBox & type JLabel
        cbxPeople = new JComboBox<>();
        cbxPeople.addItemListener(_ -> displaySelection());
        gbc.gridx = 3;
        gbc.gridy = 1;
        gbc.weightx = .6;
        pnlInfoGrid.add(cbxPeople, gbc);

        lblClassType = new JLabel();
        gbc.gridy = 2;
        pnlInfoGrid.add(lblClassType, gbc);

        // Buttons
        JPanel pnlButtons = new JPanel();

        btnAddNew = new JButton("Add New");
        btnEdit = new JButton("Edit");
        btnDelete = new JButton("Delete");

        JButton[] btnList = {btnAddNew, btnEdit, btnDelete};
        for (JButton btn : btnList) {
            btn.addActionListener(this);
            pnlButtons.add(btn);
        }

        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        pnlInfoGrid.add(pnlButtons, gbc);

        // Main panel
        pnlCenter.add(pnlInfoGrid);
        add(pnlCenter, BorderLayout.CENTER);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == mniSaveAs) {
            saveNewFile();
        }
        if (e.getSource() == mniSave) {
            if (loadedFile == null) {
                saveNewFile();
            } else {
                saveToFile(loadedFile);
            }
        }
        if (e.getSource() == mniOpen) {
            loadFromFile();
        }
        if (e.getSource() == mniExit) {
            exitProgram();
        }
        if (e.getSource() == mniHowTo) {
            JOptionPane.showMessageDialog(null,
                    """
                            New people are created via the "Add New..." option in the file menu \
                            after the Person information is entered into the various textboxes.
                            
                            Edits via the "Edit" button will override the currently selected \
                            person with new information entered.""",
                    "About PersonGUI", JOptionPane.PLAIN_MESSAGE);
        }

        if (e.getSource() == btnAddNew) {
            msg = "Please enter person information to add a Person entry.";
            Person newPerson = makePerson();

            if (newPerson != null && !isCopy(newPerson)) {
                cbxPeople.addItem(newPerson);
                cbxPeople.setSelectedItem(newPerson);
                displaySelection();
                return;
            }
            JOptionPane.showMessageDialog(null, msg, "Message", JOptionPane.INFORMATION_MESSAGE);
        }
        if (e.getSource() == btnEdit) {
            msg = "Please enter person information to edit selection.";
            Person editedPerson = makePerson();
            if (editedPerson != null) {
                cbxPeople.removeItem(cbxPeople.getSelectedItem());
                cbxPeople.addItem(editedPerson);
                cbxPeople.setSelectedItem(editedPerson);
            } else {
                JOptionPane.showMessageDialog(null, msg, "Message", JOptionPane.INFORMATION_MESSAGE);
            }

        }
        if (e.getSource() == btnDelete) {
            cbxPeople.removeItem(cbxPeople.getSelectedItem());
        }
    }

    private boolean isCopy(Person newPerson) {
        for (int i = 0; i < cbxPeople.getItemCount(); i++) {
            if (newPerson.equals(cbxPeople.getItemAt(i))) {
                msg = "This person already exists.";
                return true;
            }
        }
        return false;
    }

    private Person makePerson() {
        Person newPerson = null;
        if (!txtFirstName.getText().isEmpty()) {
            newPerson = new Person(txtFirstName.getText(), txtLastName.getText(), new OCCCDate());
            if (!txtGovID.getText().isEmpty()) {
                newPerson = new RegisteredPerson(newPerson, txtGovID.getText());
                if (!txtStudentID.getText().isEmpty()) {
                    newPerson = new OCCCPerson((RegisteredPerson) newPerson, txtStudentID.getText());
                }
            } else if (!txtStudentID.getText().isEmpty()) {
                msg = "Students must have a government ID.";
                return null;
            }
        }

        return newPerson;
    }

    private void displaySelection() {
        txtFirstName.setText("");
        txtLastName.setText("");
        txtGovID.setText("");
        txtStudentID.setText("");
        Person selection = (Person) cbxPeople.getSelectedItem();
        if (selection != null) {
            txtFirstName.setText(selection.getFirstName());
            txtLastName.setText(selection.getLastName());
            if (selection instanceof RegisteredPerson rp) {
                txtGovID.setText(rp.getGovernmentID());
                if (selection instanceof OCCCPerson op) {
                    txtStudentID.setText(op.getStudentID());
                }
            }
            lblClassType.setText("Type: " + selection.getClass().getName());
        }
    }

    private void loadFromFile() {
        JFileChooser fc = new JFileChooser(System.getProperty("user.dir"));
        fc.setFileFilter(filter);

        if (fc.showOpenDialog(PersonGUI.this) == JFileChooser.APPROVE_OPTION) {
            loadedFile = fc.getSelectedFile();
            loadFromFile(loadedFile);
        }
    }

    private void saveNewFile() {
        JFileChooser fc = new JFileChooser(System.getProperty("user.dir"));
        fc.setFileFilter(filter);

        if (fc.showSaveDialog(PersonGUI.this) == JFileChooser.APPROVE_OPTION) {
            loadedFile = fc.getSelectedFile();
            saveToFile(loadedFile);
        }
    }

    private void loadFromFile(File peopleFile) {
        try {
            FileInputStream fin = new FileInputStream(peopleFile);
            ObjectInputStream oin = new ObjectInputStream(fin);
            cbxPeople.removeAllItems();
            while (true) {
                cbxPeople.addItem((Person) oin.readObject());
            }
        } catch (Exception _) {
        }
    }

    private void saveToFile(File saveFile) {
        try {
            FileOutputStream fout = new FileOutputStream(saveFile);
            ObjectOutputStream oout = new ObjectOutputStream(fout);

            for (int i = 0; i < cbxPeople.getItemCount(); i++) {
                oout.writeObject(cbxPeople.getItemAt(i));
            }
        } catch (IOException ioe) {
            System.out.println(ioe);
        }
    }

    private void exitProgram() {
        dispose();
        System.exit(0);
    }
}
