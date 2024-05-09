import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;


public class TeacherManager extends Frame implements ActionListener {
    private ArrayList<Teacher> teachers;
    private List teacherList;
    private Button addButton, modifyButton, deleteButton,exitButton;
    private Panel inputPanel;
    private Dialog addDialog;
    private CheckboxGroup radioGroup;
    private Checkbox highSchoolRadio, collegeRadio;
    private TextField nameField, experienceField, gradeField, nrOfHoursField, titleField, yearOfEmploymentField;
    private Button saveButton, cancelButton;

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
        teacherList.setPreferredSize(new Dimension(400, 300));
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
        exitButton.addActionListener(this);
        buttonPanel.add(addButton);
        buttonPanel.add(modifyButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(exitButton);
        add(buttonPanel, BorderLayout.SOUTH);

        createAddDialog();

        setSize(600,400);
        setLocationRelativeTo(null);
    }

    private void createAddDialog() {
        addDialog = new Dialog(this, "Add Teacher", true);
        addDialog.setLayout(new BorderLayout());

        inputPanel = new Panel(new GridLayout(10, 2));
        radioGroup = new CheckboxGroup();
        highSchoolRadio = new Checkbox("High School Teacher", radioGroup, false);
        collegeRadio = new Checkbox("College Teacher", radioGroup, false);
        inputPanel.add(highSchoolRadio);
        inputPanel.add(collegeRadio);
        nameField = new TextField();
        experienceField = new TextField();
        gradeField = new TextField();
        nrOfHoursField = new TextField();
        titleField = new TextField();
        yearOfEmploymentField = new TextField();
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
                    HighSchoolTeacher hsTeacher = (HighSchoolTeacher) teacher;
                    writer.println("HighSchoolTeacher," + hsTeacher.name + "," + hsTeacher.experience +
                            "," + hsTeacher.grade + "," + hsTeacher.nrOfHours);
                } else if (teacher instanceof CollegeTeacher) {
                    CollegeTeacher cTeacher = (CollegeTeacher) teacher;
                    writer.println("CollegeTeacher," + cTeacher.name + "," + cTeacher.experience +
                            "," + cTeacher.title + "," + cTeacher.yearOfEmployment);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addTeacher() {
        if (highSchoolRadio.getState()) {
            String name = nameField.getText();
            int experience = Integer.parseInt(experienceField.getText());
            String grade = gradeField.getText();
            int nrOfHours = Integer.parseInt(nrOfHoursField.getText());
            HighSchoolTeacher teacher = new HighSchoolTeacher(name, experience, grade, nrOfHours);
            if (isDuplicate(teacher)) {
                showMessageDialog("A teacher with the same name and experience already exists.", "Duplicate Entry");
            } else {
                teachers.add(teacher);
                teacherList.add(teacher.toString());
                clearFields();
                addDialog.setVisible(false);
                saveTeachers();
            }
        } else if (collegeRadio.getState()) {
            String name = nameField.getText();
            int experience = Integer.parseInt(experienceField.getText());
            String title = titleField.getText();
            int yearOfEmployment = Integer.parseInt(yearOfEmploymentField.getText());
            CollegeTeacher teacher = new CollegeTeacher(name, experience, title, yearOfEmployment);
            if (isDuplicate(teacher)) {
                showMessageDialog("A teacher with the same name and experience already exists.", "Duplicate Entry");
            } else {
                teachers.add(teacher);
                teacherList.add(teacher.toString());
                clearFields();
                addDialog.setVisible(false);
                saveTeachers();
            }
        }
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
            Teacher teacher = teachers.get(selectedIndex);
            if (teacher instanceof HighSchoolTeacher) {
                HighSchoolTeacher hsTeacher = (HighSchoolTeacher) teacher;
                highSchoolRadio.setState(true);
                nameField.setText(hsTeacher.name);
                experienceField.setText(String.valueOf(hsTeacher.experience));
                gradeField.setText(hsTeacher.grade);
                nrOfHoursField.setText(String.valueOf(hsTeacher.nrOfHours));
            } else if (teacher instanceof CollegeTeacher) {
                CollegeTeacher cTeacher = (CollegeTeacher) teacher;
                collegeRadio.setState(true);
                nameField.setText(cTeacher.name);
                experienceField.setText(String.valueOf(cTeacher.experience));
                titleField.setText(cTeacher.title);
                yearOfEmploymentField.setText(String.valueOf(cTeacher.yearOfEmployment));
            }
            addDialog.setVisible(true);
        }
    }


