class CollegeTeacher extends Teacher {
    public String title;
    public int yearOfEmployment;

    public CollegeTeacher(String name, int experience, String title, int yearOfEmployment) {
        super(name, experience);
        this.title = title;
        this.yearOfEmployment = yearOfEmployment;
    }

    public String toString() {
        return "College Teacher: " + name + ", Experience: " + experience +
                ", Title: " + title + ", Year of Employment: " + yearOfEmployment;
    }
}