public class RegisteredPerson extends Person {
    private String govID;

    public RegisteredPerson(String firstName, String lastName, OCCCDate dob, String govID) {
        super(firstName, lastName, dob);
        this.govID = govID;
    }

    public RegisteredPerson(Person p, String govID) {
        this(p.getFirstName(), p.getLastName(), p.getDoB(), govID);
    }

    public RegisteredPerson(RegisteredPerson p) {
        this(p, p.govID);
    }

    public String getGovernmentID() {
        return govID;
    }

    public boolean equals(RegisteredPerson p) {
        return super.equals(p) && govID.equals(p.govID);
    }

    @Override
    public boolean equals(Person p) {
        return super.equals(p);
    }
}
