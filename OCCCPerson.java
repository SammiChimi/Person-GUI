public class OCCCPerson extends RegisteredPerson {
    private String studentID;

    public OCCCPerson(RegisteredPerson p, String studentID) {
        super(p);
        this.studentID = studentID;
    }

    public OCCCPerson(OCCCPerson p) {
        this(p, p.studentID);
    }

    public String getStudentID() {
        return studentID;
    }

    public boolean equals(OCCCPerson p) {
        return super.equals(p) && studentID.equals(p.studentID);
    }

    @Override
    public boolean equals(RegisteredPerson p) {
        return super.equals(p);
    }

    @Override
    public boolean equals(Person p) {
        return super.equals(p);
    }
}