    private void deleteTeacher() {
        int selectedIndex = teacherList.getSelectedIndex();
        if (selectedIndex != -1) {
            showConfirmationDialog("Are you sure you want to delete the selected teacher?", "Confirmation");
            saveTeachers();
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
        titleField.setText("");
        yearOfEmploymentField.setText("");
    }

    private void showMessageDialog(String message, String title) {
        Dialog dialog = new Dialog(this, title, true);
        dialog.setLayout(new FlowLayout());
        dialog.add(new Label(message));
        Button okButton = new Button("OK");
        okButton.addActionListener(e -> dialog.dispose());
        dialog.add(okButton);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == exitButton) {
            System.exit(0);
        }
        if (e.getSource() == addButton) {
            clearFields();
            addDialog.setVisible(true);
        } else if (e.getSource() == modifyButton) {
            modifyTeacher();
        } else if (e.getSource() == deleteButton) {
            deleteTeacher();
        } else if (e.getSource() == saveButton) {
            int selectedIndex = teacherList.getSelectedIndex();
            if (selectedIndex != -1) {
                // Overwrite the old teacher with the modified one
                if (highSchoolRadio.getState()) {
                    String name = nameField.getText();
                    int experience = Integer.parseInt(experienceField.getText());
                    String grade = gradeField.getText();
                    int nrOfHours = Integer.parseInt(nrOfHoursField.getText());
                    HighSchoolTeacher modifiedTeacher = new HighSchoolTeacher(name, experience, grade, nrOfHours);
                    teachers.set(selectedIndex, modifiedTeacher);
                    teacherList.replaceItem(modifiedTeacher.toString(), selectedIndex);
                } else if (collegeRadio.getState()) {
                    String name = nameField.getText();
                    int experience = Integer.parseInt(experienceField.getText());
                    String title = titleField.getText();
                    int yearOfEmployment = Integer.parseInt(yearOfEmploymentField.getText());
                    CollegeTeacher modifiedTeacher = new CollegeTeacher(name, experience, title, yearOfEmployment);
                    teachers.set(selectedIndex, modifiedTeacher);
                    teacherList.replaceItem(modifiedTeacher.toString(), selectedIndex);
                }

                addDialog.setVisible(false);
                saveTeachers();
            } else {
                // Perform the validation and adding of a new teacher
                if (highSchoolRadio.getState() || collegeRadio.getState()) {
                    if (nameField.getText().isEmpty()) {
                        showMessageDialog("Please enter a name.", "Validation Error");
                    } else if (experienceField.getText().isEmpty()) {
                        showMessageDialog("Please enter the years of experience.", "Validation Error");
                    } else {
                        try {
                            int experience = Integer.parseInt(experienceField.getText());
                            if (experience < 0) {
                                showMessageDialog("Years of experience cannot be negative.", "Validation Error");
                            } else {
                                if (highSchoolRadio.getState()) {
                                    if (gradeField.getText().isEmpty()) {
                                        showMessageDialog("Please enter a grade.", "Validation Error");
                                    } else if (nrOfHoursField.getText().isEmpty()) {
                                        showMessageDialog("Please enter the number of hours.", "Validation Error");
                                    } else {
                                        try {
                                            int nrOfHours = Integer.parseInt(nrOfHoursField.getText());
                                            if (nrOfHours < 0) {
                                                showMessageDialog("Number of hours cannot be negative.", "Validation Error");
                                            } else {
                                                addTeacher();
                                            }
                                        } catch (NumberFormatException ex) {
                                            showMessageDialog("Please enter a valid number for the number of hours.", "Validation Error");
                                        }
                                    }
                                } else if (collegeRadio.getState()) {
                                    if (titleField.getText().isEmpty()) {
                                        showMessageDialog("Please enter a title.", "Validation Error");
                                    } else if (yearOfEmploymentField.getText().isEmpty()) {
                                        showMessageDialog("Please enter the year of employment.", "Validation Error");
                                    } else {
                                        try {
                                            int yearOfEmployment = Integer.parseInt(yearOfEmploymentField.getText());
                                            if (yearOfEmployment < 0) {
                                                showMessageDialog("Year of employment cannot be negative.", "Validation Error");
                                            } else {
                                                addTeacher();
                                            }
                                        } catch (NumberFormatException ex) {
                                            showMessageDialog("Please enter a valid year for the year of employment.", "Validation Error");
                                        }
                                    }
                                }
                            }
                        } catch (NumberFormatException ex) {
                            showMessageDialog("Please enter a valid number for the years of experience.", "Validation Error");
                        }
                    }
                } else {
                    showMessageDialog("Please select a teacher type.", "Validation Error");
                }
            }
        } else if (e.getSource() == cancelButton) {
            addDialog.setVisible(false);
        }
    }


    public static void main(String[] args) {
        new TeacherManager().setVisible(true);
    }
}
