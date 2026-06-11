package models;

public class Patient {
    private int patientId;
    private String name;
    private int age;
    private String gender;
    private String phone;
    private String disease;
    private String address;
    private String visited; // YES or NO

    public Patient() {}

    public Patient(int patientId, String name, int age, String gender, String phone, String disease, String address, String visited) {
        this.patientId = patientId;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.phone = phone;
        this.disease = disease;
        this.address = address;
        this.visited = visited;
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDisease() {
        return disease;
    }

    public void setDisease(String disease) {
        this.disease = disease;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getVisited() {
        return visited;
    }

    public void setVisited(String visited) {
        this.visited = visited;
    }

    public String getConsultationStatus() {
        return (visited != null && visited.equalsIgnoreCase("YES")) ? "Consulted" : "Not Consulted";
    }

    public String getDoctorVisited() {
        return (visited != null && visited.equalsIgnoreCase("YES")) ? "YES" : "NO";
    }
}
