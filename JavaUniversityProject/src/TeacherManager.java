import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;


public class TeacherManager extends Frame implements ActionListener {
    private ArrayList<Teacher> teachers;
    private List teacherList;
    private Button addButton, modifyButton, deleteButton, exitButton;
    private Panel inputPanel;
    private Dialog addDialog;
    private CheckboxGroup radioGroup;
    private Checkbox highSchoolRadio, collegeRadio;
    private TextField nameField, experienceField, gradeField, nrOfHoursField, titleField, yearOfEmploymentField;
    private Button saveButton, cancelButton;

    private boolean isModifying = false;
    private int modifyingIndex = -1;

    public TeacherManager() {
        teachers = new ArrayList<>();
        loadTeachers();

        setTitle("Teacher Management");

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                saveTeachers();
                System.exit(0);
            }
        });
        setLayout(new BorderLayout());

        teacherList = new List();
        for (Teacher teacher : teachers) {
            teacherList.add(teacher.toString());
        }
        add(teacherList, BorderLayout.CENTER);

        Panel buttonPanel = new Panel();
        addButton = new Button("Add");
        modifyButton = new Button("Modify");
        deleteButton = new Button("Delete");
        exitButton = new Button("Exit");
        addButton.addActionListener(this);
        modifyButton.addActionListener(this);
        deleteButton.addActionListener(this);
        exitButton.addActionListener(this);
        buttonPanel.add(addButton);
        buttonPanel.add(modifyButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(exitButton);
        add(buttonPanel, BorderLayout.SOUTH);

        createAddDialog();

        setSize(800, 200);
        setLocationRelativeTo(null);
    }

    private void createAddDialog() {
        addDialog = new Dialog(this, "Add Teacher", true);
        addDialog.setLayout(new BorderLayout());

        inputPanel = new Panel(new GridLayout(8, 2));
        radioGroup = new CheckboxGroup();
        highSchoolRadio = new Checkbox("High School", radioGroup, false);
        collegeRadio = new Checkbox("College", radioGroup, false);
        inputPanel.add(highSchoolRadio);
        inputPanel.add(collegeRadio);
        nameField = new TextField();
        experienceField = new TextField();
        gradeField = new TextField();
        nrOfHoursField = new TextField();
        titleField = new TextField();
        yearOfEmploymentField = new TextField();

        // Set preferred width for text fields
        int textFieldWidth = 200;
        nameField.setPreferredSize(new Dimension(textFieldWidth, nameField.getPreferredSize().height));
        experienceField.setPreferredSize(new Dimension(textFieldWidth, experienceField.getPreferredSize().height));
        gradeField.setPreferredSize(new Dimension(textFieldWidth, gradeField.getPreferredSize().height));
        nrOfHoursField.setPreferredSize(new Dimension(textFieldWidth, nrOfHoursField.getPreferredSize().height));
        titleField.setPreferredSize(new Dimension(textFieldWidth, titleField.getPreferredSize().height));
        yearOfEmploymentField.setPreferredSize(new Dimension(textFieldWidth, yearOfEmploymentField.getPreferredSize().height));

        inputPanel.add(new Label("Name:"));
        inputPanel.add(nameField);
        inputPanel.add(new Label("Experience:"));
        inputPanel.add(experienceField);
        inputPanel.add(new Label("Grade:"));
        inputPanel.add(gradeField);
        inputPanel.add(new Label("Number of Hours:"));
        inputPanel.add(nrOfHoursField);
        inputPanel.add(new Label("Title:"));
        inputPanel.add(titleField);
        inputPanel.add(new Label("Year of Employment:"));
        inputPanel.add(yearOfEmploymentField);
        addDialog.add(inputPanel, BorderLayout.CENTER);

        Panel buttonPanel = new Panel();
        saveButton = new Button("Save");
        cancelButton = new Button("Cancel");
        saveButton.addActionListener(this);
        cancelButton.addActionListener(this);
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        addDialog.add(buttonPanel, BorderLayout.SOUTH);
        addDialog.setLocationRelativeTo(this);

        highSchoolRadio.addItemListener(e -> updateTextFields());
        collegeRadio.addItemListener(e -> updateTextFields());

        addDialog.pack();
        updateTextFields();
    }

    private void loadTeachers() {
        try (BufferedReader reader = new BufferedReader(new FileReader("teachers.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[0].equals("HighSchoolTeacher")) {
                    teachers.add(new HighSchoolTeacher(parts[1], Integer.parseInt(parts[2]),
                            parts[3], Integer.parseInt(parts[4])));
                } else if (parts[0].equals("CollegeTeacher")) {
                    teachers.add(new CollegeTeacher(parts[1], Integer.parseInt(parts[2]),
                            parts[3], Integer.parseInt(parts[4])));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveTeachers() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("teachers.txt"))) {
            for (Teacher teacher : teachers) {
                if (teacher instanceof HighSchoolTeacher) {
                    HighSchoolTeacher highSchoolTeacher = (HighSchoolTeacher) teacher;
                    writer.println("HighSchoolTeacher," + highSchoolTeacher.name + "," + highSchoolTeacher.experience +
                            "," + highSchoolTeacher.grade + "," + highSchoolTeacher.nrOfHours);
                } else if (teacher instanceof CollegeTeacher) {
                    CollegeTeacher collegeTeacher = (CollegeTeacher) teacher;
                    writer.println("CollegeTeacher," + collegeTeacher.name + "," + collegeTeacher.experience +
                            "," + collegeTeacher.title + "," + collegeTeacher.yearOfEmployment);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addOrModifyTeacher() {
        if (highSchoolRadio.getState()) {
            if (!validateHighSchoolFields()) return;
            String name = nameField.getText(); int experience = Integer.parseInt(experienceField.getText());
            String grade = gradeField.getText();
            int nrOfHours = Integer.parseInt(nrOfHoursField.getText());
            HighSchoolTeacher teacher = new HighSchoolTeacher(name, experience, grade, nrOfHours);
            if (isModifying) {
                teachers.set(modifyingIndex, teacher);
                teacherList.replaceItem(teacher.toString(), modifyingIndex);
                isModifying = false;
            } else {
                if (isDuplicate(teacher)) {
                    showMessageDialog("A teacher with the same name and experience already exists.", "Duplicate Entry");
                } else {
                    teachers.add(teacher);
                    teacherList.add(teacher.toString());
                }
            }
        } else if (collegeRadio.getState()) {
            if (!validateCollegeFields()) return;
            String name = nameField.getText();
            int experience = Integer.parseInt(experienceField.getText());
            String title = titleField.getText();
            int yearOfEmployment = Integer.parseInt(yearOfEmploymentField.getText());
            CollegeTeacher teacher = new CollegeTeacher(name, experience, title, yearOfEmployment);
            if (isModifying) {
                teachers.set(modifyingIndex, teacher);
                teacherList.replaceItem(teacher.toString(), modifyingIndex);
                isModifying = false;
            } else {
                if (isDuplicate(teacher)) {
                    showMessageDialog("A teacher with the same name and experience already exists.", "Duplicate Entry");
                } else {
                    teachers.add(teacher);
                    teacherList.add(teacher.toString());
                }
            }
        }
        clearFields();
        addDialog.setVisible(false);
        saveTeachers();
    }

    private boolean validateHighSchoolFields() {
        if (nameField.getText().isEmpty()) {
            showMessageDialog("Please enter a name.", "Validation Error");
            return false;
        }
        if (experienceField.getText().isEmpty()) {
            showMessageDialog("Please enter the experience.", "Validation Error");
            return false;
        }
        try {
            int experience = Integer.parseInt(experienceField.getText());
            if (experience < 0) {
                showMessageDialog("Experience cannot be negative.", "Validation Error");
                return false;
            }
            if (experience > 70) {
                showMessageDialog("Experience cannot be greater than 70.", "Validation Error");
                return false;

            }
        } catch (NumberFormatException ex) {
            showMessageDialog("Please enter a valid number for experience.", "Validation Error");
            return false;
        }
        if (gradeField.getText().isEmpty()) {
            showMessageDialog("Please enter the grade.", "Validation Error");
            return false;
        }
        if (nrOfHoursField.getText().isEmpty()) {
            showMessageDialog("Please enter the number of hours.", "Validation Error");
            return false;
        }
        try {
            int nrOfHours = Integer.parseInt(nrOfHoursField.getText());
            if (nrOfHours < 0) {
                showMessageDialog("Number of hours cannot be negative.", "Validation Error");
                return false;
            }
            if (nrOfHours > 40) {
                showMessageDialog("Number of hours cannot be greater than 40.", "Validation Error");
                return false;
            }
        } catch (NumberFormatException ex) {
            showMessageDialog("Please enter a valid number for number of hours.", "Validation Error");
            return false;
        }
        return true;
    }

    private boolean validateCollegeFields() {
        if (nameField.getText().isEmpty()) {
            showMessageDialog("Please enter a name.", "Validation Error");
            return false;
        }
        if (experienceField.getText().isEmpty()) {
            showMessageDialog("Please enter the experience.", "Validation Error");
            return false;
        }
        try {
            int experience = Integer.parseInt(experienceField.getText());
            if (experience < 0) {
                showMessageDialog("Experience cannot be negative.", "Validation Error");
                return false;
            }
        } catch (NumberFormatException ex) {
            showMessageDialog("Please enter a valid number for experience.", "Validation Error");
            return false;
        }
        if (titleField.getText().isEmpty()) {
            showMessageDialog("Please enter the title.", "Validation Error");
            return false;
        }
        if (yearOfEmploymentField.getText().isEmpty()) {
            showMessageDialog("Please enter the year of employment.", "Validation Error");
            return false;
        }
        try {
            int yearOfEmployment = Integer.parseInt(yearOfEmploymentField.getText());
            if (yearOfEmployment < 0) {
                showMessageDialog("Year of employment cannot be negative.", "Validation Error");
                return false;
            }
        } catch (NumberFormatException ex) {
            showMessageDialog("Please enter a valid number for year of employment.", "Validation Error");
            return false;
        }
        return true;
    }

    private boolean isDuplicate(Teacher teacher) {
        for (Teacher existingTeacher : teachers) {
            if (existingTeacher.getClass().equals(teacher.getClass()) &&
                    existingTeacher.name.equalsIgnoreCase(teacher.name) &&
                    existingTeacher.experience == teacher.experience) {
                return true;
            }
        }
        return false;
    }

    private void modifyTeacher() {
        int selectedIndex = teacherList.getSelectedIndex();
        if (selectedIndex != -1) {
            isModifying = true;
            modifyingIndex = selectedIndex;
            Teacher teacher = teachers.get(selectedIndex);
            if (teacher instanceof HighSchoolTeacher) {
                HighSchoolTeacher highSchoolTeacher = (HighSchoolTeacher) teacher;
                highSchoolRadio.setState(true);
                nameField.setText(highSchoolTeacher.name);
                experienceField.setText(String.valueOf(highSchoolTeacher.experience));
                gradeField.setText(highSchoolTeacher.grade);
                nrOfHoursField.setText(String.valueOf(highSchoolTeacher.nrOfHours));
            } else if (teacher instanceof CollegeTeacher) {
                CollegeTeacher collegeTeacher = (CollegeTeacher) teacher;
                collegeRadio.setState(true);
                nameField.setText(collegeTeacher.name);
                experienceField.setText(String.valueOf(collegeTeacher.experience));
                titleField.setText(collegeTeacher.title);
                yearOfEmploymentField.setText(String.valueOf(collegeTeacher.yearOfEmployment));
            }
            addDialog.setVisible(true);
        }
    }

    private void deleteTeacher() {
        int selectedIndex = teacherList.getSelectedIndex();
        if (selectedIndex != -1) {
            showConfirmationDialog("Are you sure you want to delete the selected teacher?", "Confirmation");
        }
    }

    private void updateTextFields() {
        if (highSchoolRadio.getState()) {
            gradeField.setEnabled(true);
            nrOfHoursField.setEnabled(true);
            titleField.setEnabled(false);
            yearOfEmploymentField.setEnabled(false);
        } else if (collegeRadio.getState()) {
            gradeField.setEnabled(false);
            nrOfHoursField.setEnabled(false);
            titleField.setEnabled(true);
            yearOfEmploymentField.setEnabled(true);
        }
    }

    private void showConfirmationDialog(String message, String title) {
        Dialog dialog = new Dialog(this, title, true);
        dialog.setLayout(new BorderLayout());
        dialog.add(new Label(message), BorderLayout.CENTER);

        Panel buttonPanel = new Panel(new FlowLayout());
        Button yesButton = new Button("Yes");
        Button noButton = new Button("No");

        yesButton.addActionListener(e -> {
            int selectedIndex = teacherList.getSelectedIndex();
            if (selectedIndex != -1) {
                teachers.remove(selectedIndex);
                teacherList.remove(selectedIndex);
            }
            dialog.dispose();
        });

        noButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(yesButton);
        buttonPanel.add(noButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void clearFields() {
        nameField.setText("");
        experienceField.setText("");
        gradeField.setText("");
        nrOfHoursField.setText("");
        titleField.setText("");yearOfEmploymentField.setText("");
        radioGroup.setSelectedCheckbox(null);
    }

    private void showMessageDialog(String message, String title) {
        Dialog dialog = new Dialog(this, title, true);
        dialog.setLayout(new BorderLayout());
        dialog.add(new Label(message), BorderLayout.CENTER);

        Panel buttonPanel = new Panel(new FlowLayout());
        Button okButton = new Button("OK");
        okButton.addActionListener(e -> dialog.dispose());
        buttonPanel.add(okButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addButton) {
            isModifying = false;
            addDialog.setVisible(true);
        } else if (e.getSource() == modifyButton) {
            modifyTeacher();
        } else if (e.getSource() == deleteButton) {
            deleteTeacher();
        } else if (e.getSource() == exitButton) {
            saveTeachers();
            System.exit(0);
        } else if (e.getSource() == saveButton) {
            addOrModifyTeacher();
        } else if (e.getSource() == cancelButton) {
            addDialog.setVisible(false);
            clearFields();
        }
    }

    public static void main(String[] args) {
        TeacherManager teacherManager = new TeacherManager();
        teacherManager.setVisible(true);
    }
}
