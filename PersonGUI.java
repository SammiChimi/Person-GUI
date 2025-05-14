// Erick Lujan, Bengiamin Le, Bennett Holliday
// Person GUI
// Advanced Java
// OCCC Spring 2025

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.GregorianCalendar;
import javax.swing.filechooser.FileFilter;

public class PersonGUI extends JFrame implements ActionListener {

    private static final int WIDTH = 660;
    private static final int HEIGHT = 500;
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
    private boolean changed = false, editing = false, adding = true;
    private File loadedFile;
    private String msg;
    private OCCCDate dob;

    JMenuItem mniNew, mniOpen, mniSave, mniSaveAs, mniExit;
    JMenuItem mniAddHelp, mniEditHelp, mniDeleteHelp, mniSaveHelp, mniOpenHelp;
    JTextField txtFirstName, txtLastName, txtGovID, txtStudentID;
    JTextField[] txtList;
    DatePicker dobPicker;
    JButton btnAddNew, btnEdit, btnDelete;
    JList<Person> lstPeople;
    DefaultListModel<Person> personList;
    JLabel lblClassType;

    public static void main(String[] args) {
        new PersonGUI();
    }

    public PersonGUI() {
        setResizable(false);
        setSize(WIDTH, HEIGHT);
        setTitle("Person Editor");
        setLayout(new BorderLayout());
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exitProgram();
            }
        });

        // File menu
        {
            JMenu mnuFile = new JMenu("File");
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

            JMenuItem[] mniFile = {mniNew, mniOpen, mniSave, mniSaveAs, mniExit};
            for (JMenuItem mni : mniFile) {
                if (mni == mniExit) {
                    mnuFile.addSeparator();
                }
                mni.addActionListener(this);
                mnuFile.add(mni);
            }

            // Help menu
            JMenu mnuHelp = new JMenu("Help");
            mnuHelp.setMnemonic(KeyEvent.VK_H);

            mniAddHelp = new JMenuItem("How to Add");
            mniEditHelp = new JMenuItem("How to Edit");
            mniDeleteHelp = new JMenuItem("How to Delete");
            mniSaveHelp = new JMenuItem("Saving Files");
            mniOpenHelp = new JMenuItem("Opening Files");

            JMenuItem[] mniHelp = {mniAddHelp, mniEditHelp, mniDeleteHelp, mniSaveHelp, mniOpenHelp};

            for (JMenuItem mni : mniHelp) {
                mni.addActionListener(this);
                mnuHelp.add(mni);
            }

            // Bar
            JMenuBar bar = new JMenuBar();
            bar.add(mnuFile);
            bar.add(Box.createHorizontalGlue());
            bar.add(mnuHelp);
            setJMenuBar(bar);
        }
        // Entry panel
        {
            JPanel pnlEntryGrid = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();

            // Entry fields
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(0, 20, 10, 0);
            gbc.gridx = 0;
            gbc.gridy = 0;

            JLabel lblFirstName = new JLabel("First Name:");
            JLabel lblLastName = new JLabel("Last Name:");
            JLabel lblDoB = new JLabel("Date of Birth:");
            JLabel lblGovID = new JLabel("Government ID:");
            JLabel lblStudentID = new JLabel("StudentID:");

            JLabel[] lblList = {lblFirstName, lblLastName, lblDoB, lblGovID, lblStudentID};
            for (JLabel lbl : lblList) {
                pnlEntryGrid.add(lbl, gbc);
                gbc.gridy++;
            }

            // Text fields and date picker
            gbc.gridx++;
            gbc.gridy = 0;

            txtFirstName = new JTextField();
            txtLastName = new JTextField();
            txtGovID = new JTextField();
            txtStudentID = new JTextField();

            DatePickerSettings dps = new DatePickerSettings();
            dobPicker = new DatePicker(dps);
            dps.setDateRangeLimits(LocalDate.MIN, LocalDate.now());
            dobPicker.addDateChangeListener(dce -> {
                if (dce.getNewDate() != null) {
                    dob = new OCCCDate(GregorianCalendar.from(dce.getNewDate().atStartOfDay(ZoneId.systemDefault())));
                } else {
                    dob = null;
                }
            });
            txtList = new JTextField[]{txtFirstName, txtLastName, txtGovID, txtStudentID};
            for (JTextField txt : txtList) {
                txt.addKeyListener(new KeyListener() {
                    @Override
                    public void keyTyped(KeyEvent e) {

                    }

                    @Override
                    public void keyPressed(KeyEvent e) {
                        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                            addNewPerson();
                        }
                    }

                    @Override
                    public void keyReleased(KeyEvent e) {

                    }
                });
                pnlEntryGrid.add(txt, gbc);
                gbc.gridy++;
                if (gbc.gridy == 2) {
                    pnlEntryGrid.add(dobPicker, gbc);
                    gbc.gridy++;
                }
            }

            // Buttons
            JPanel pnlButtons = new JPanel();
            gbc.gridy++;

            btnAddNew = new JButton("Add New");
            btnEdit = new JButton("Edit");
            btnDelete = new JButton("Delete");

            JButton[] btnList = {btnAddNew, btnEdit, btnDelete};
            for (JButton btn : btnList) {
                if (btn != btnAddNew) {
                    btn.setEnabled(false);
                }
                btn.addActionListener(this);
                pnlButtons.add(btn);
            }

            pnlEntryGrid.add(pnlButtons, gbc);
            add(pnlEntryGrid, BorderLayout.WEST);
        }
        // Person data panel
        {
            JPanel pnlData = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridy = 0;
            personList = new DefaultListModel<>();
            lstPeople = new JList<>(personList);
            lstPeople.addListSelectionListener(_ -> displaySelection());
            lstPeople.setFixedCellWidth(240);
            JScrollPane scpPersonList = new JScrollPane(lstPeople,
                    JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            gbc.insets = new Insets(20, 20, 0, 20);
            gbc.weighty = 1;
            gbc.fill = GridBagConstraints.VERTICAL;
            pnlData.add(scpPersonList, gbc);

            gbc.insets = new Insets(0, 20, 10, 20);
            gbc.gridy++;
            gbc.weighty = 0.1;
            lblClassType = new JLabel("Type:");
            pnlData.add(lblClassType, gbc);

            add(pnlData, BorderLayout.EAST);

            setVisible(true);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == mniNew) {
            createNew();
        }
        if (e.getSource() == mniSaveAs) {
            saveAs();
        }
        if (e.getSource() == mniSave) {
            save();
        }
        if (e.getSource() == mniOpen) {
            open();
        }
        if (e.getSource() == mniExit) {
            exitProgram();
        }
        if (e.getSource() == mniAddHelp) {
            JOptionPane.showMessageDialog(this,
                    """
                            === How to Add ===
                            
                            • Enter First Name, Last Name, and select a Date of Birth.
                            • Government ID is required if you want to assign a Student ID.
                            • Click 'Add New' to add the person to the list.
                            """,
                    "Help: Adding", JOptionPane.INFORMATION_MESSAGE);
        }

        if (e.getSource() == mniEditHelp) {
            JOptionPane.showMessageDialog(this,
                    """
                            === How to Edit ===
                            
                            • Select a person from the list.
                            • Update their information in the fields.
                            • Click 'Edit' to save the changes.
                            """,
                    "Help: Editing", JOptionPane.INFORMATION_MESSAGE);
        }

        if (e.getSource() == mniDeleteHelp) {
            JOptionPane.showMessageDialog(this,
                    """
                            === How to Delete ===
                            
                            • Select a person from the list.
                            • Click 'Delete'.
                            • Confirm the action in the popup dialog.
                            """,
                    "Help: Deleting", JOptionPane.INFORMATION_MESSAGE);
        }

        if (e.getSource() == mniSaveHelp) {
            JOptionPane.showMessageDialog(this,
                    """
                            === Saving Files ===
                            
                            • Use 'File > Save' to save changes to the current file.
                            • Use 'File > Save As...' to create a new save file.
                            • You'll be prompted to save when exiting if changes are unsaved.
                            """,
                    "Help: Saving", JOptionPane.INFORMATION_MESSAGE);
        }

        if (e.getSource() == mniOpenHelp) {
            JOptionPane.showMessageDialog(this,
                    """
                            === Opening Files ===
                            
                            • Use 'File > Open' to load a previously saved .txt file.
                            • Only .txt files saved by this program will load correctly.
                            """,
                    "Help: Opening", JOptionPane.INFORMATION_MESSAGE);
        }
        if (e.getSource() == btnAddNew) {
            if (adding) {
                if (addNewPerson()) {
                    adding = false;
                }
            } else {
                clearTxtFields();
                adding = true;
            }
            setEditingMode(adding);
            lstPeople.clearSelection();
            btnEdit.setEnabled(!adding);
        }
        if (e.getSource() == btnEdit) {
            if (editing) {
                if (editPerson(lstPeople.getSelectedValue())) {
                    editing = false;
                }
            } else {
                editing = true;
            }
            setEditingMode(editing);
            lstPeople.setEnabled(!editing);
            btnAddNew.setEnabled(!editing);
        }
        if (e.getSource() == btnDelete) {
            Person p = lstPeople.getSelectedValue();
            if (p != null) {
                int result = JOptionPane.showConfirmDialog(this, "Do you want to delete " +
                                p.getClass().getName() + " \"" + p.getFirstName() + " " + p.getLastName() + "\"?",
                        "Confirm Delete", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null);
                if (result == JOptionPane.OK_OPTION) {
                    personList.removeElement(lstPeople.getSelectedValue());
                    changed = true;
                }
            }
        }
    }

    private boolean editPerson(Person p) {
        msg = "Please enter person information to edit selection.";
        Person editedPerson = makePerson();
        if (editedPerson != null) {
            personList.removeElement(p);
            personList.addElement(editedPerson);
            clearTxtFields();
            changed = true;
            return true;
        } else {
            JOptionPane.showMessageDialog(null, msg, "Message", JOptionPane.INFORMATION_MESSAGE);
            return false;
        }
    }

    private void setEditingMode(boolean bool) {
        for (JTextField txt : txtList) {
            txt.setEditable(bool);
        }
        dobPicker.setEnabled(bool);

        btnDelete.setEnabled(!bool);
        mniSave.setEnabled(!bool);
        mniSaveAs.setEnabled(!bool);
    }

    private boolean addNewPerson() {
        msg = "Please enter person information to add a Person entry.";
        Person newPerson = makePerson();

        if (newPerson != null && !isCopy(newPerson)) {
            personList.addElement(newPerson);
            clearTxtFields();
            changed = true;
            return true;
        } else {
            JOptionPane.showMessageDialog(null, msg, "Message", JOptionPane.INFORMATION_MESSAGE);
            return false;
        }
    }

    private boolean isCopy(Person newPerson) {
        for (int i = 0; i < personList.getSize(); i++) {
            if (newPerson.equals(personList.get(i))) {
                msg = "This person already exists.";
                return true;
            }
        }
        return false;
    }

    private Person makePerson() {
        Person newPerson = null;
        if (!txtFirstName.getText().isEmpty() && !txtLastName.getText().isEmpty() && dob != null) {
            newPerson = new Person(txtFirstName.getText(), txtLastName.getText(), dob);
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
        Person selection = lstPeople.getSelectedValue();
        if (selection != null) {
            setEditingMode(false);
            btnEdit.setEnabled(true);
            clearTxtFields();
            txtFirstName.setText(selection.getFirstName());
            txtLastName.setText(selection.getLastName());
            OCCCDate d = selection.getDoB();
            dobPicker.setDate(LocalDate.of(d.getYear(), d.getMonthOfYear(), d.getDayOfMonth()));
            if (selection instanceof RegisteredPerson rp) {
                txtGovID.setText(rp.getGovernmentID());
                if (selection instanceof OCCCPerson op) {
                    txtStudentID.setText(op.getStudentID());
                }
            }
            lblClassType.setText("Type: " + selection.getClass().getName());
        }
    }

    private void clearTxtFields() {
        txtFirstName.setText(null);
        txtLastName.setText(null);
        txtGovID.setText(null);
        txtStudentID.setText(null);
        dobPicker.setDate(null);
        dob = null;
        lblClassType.setText("Type:");
    }

    private void createNew() {
        if (saveContinue()) {
            loadedFile = null;
            clearTxtFields();
            personList.clear();
            changed = false;
        }
    }

    private void save() {
        if (loadedFile == null) {
            saveAs();
        } else {
            saveFile(loadedFile);
            changed = false;
        }
    }

    private void saveAs() {
        JFileChooser fc = new JFileChooser(System.getProperty("user.dir"));
        fc.setFileFilter(filter);

        if (fc.showSaveDialog(PersonGUI.this) == JFileChooser.APPROVE_OPTION) {
            loadedFile = fc.getSelectedFile();
            saveFile(loadedFile);
            changed = false;
        }
    }

    private void open() {
        JFileChooser fc = new JFileChooser(System.getProperty("user.dir"));
        fc.setFileFilter(filter);

        if (fc.showOpenDialog(PersonGUI.this) == JFileChooser.APPROVE_OPTION) {
            loadedFile = fc.getSelectedFile();
            loadFile(loadedFile);
            clearTxtFields();
            changed = false;
        }
    }

    private void saveFile(File saveFile) {
        try {
            ObjectOutputStream oout = new ObjectOutputStream(new FileOutputStream(saveFile));

            for (int i = 0; i < personList.size(); i++) {
                oout.writeObject(personList.get(i));
            }
        } catch (IOException ioe) {
            throw new RuntimeException();
        }
    }

    private void loadFile(File peopleFile) {
        try {
            FileInputStream fin = new FileInputStream(peopleFile);
            ObjectInputStream oin = new ObjectInputStream(fin);
            personList.removeAllElements();
            while (true) {
                personList.addElement((Person) oin.readObject());
            }
        } catch (Exception _) {
        }
    }

    private boolean saveContinue() {
        if (changed) {
            int result = JOptionPane.showConfirmDialog(this, "Do you want to save changes?",
                    "Save Changes?", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null);
            if (result == JOptionPane.CANCEL_OPTION) {
                return false;
            }
            if (result == JOptionPane.YES_OPTION) {
                save();
            }
        }

        return true;
    }

    private void exitProgram() {
        if (saveContinue()) {
            dispose();
            System.exit(0);
        }
    }
}
