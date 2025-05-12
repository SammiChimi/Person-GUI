// Erick Lujan
// Person GUI
// Advanced Java
// OCCC Spring 2025

import com.github.lgooddatepicker.components.CalendarPanel;
import com.github.lgooddatepicker.optionalusertools.CalendarListener;
import com.github.lgooddatepicker.zinternaltools.CalendarSelectionEvent;
import com.github.lgooddatepicker.zinternaltools.YearMonthChangeEvent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private boolean changed = false;
    private File loadedFile;
    private String msg;
    private OCCCDate dob;

    JMenuItem mniNew, mniOpen, mniSave, mniSaveAs, mniExit, mniSaveEdit;
    JTextField txtFirstName, txtLastName, txtGovID, txtStudentID;
    CalendarPanel pnlCalendar;
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

            // ADD HELP ITEMS HERE
            mniSaveEdit = new JMenuItem("Saving and Editing");

            JMenuItem[] mniHelp = {mniSaveEdit};
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
            JLabel lblGovID = new JLabel("Government ID:");
            JLabel lblStudentID = new JLabel("StudentID:");
            JLabel lblDoB = new JLabel("Date of Birth: ");

            JLabel[] lblList = {lblFirstName, lblLastName, lblGovID, lblStudentID, lblDoB};
            for (JLabel lbl : lblList) {
                pnlEntryGrid.add(lbl, gbc);
                gbc.gridy++;
            }

            gbc.gridx++;
            gbc.gridy = 0;

            txtFirstName = new JTextField();
            txtLastName = new JTextField();
            txtGovID = new JTextField();
            txtStudentID = new JTextField();

            JTextField[] txtList = {txtFirstName, txtLastName, txtGovID, txtStudentID};
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
            }

            pnlCalendar = new CalendarPanel();
            pnlCalendar.addCalendarListener(new CalendarListener() {
                @Override
                public void selectedDateChanged(CalendarSelectionEvent cse) {
                    dob = new OCCCDate(GregorianCalendar.from(cse.getNewDate().atStartOfDay(ZoneId.systemDefault())));
                }

                @Override
                public void yearMonthChanged(YearMonthChangeEvent yearMonthChangeEvent) {

                }
            });
            pnlEntryGrid.add(pnlCalendar, gbc);

            // Buttons
            JPanel pnlButtons = new JPanel();
            gbc.gridy++;

            btnAddNew = new JButton("Add New");
            btnEdit = new JButton("Edit");
            btnDelete = new JButton("Delete");

            JButton[] btnList = {btnAddNew, btnEdit, btnDelete};
            for (JButton btn : btnList) {
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
            newFile();
        }
        if (e.getSource() == mniSaveAs) {
            saveAsFile();
        }
        if (e.getSource() == mniSave) {
            saveFile();
        }
        if (e.getSource() == mniOpen) {
            openFile();
        }
        if (e.getSource() == mniExit) {
            exitProgram();
        }
        if (e.getSource() == mniSaveEdit) {
            JOptionPane.showMessageDialog(this,
                    """
                            New people are created via the "Add New..." option in the file menu \
                            after the Person information is entered into the various textboxes.
                            
                            Edits via the "Edit" button will override the currently selected \
                            person with new information entered.""",
                    "About PersonGUI", JOptionPane.PLAIN_MESSAGE);
        }
        if (e.getSource() == btnAddNew) {
            addNewPerson();
        }
        if (e.getSource() == btnEdit) {
            msg = "Please enter person information to edit selection.";
            Person editedPerson = makePerson();
            if (editedPerson != null) {
                personList.removeElement(lstPeople.getSelectedValue());
                personList.addElement(editedPerson);
                clearTxtFields();
                changed = true;
            } else {
                JOptionPane.showMessageDialog(null, msg, "Message", JOptionPane.INFORMATION_MESSAGE);
            }
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

    private void addNewPerson() {
        msg = "Please enter person information to add a Person entry.";
        Person newPerson = makePerson();

        if (newPerson != null && !isCopy(newPerson)) {
            personList.addElement(newPerson);
            clearTxtFields();
            changed = true;
        } else {
            JOptionPane.showMessageDialog(null, msg, "Message", JOptionPane.INFORMATION_MESSAGE);
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
            txtFirstName.setText(selection.getFirstName());
            txtLastName.setText(selection.getLastName());
            OCCCDate d = selection.getDoB();
            pnlCalendar.setSelectedDate(LocalDate.of(d.getYear(), d.getMonthOfYear(), d.getDayOfMonth()));
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
        pnlCalendar.setSelectedDate(LocalDate.now());
        lblClassType.setText("Type:");
    }

    private void newFile() {
        if (saveContinue()) {
            loadedFile = null;
            clearTxtFields();
            personList.clear();
        }
    }

    private void saveFile() {
        if (loadedFile == null) {
            saveAsFile();
        } else {
            saveFile(loadedFile);
        }
    }

    private void saveAsFile() {
        JFileChooser fc = new JFileChooser(System.getProperty("user.dir"));
        fc.setFileFilter(filter);

        if (fc.showSaveDialog(PersonGUI.this) == JFileChooser.APPROVE_OPTION) {
            loadedFile = fc.getSelectedFile();
            saveFile(loadedFile);
            changed = false;
        }
    }

    private void openFile() {
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
            System.out.println(ioe);
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

    private void exitProgram() {
        if (saveContinue()) {
            dispose();
            System.exit(0);
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
                saveFile();
            }
        }

        return true;
    }
}
