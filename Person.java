import java.io.Serializable;

public class Person implements Serializable, Comparable<Person> {

    private String firstName, lastName;
    private OCCCDate dob;

    public Person(String firstName, String lastName, OCCCDate dob) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.dob = dob;
    }

    public Person(Person p) {
        this(p.firstName, p.lastName, p.dob);
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public OCCCDate getDoB() {
        return dob;
    }

    @Override
    public String toString() {
        return lastName + ", " + firstName;
    }

    @Override
    public int compareTo(Person p) {
        String pName = p.lastName + p.firstName;
        String thisName = this.lastName + this.firstName;

        return thisName.compareToIgnoreCase(pName);
    }

    public boolean equals(Person p) {
        return p.firstName.equalsIgnoreCase(firstName) &&
                p.lastName.equalsIgnoreCase(lastName);
    }

    public void eat() {
        System.out.println(getClass().getName() + " is eating...");
    }

    public void sleep() {
        System.out.println(getClass().getName() + " is sleeping...");
    }

    public void play() {
        System.out.println(getClass().getName() + " is playing...");
    }

    public void run() {
        System.out.println(getClass().getName() + " is running...");
    }
}
