public abstract class Teacher
{
    protected String name;
    protected int experience;


    public Teacher(String name, int experience) {
        this.name = name;
        this.experience = experience;
    }

    public abstract String toString();
}
