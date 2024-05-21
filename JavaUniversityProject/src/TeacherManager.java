package proiect;


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
        teacherList.setFont(new Font("Arial", Font.PLAIN, 14));
        for (Teacher teacher : teachers) {
            teacherList.add(teacher.toString());
        }
        add(teacherList, BorderLayout.CENTER);

        Panel buttonPanel = new Panel();
        addButton = new Button("Add");
        addButton.setFont(new Font("Arial", Font.PLAIN, 14));
        modifyButton = new Button("Modify");
        modifyButton.setFont(new Font("Arial", Font.PLAIN, 14));
        deleteButton = new Button("Delete");
        deleteButton.setFont(new Font("Arial", Font.PLAIN, 14));
        exitButton = new Button("Exit");
        exitButton.setFont(new Font("Arial", Font.PLAIN, 14));
        exitButton.setForeground(Color.RED);
        addButton.addActionListener(this);
        modifyButton.addActionListener(this);
        deleteButton.addActionListener(this);
        exitButton.addActionListener(this);
        buttonPanel.add(addButton);
        buttonPanel.add(modifyButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(exitButton);
        add(buttonPanel, BorderLayout.SOUTH);
        buttonPanel.setBackground(Color.LIGHT_GRAY);

        createAddDialog();

        setSize(800, 400);
        setLocationRelativeTo(null);
    }

    /**
     * Creates the dialog window for adding a new teacher.
     * Sets up the layout and components for entering teacher details.
     */
    private void createAddDialog() {
        // Create the dialog window
        addDialog = new Dialog(this, "Add Teacher", true);
        addDialog.setLayout(new BorderLayout());
        addDialog.setBackground(Color.LIGHT_GRAY);

        // Initialize input panel with a grid layout
        inputPanel = new Panel(new GridLayout(10, 2));

        // Create radio buttons for selecting teacher type
        radioGroup = new CheckboxGroup();
        highSchoolRadio = new Checkbox("High School Teacher", radioGroup, false);
        collegeRadio = new Checkbox("College Teacher", radioGroup, false);
        inputPanel.add(highSchoolRadio);
        inputPanel.add(collegeRadio);

        // Create input fields for teacher details
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

        // Create button panel for Save and Cancel buttons
        Panel buttonPanel = new Panel();
        saveButton = new Button("Save");
        saveButton.setFont(new Font("Arial", Font.PLAIN, 14));
        cancelButton = new Button("Cancel");
        cancelButton.setFont(new Font("Arial", Font.PLAIN, 14));
        saveButton.addActionListener(this);
        cancelButton.addActionListener(this);
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        addDialog.add(buttonPanel, BorderLayout.SOUTH);
        addDialog.setLocationRelativeTo(this);

        // Add item listeners to radio buttons
        highSchoolRadio.addItemListener(e -> updateTextFields());
        collegeRadio.addItemListener(e -> updateTextFields());

        // Adjust dialog size, update text fields, and pack components
        addDialog.pack();
        updateTextFields();
    }

    /**
     * Loads teachers from the "teachers.txt" file and populates the teachers list.
     */
    private void loadTeachers() {
        try (BufferedReader reader = new BufferedReader(new FileReader("teachers.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[0].equals("HighSchoolTeacher")) {
                    // Add HighSchoolTeacher to the teachers list
                    teachers.add(new HighSchoolTeacher(parts[1], Integer.parseInt(parts[2]),
                            parts[3], Integer.parseInt(parts[4])));
                } else if (parts[0].equals("CollegeTeacher")) {
                    // Add CollegeTeacher to the teachers list
                    teachers.add(new CollegeTeacher(parts[1], Integer.parseInt(parts[2]),
                            parts[3], Integer.parseInt(parts[4])));
                }
            }
        } catch (IOException e) {
            // Print the stack trace if an IOException occurs
            e.printStackTrace();
        }
    }

    /**
     * Saves the list of teachers to a file named "teachers.txt".
     * Each teacher's information is formatted and written to the file.
     * HighSchoolTeacher information is saved with name, experience, grade, and number of hours.
     * CollegeTeacher information is saved with name, experience, title, and year of employment.
     */
    private void saveTeachers() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("teachers.txt"))) {
            for (Teacher teacher : teachers) {
                if (teacher instanceof HighSchoolTeacher) {
                    HighSchoolTeacher hsTeacher = (HighSchoolTeacher) teacher;
                    // Write HighSchoolTeacher information to the file
                    writer.println("HighSchoolTeacher," + hsTeacher.name + "," + hsTeacher.experience +
                            "," + hsTeacher.grade + "," + hsTeacher.nrOfHours);
                } else if (teacher instanceof CollegeTeacher) {
                    CollegeTeacher cTeacher = (CollegeTeacher) teacher;
                    // Write CollegeTeacher information to the file
                    writer.println("CollegeTeacher," + cTeacher.name + "," + cTeacher.experience +
                            "," + cTeacher.title + "," + cTeacher.yearOfEmployment);
                }
            }
        } catch (IOException e) {
            // Print the stack trace if an IOException occurs
            e.printStackTrace();
        }
    }

    /**
     * Adds a new teacher based on the selected radio button (high school or college).
     * If a teacher with the same name already exists, it shows an error message.
     */
    private void addTeacher() {
        // Check if the high school radio button is selected
        if (highSchoolRadio.getState()) {
            // Check for duplicate name
            if (isDuplicate(nameField.getText())) {
                showMessageDialog("Teacher with the same name already exists.", "Duplicate Entry");
                return;
            }
            // Get input values
            String name = nameField.getText();
            int experience = textFieldValiation(experienceField.getText(), "Years of experience");
            if (experience == -1) return;
            String grade = gradeField.getText();
            int nrOfHours = textFieldValiation(nrOfHoursField.getText(), "number of hours");
            if (nrOfHours == -1) return;

            // Create a HighSchoolTeacher object and add to lists
            HighSchoolTeacher teacher = new HighSchoolTeacher(name, experience, grade, nrOfHours);
            teachers.add(teacher);
            teacherList.add(teacher.toString());
        }
        // Check if the college radio button is selected
        else if (collegeRadio.getState()) {
            // Check for duplicate name
            if (isDuplicate(nameField.getText())) {
                showMessageDialog("Teacher with the same name already exists.", "Duplicate Entry");
                return;
            }
            // Get input values
            String name = nameField.getText();
            int experience = textFieldValiation(experienceField.getText(), "Years of experience");
            if (experience == -1) return;
            String title = titleField.getText();
            int yearOfEmployment = textFieldValiation(yearOfEmploymentField.getText(), "Year of employment");
            if (yearOfEmployment == -1) return;

            // Create a CollegeTeacher object and add to lists
            CollegeTeacher teacher = new CollegeTeacher(name, experience, title, yearOfEmployment);
            teachers.add(teacher);
            teacherList.add(teacher.toString());
        }

        // Clear input fields
        clearFields();

        // Hide the add dialog
        addDialog.setVisible(false);

        // Save the updated list of teachers
        saveTeachers();
    }

    /**
     * Checks if there is a duplicate teacher based on the given name.
     *
     * @param name The name to check for duplicates.
     * @return True if a teacher with the same name exists, false otherwise.
     */
    private boolean isDuplicate(String name) {
        for (Teacher teacher : teachers) {
            if (teacher.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Parses the given field value to an integer based on the specified field name.
     *
     * @param fieldValue The value to parse.
     * @param fieldName  The name of the field for validation message.
     * @return The parsed integer value if valid, -1 otherwise.
     */
        private int textFieldValiation(String fieldValue, String fieldName) {
            // Check if the field value is empty
            if (fieldValue.isEmpty()) {
                showMessageDialog("Please enter " + fieldName + ".", "Validation Error");
                return -1;
            }
    
            try {
                int value = Integer.parseInt(fieldValue);
    
                // Check for negative value
                if (value < 0) {
                    showMessageDialog(fieldName + " cannot be negative.", "Validation Error");
                    return -1;
                }
    
                // Validate based on field name
                if (fieldName.equals("number of hours") && value > 40) {
                    showMessageDialog("Number of hours cannot exceed 40.", "Validation Error");
                    return -1;
                }
    
                if (fieldName.equals("years of experience") && value > 80) {
                    showMessageDialog("Years of experience cannot exceed 80.", "Validation Error");
                    return -1;
                }
    
                if (fieldName.equals("year of employment") && value < 1900) {
                    showMessageDialog("Year of employment cannot be earlier than 1900.", "Validation Error");
                    return -1;
                }
    
                if (fieldName.equals("year of employment") && value > 2025) {
                    showMessageDialog("Year of employment cannot be later than 2025.", "Validation Error");
                    return -1;
                }
    
                return value;
            } catch (NumberFormatException ex) {
                showMessageDialog("Please enter a valid number for " + fieldName + ".", "Validation Error");
                return -1;
            }
        }

        /**
         * Modifies the selected teacher based on the type of teacher.
         * Updates the UI components with the teacher's information.
         */
        private void modifyTeacher() {
            // Get the index of the selected teacher in the list
            int selectedIndex = teacherList.getSelectedIndex();
    
            // Check if a teacher is selected
            if (selectedIndex != -1) {
                // Retrieve the selected teacher object
                Teacher teacher = teachers.get(selectedIndex);
    
                // Update the UI components based on the type of teacher
                if (teacher instanceof HighSchoolTeacher) {
                    // Set radio buttons for high school teacher
                    highSchoolRadio.setState(true);
                    collegeRadio.setState(false);
                    highSchoolRadio.setVisible(false);
                    collegeRadio.setVisible(false);
    
    
                    // Cast to HighSchoolTeacher and update fields
                    HighSchoolTeacher hsTeacher = (HighSchoolTeacher) teacher;
                    nameField.setText(hsTeacher.name);
                    experienceField.setText(String.valueOf(hsTeacher.experience));
                    gradeField.setText(hsTeacher.grade);
                    nrOfHoursField.setText(String.valueOf(hsTeacher.nrOfHours));
                    gradeField.setEnabled(true);
                    nrOfHoursField.setEnabled(true);
                    titleField.setText("");
                    yearOfEmploymentField.setText("");
                    titleField.setEnabled(false);
                    yearOfEmploymentField.setEnabled(false);
                } else if (teacher instanceof CollegeTeacher) {
                    // Set radio buttons for college teacher
                    collegeRadio.setState(true);
                    highSchoolRadio.setState(false);
                    collegeRadio.setVisible(false);
                    highSchoolRadio.setVisible(false);
    
                    // Cast to CollegeTeacher and update fields
                    CollegeTeacher cTeacher = (CollegeTeacher) teacher;
                    nameField.setText(cTeacher.name);
                    experienceField.setText(String.valueOf(cTeacher.experience));
                    titleField.setText(cTeacher.title);
                    yearOfEmploymentField.setText(String.valueOf(cTeacher.yearOfEmployment));
                    titleField.setEnabled(true);
                    yearOfEmploymentField.setEnabled(true);
                    gradeField.setText("");
                    nrOfHoursField.setText("");
                    gradeField.setEnabled(false);
                    nrOfHoursField.setEnabled(false);
                }
    
                // Set dialog title and make it visible
                addDialog.setTitle("Modify Teacher");
                addDialog.setVisible(true);
            }
        }
    
    
        /**
         * Deletes the selected teacher from the teacher list.
         */
        private void deleteTeacher() {
            // Get the index of the selected teacher
            int selectedIndex = teacherList.getSelectedIndex();
    
            // Check if a teacher is selected
            if (selectedIndex != -1) {
                // Show confirmation dialog before deleting
                showConfirmationDialog("Are you sure you want to delete the selected teacher?", "Confirmation");
    
                // Save the teachers after deletion
                saveTeachers();
            }
        }
    
        /**
         * Updates the text fields based on the selected radio button.
         */
        private void updateTextFields() {
            // If High School Teacher radio button is selected
            if (highSchoolRadio.getState()) {
                // Enable grade and number of hours fields
                gradeField.setEnabled(true);
                nrOfHoursField.setEnabled(true);
                // Disable title and year of employment fields
                titleField.setEnabled(false);
                yearOfEmploymentField.setEnabled(false);
            } else if (collegeRadio.getState()) {
                // If College Teacher radio button is selected
                // Disable grade and number of hours fields
                gradeField.setEnabled(false);
                nrOfHoursField.setEnabled(false);
                // Enable title and year of employment fields
                titleField.setEnabled(true);
                yearOfEmploymentField.setEnabled(true);
            }
        }
    
        /**
         * Displays a confirmation dialog with the given message and title.
         *
         * @param message the message to display
         * @param title the title of the dialog
         */
        private void showConfirmationDialog(String message, String title) {
            // Create a new dialog window with the specified title and set it to be modal
            Dialog dialog = new Dialog(this, title, true);
    
            // Set the layout of the dialog window to a border layout
            dialog.setLayout(new BorderLayout());
    
            // Add a label with the message to the center of the dialog window
            dialog.add(new Label(message), BorderLayout.CENTER);
    
            // Create buttons for 'Yes' and 'No'
            Button yesButton = new Button("Yes");
            Button noButton = new Button("No");
    
            // Action listener for the 'Yes' button
            yesButton.addActionListener(e -> {
                // Get the selected index from the teacherList
                int selectedIndex = teacherList.getSelectedIndex();
                if (selectedIndex != -1) {
                    // Remove the teacher and corresponding entry from the list
                    teachers.remove(selectedIndex);
                    teacherList.remove(selectedIndex);
                }
                // Close the dialog
                dialog.dispose();
            });
    
            // Action listener for the 'No' button
            noButton.addActionListener(e -> dialog.dispose());
    
            // Create a panel for the buttons with a flow layout
            Panel buttonPanel = new Panel(new FlowLayout());
            buttonPanel.add(yesButton);
            buttonPanel.add(noButton);
    
            // Add the button panel to the bottom of the dialog window
            dialog.add(buttonPanel, BorderLayout.SOUTH);
    
            // Pack the components within the dialog window
            dialog.pack();
    
            // Set the location of the dialog window relative to 'this' component
            dialog.setLocationRelativeTo(this);
    
            // Make the dialog window visible
            dialog.setVisible(true);
        }
    
        /**
         * Clears all the input fields in the addDialog.
         */
        private void clearFields() {
            // Clear the nameField
            nameField.setText("");
    
            // Clear the experienceField
            experienceField.setText("");
    
            // Clear the gradeField
            gradeField.setText("");
    
            // Clear the nrOfHoursField
            nrOfHoursField.setText("");
    
            // Clear the titleField
            titleField.setText("");
    
            // Clear the yearOfEmploymentField
            yearOfEmploymentField.setText("");
        }
    
        /**
         * Displays a message dialog with the given message and title.
         *
         * @param message the message to display
         * @param title the title of the dialog
         */
        private void showMessageDialog(String message, String title) {
            // Create a new dialog window with the specified title and set it to be modal
            Dialog dialog = new Dialog(this, title, true);
    
            // Set the layout of the dialog window to a flow layout
            dialog.setLayout(new FlowLayout());
    
            // Add a label with the message to the dialog window
            dialog.add(new Label(message));
    
            // Create an OK button and set its action to dispose of the dialog window when clicked
            Button okButton = new Button("OK");
            okButton.addActionListener(e -> dialog.dispose());
    
            // Add the OK button to the dialog window
            dialog.add(okButton);
    
            // Pack the components within the dialog window
            dialog.pack();
    
            // Set the location of the dialog window relative to 'this' component
            dialog.setLocationRelativeTo(this);
    
            // Make the dialog window visible
            dialog.setVisible(true);
        }
        /**
         * This method handles the actions performed by various buttons in the teacher management system.
         *
         * @param e The ActionEvent generated by the button action.
         */
        public void actionPerformed(ActionEvent e) {
            // Check if the source of the event is the exitButton
            if (e.getSource() == exitButton) {
                // If the exitButton is clicked, exit the application
                System.exit(0);
            }
    
            // Check if the source of the event is the addButton
            if (e.getSource() == addButton) {
            // Clear the input fields
            clearFields();
            // Reset the radio buttons
            addDialog.setTitle("Add Teacher");
            highSchoolRadio.setState(false);
            collegeRadio.setState(false);
            highSchoolRadio.setVisible(true);
            collegeRadio.setVisible(true);

            // Show the add teacher dialog
            addDialog.setVisible(true);

} 
            // Check if the source of the event is the modifyButton
            else if (e.getSource() == modifyButton) {
                // If the modifyButton is clicked, call the method to modify a teacher
                modifyTeacher();
            }
            // Check if the source of the event is the deleteButton
            else if (e.getSource() == deleteButton) {
                // If the deleteButton is clicked, call the method to delete a teacher
                deleteTeacher();
            }
            // Check if the source of the event is the saveButton
            else if (e.getSource() == saveButton) {
                // Get the index of the selected teacher in the teacherList
                int selectedIndex = teacherList.getSelectedIndex();
    
                // Check if a teacher is selected (selectedIndex != -1)
                if (selectedIndex != -1) {
                    // Check if the highSchoolRadio is selected
                    if (highSchoolRadio.getState()) {
                        // Validate the name field
                        if (nameField.getText().trim().isEmpty()) {
                            // If the name field is empty, show an error message and return
                            showMessageDialog("Please enter a name.", "Validation Error");
                            return;
                        }
                        // Get the trimmed value of the name field
                        String name = nameField.getText().trim();
    
                        // Parse the experience field to an integer
                        int experience = textFieldValiation(experienceField.getText(), "years of experience");
                        // If parsing fails (returns -1), return
                        if (experience == -1) return;
    
                        // Validate the grade field
                        if (gradeField.getText().trim().isEmpty()) {
                            // If the grade field is empty, show an error message and return
                            showMessageDialog("Please enter a grade.", "Validation Error");
                            return;
                        }
                        // Get the trimmed value of the grade field
                        String grade = gradeField.getText().trim();
    
                        // Parse the number of hours field to an integer
                        int nrOfHours = textFieldValiation(nrOfHoursField.getText(), "number of hours");
                        // If parsing fails (returns -1), return
                        if (nrOfHours == -1) return;
    
                        // Create a new HighSchoolTeacher object with the entered values
                        HighSchoolTeacher modifiedTeacher = new HighSchoolTeacher(name, experience, grade, nrOfHours);
                        // Replace the teacher at the selected index in the teachers list with the modified teacher
                        teachers.set(selectedIndex, modifiedTeacher);
                        // Replace the item in the teacherList at the selected index with the modified teacher's string representation
                        teacherList.replaceItem(modifiedTeacher.toString(), selectedIndex);
                    }
                    // Check if the collegeRadio is selected
                    else if (collegeRadio.getState()) {
                        // Validate the name field
                        if (nameField.getText().trim().isEmpty()) {
                            // If the name field is empty, show an error message and return
                            showMessageDialog("Please enter a name.", "Validation Error");
                            return;
                        }
                        // Get the trimmed value of the name field
                        String name = nameField.getText().trim();
    
                        // Parse the experience field to an integer
                        int experience = textFieldValiation(experienceField.getText(), "years of experience");
                        // If parsing fails (returns -1), return
                        if (experience == -1) return;
    
                        // Validate the title field
                        if (titleField.getText().trim().isEmpty()) {
                            // If the title field is empty, show an error message and return
                            showMessageDialog("Please enter a title.", "Validation Error");
                            return;
                        }
                        // Get the trimmed value of the title field
                        String title = titleField.getText().trim();
    
    
                        // Parse the year of employment field to an integer
                        int yearOfEmployment = textFieldValiation(yearOfEmploymentField.getText(), "year of employment");
                        // If parsing fails (returns -1), return
                        if (yearOfEmployment == -1) return;
    
                        // Create a new CollegeTeacher object with the entered values
                        CollegeTeacher modifiedTeacher = new CollegeTeacher(name, experience, title, yearOfEmployment);
                        // Replace the teacher at the selected index in the teachers list with the modified teacher
                        teachers.set(selectedIndex, modifiedTeacher);
                        // Replace the item in the teacherList at the selected index with the modified teacher's string representation
                        teacherList.replaceItem(modifiedTeacher.toString(), selectedIndex);
                    }
    
                    // Hide the add teacher dialog
                    addDialog.setVisible(false);
                    // Save the updated teacher list
                    saveTeachers();
                }
                // If no teacher is selected (adding a new teacher)
                else {
                    // Check if either the highSchoolRadio or collegeRadio is selected
                    if (highSchoolRadio.getState() || collegeRadio.getState()) {
                        // Validate the name field
                        if (nameField.getText().trim().isEmpty()) {
                            // If the name field is empty, show an error message and return
                            showMessageDialog("Please enter a name.", "Validation Error");
                            return;
                        }
    
                        // Parse the experience field to an integer
                        int experience = textFieldValiation(experienceField.getText(), "years of experience");
                        // If parsing fails (returns -1), return
                        if (experience == -1) return;
    
                        // Check for duplicate teacher names
                        if (isDuplicate(nameField.getText().trim())) {
                            // If a duplicate name exists, show an error message and return
                            showMessageDialog("Teacher with the same name already exists.", "Duplicate Entry");
                            return;
                        }
    
                        // Check if the highSchoolRadio is selected
                        if (highSchoolRadio.getState()) {
                            // Validate the grade field
                            if (gradeField.getText().trim().isEmpty()) {
                                // If the grade field is empty, show an error message and return
                                showMessageDialog("Please enter a grade.", "Validation Error");
                                return;
                            }
    
                            // Parse the number of hours field to an integer
                            int nrOfHours = textFieldValiation(nrOfHoursField.getText(), "number of hours");
                            // If parsing fails (returns -1), return
                            if (nrOfHours == -1) return;
    
                            // Call the method to add a new high school teacher
                            addTeacher();
                        }
                        // Check if the collegeRadio is selected
                        else if (collegeRadio.getState()) {
                            // Validate the title field
                            if (titleField.getText().trim().isEmpty()) {
                                // If the title field is empty, show an error message and return
                                showMessageDialog("Please enter a title.", "Validation Error");
                                return;
                            }
    
                            // Parse the year of employment field to an integer
                            int yearOfEmployment = textFieldValiation(yearOfEmploymentField.getText(), "year of employment");
                            // If parsing fails (returns -1), return
                            if (yearOfEmployment == -1) return;
    
                            // Call the method to add a new college teacher
                            addTeacher();
                        }
                    }
                    // If neither the highSchoolRadio nor collegeRadio is selected
                    else {
                        // Show an error message to select a teacher type
                        showMessageDialog("Please select a teacher type.", "Validation Error");
                    }
                }
            }
            // Check if the source of the event is the cancelButton
            else if (e.getSource() == cancelButton) {
                // If the cancelButton is clicked, hide the add teacher dialog
                addDialog.setVisible(false);
            }
        }
        public static void main(String[] args) {
            new TeacherManager().setVisible(true);
        }
    }
