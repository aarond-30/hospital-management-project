package models;

public class LabReport {
    private int reportId;
    private int patientId;
    private String patientName;
    private int doctorId;
    private String doctorName;
    private String testName;
    private String testDate;
    private String testMetrics;
    private String diagnosisSummary;
    private String status;

    public LabReport(int reportId, int patientId, String patientName, int doctorId, String doctorName,
                     String testName, String testDate, String testMetrics, String diagnosisSummary, String status) {
        this.reportId = reportId;
        this.patientId = patientId;
        this.patientName = patientName;
        this.doctorId = doctorId;
        this.doctorName = doctorName;
        this.testName = testName;
        this.testDate = testDate;
        this.testMetrics = testMetrics;
        this.diagnosisSummary = diagnosisSummary;
        this.status = status;
    }

    public int getReportId() {
        return reportId;
    }

    public void setReportId(int reportId) {
        this.reportId = reportId;
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public int getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public String getTestDate() {
        return testDate;
    }

    public void setTestDate(String testDate) {
        this.testDate = testDate;
    }

    public String getTestMetrics() {
        return testMetrics;
    }

    public void setTestMetrics(String testMetrics) {
        this.testMetrics = testMetrics;
    }

    public String getDiagnosisSummary() {
        return diagnosisSummary;
    }

    public void setDiagnosisSummary(String diagnosisSummary) {
        this.diagnosisSummary = diagnosisSummary;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
