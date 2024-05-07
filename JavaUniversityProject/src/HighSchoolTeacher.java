public class HighSchoolTeacher extends Teacher {
    public String grade;
    public int nrOfHours;

    public HighSchoolTeacher(String name, int experience, String grade, int nrOfHours) {
        super(name, experience);
        this.grade = grade;
        this.nrOfHours = nrOfHours;
    }

    public String toString() {
        return "High School Teacher: " + name + ", Experience: " + experience +
                ", Grade: " + grade + ", Hours: " + nrOfHours;
    }
}