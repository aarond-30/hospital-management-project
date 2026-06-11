package models;

public class Bill {
    private int billId;
    private int patientId;
    private String patientName;
    private int doctorId;
    private String doctorName;
    private double consultationFee;
    private double roomCharges;
    private double medicineCharges;
    private double otherCharges;
    private double totalAmount;
    private String billingDate;

    public Bill() {}

    public Bill(int billId, int patientId, String patientName, int doctorId, String doctorName,
                double consultationFee, double roomCharges, double medicineCharges, double otherCharges,
                double totalAmount, String billingDate) {
        this.billId = billId;
        this.patientId = patientId;
        this.patientName = patientName;
        this.doctorId = doctorId;
        this.doctorName = doctorName;
        this.consultationFee = consultationFee;
        this.roomCharges = roomCharges;
        this.medicineCharges = medicineCharges;
        this.otherCharges = otherCharges;
        this.totalAmount = totalAmount;
        this.billingDate = billingDate;
    }

    public int getBillId() { return billId; }
    public void setBillId(int billId) { this.billId = billId; }

    public int getPatientId() { return patientId; }
    public void setPatientId(int patientId) { this.patientId = patientId; }

    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }

    public int getDoctorId() { return doctorId; }
    public void setDoctorId(int doctorId) { this.doctorId = doctorId; }

    public String getDoctorName() { return doctorName; }
    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }

    public double getConsultationFee() { return consultationFee; }
    public void setConsultationFee(double consultationFee) { this.consultationFee = consultationFee; }

    public double getRoomCharges() { return roomCharges; }
    public void setRoomCharges(double roomCharges) { this.roomCharges = roomCharges; }

    public double getMedicineCharges() { return medicineCharges; }
    public void setMedicineCharges(double medicineCharges) { this.medicineCharges = medicineCharges; }

    public double getOtherCharges() { return otherCharges; }
    public void setOtherCharges(double otherCharges) { this.otherCharges = otherCharges; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public String getBillingDate() { return billingDate; }
    public void setBillingDate(String billingDate) { this.billingDate = billingDate; }
}
