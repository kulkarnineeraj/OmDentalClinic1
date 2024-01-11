package com.Om.DentalClinic.model;


import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;


import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;


@Entity
@Table(name = "tbl_patient_info")
public class PatientInfo {
	
	@Id
	@Column(name = "patient_num")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int patientnumber;
			
	@Column(name = "fname")
	private String firstname;
	
	@Column(name = "mname")
	private String middlename;
	
	@Column(name = "lname")
	private String lastname;
	
	@Column(name = "age")
	private int patientage;
	
	@Column(name = "gender")
	private String patientgender;
	
	@Column(name = "reg_date")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date patientregdate;
	
	@Column(name = "mobile_one")
	private long patientmobile1;
	
	@Column(name = "mobile_two")
	private long patientmobile2;
	
	@Column(name = "medical_history")
	private String patientmedicalhistory;
	
	@Column(name = "info_cashier_name")
	private String cashiername;
	
    @Lob
    @Column(name = "reports")
    private byte[] patientReports;
    
//	@Column(name = "s3Objectkey")
//	private String reportlocation;
    
	@Column(name = "info_timestamp")
	private Date timestamp;
	 
    @PrePersist
    protected void onCreate() {
        timestamp = new Date();
    }
    
    @PreUpdate
    protected void onUpdate() {
        timestamp = new Date();
    }
    
    @OneToMany(mappedBy = "procedurenumber", cascade = CascadeType.ALL)
    private List<PatientProcedure> patientprocedure ;

	public PatientInfo() {
		super();
		// TODO Auto-generated constructor stub
	}

	public PatientInfo(int patientnumber, String firstname, String middlename, String lastname, int patientage,
			String patientgender, Date patientregdate, long patientmobile1, long patientmobile2,
			String patientmedicalhistory, String cashiername, byte[] patientReports, Date timestamp,
			List<PatientProcedure> patientprocedure) {
		super();
		this.patientnumber = patientnumber;
		this.firstname = firstname;
		this.middlename = middlename;
		this.lastname = lastname;
		this.patientage = patientage;
		this.patientgender = patientgender;
		this.patientregdate = patientregdate;
		this.patientmobile1 = patientmobile1;
		this.patientmobile2 = patientmobile2;
		this.patientmedicalhistory = patientmedicalhistory;
		this.cashiername = cashiername;
		this.patientReports = patientReports;
		this.timestamp = timestamp;
		this.patientprocedure = patientprocedure;
	}

	public int getPatientnumber() {
		return patientnumber;
	}

	public void setPatientnumber(int patientnumber) {
		this.patientnumber = patientnumber;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getMiddlename() {
		return middlename;
	}

	public void setMiddlename(String middlename) {
		this.middlename = middlename;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public int getPatientage() {
		return patientage;
	}

	public void setPatientage(int patientage) {
		this.patientage = patientage;
	}

	public String getPatientgender() {
		return patientgender;
	}

	public void setPatientgender(String patientgender) {
		this.patientgender = patientgender;
	}

	public Date getPatientregdate() {
		return patientregdate;
	}

	public void setPatientregdate(Date patientregdate) {
		this.patientregdate = patientregdate;
	}

	public long getPatientmobile1() {
		return patientmobile1;
	}

	public void setPatientmobile1(long patientmobile1) {
		this.patientmobile1 = patientmobile1;
	}

	public long getPatientmobile2() {
		return patientmobile2;
	}

	public void setPatientmobile2(long patientmobile2) {
		this.patientmobile2 = patientmobile2;
	}

	public String getPatientmedicalhistory() {
		return patientmedicalhistory;
	}

	public void setPatientmedicalhistory(String patientmedicalhistory) {
		this.patientmedicalhistory = patientmedicalhistory;
	}

	public String getCashiername() {
		return cashiername;
	}

	public void setCashiername(String cashiername) {
		this.cashiername = cashiername;
	}

	public byte[] getPatientReports() {
		return patientReports;
	}

	public void setPatientReports(byte[] patientReports) {
		this.patientReports = patientReports;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public List<PatientProcedure> getPatientprocedure() {
		return patientprocedure;
	}

	public void setPatientprocedure(List<PatientProcedure> patientprocedure) {
		this.patientprocedure = patientprocedure;
	}

	@Override
	public String toString() {
		return "PatientInfo [patientnumber=" + patientnumber + ", firstname=" + firstname + ", middlename=" + middlename
				+ ", lastname=" + lastname + ", patientage=" + patientage + ", patientgender=" + patientgender
				+ ", patientregdate=" + patientregdate + ", patientmobile1=" + patientmobile1 + ", patientmobile2="
				+ patientmobile2 + ", patientmedicalhistory=" + patientmedicalhistory + ", cashiername=" + cashiername
				+ ", patientReports=" + Arrays.toString(patientReports) + ", timestamp=" + timestamp
				+ ", patientprocedure=" + patientprocedure + ", getPatientnumber()=" + getPatientnumber()
				+ ", getFirstname()=" + getFirstname() + ", getMiddlename()=" + getMiddlename() + ", getLastname()="
				+ getLastname() + ", getPatientage()=" + getPatientage() + ", getPatientgender()=" + getPatientgender()
				+ ", getPatientregdate()=" + getPatientregdate() + ", getPatientmobile1()=" + getPatientmobile1()
				+ ", getPatientmobile2()=" + getPatientmobile2() + ", getPatientmedicalhistory()="
				+ getPatientmedicalhistory() + ", getCashiername()=" + getCashiername() + ", getPatientReports()="
				+ Arrays.toString(getPatientReports()) + ", getTimestamp()=" + getTimestamp()
				+ ", getPatientprocedure()=" + getPatientprocedure() + ", getClass()=" + getClass() + ", hashCode()="
				+ hashCode() + ", toString()=" + super.toString() + "]";
	}




}