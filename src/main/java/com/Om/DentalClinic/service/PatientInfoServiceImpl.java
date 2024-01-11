package com.Om.DentalClinic.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//import java.util.List;
import java.io.File;
import java.io.FileOutputStream;

import org.apache.el.stream.Optional;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.Om.DentalClinic.model.PatientInfo;
import com.Om.DentalClinic.model.PatientProcedure;
import com.Om.DentalClinic.model.Sittings;
import com.Om.DentalClinic.repository.PatientInfoRepository;
import com.Om.DentalClinic.repository.SittingRepository;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;

import jakarta.servlet.http.HttpServletResponse;


@Service
public class PatientInfoServiceImpl implements PatientInfoService {
	
	@Value("${applicaton.bucket.name}")
	private String bucketName;
	
	@Autowired
	private AmazonS3 s3Client;
	
	@Autowired
	private PatientInfoRepository patientInfoRepository;
	
	@Autowired
	private SittingRepository sittingsRepository;

	
	public List<PatientInfo> getAllPatientInfo() {
		return patientInfoRepository.findAll();
	}
	
	public  List<PatientInfo> findAllByOrderByPatientregdateDesc(){
		return patientInfoRepository.findAllByOrderByPatientregdateDesc();
	}
 
	public void savePatientInfo(MultipartFile patientReports, String firstname, String middlename, String lastname,
            int patientage, String patientgender, Date patientregdate, long patientmobile1,
            long patientmobile2, String patientmedicalhistory, String cashierName) throws IOException {

			// Create a new PatientInfo instance
			PatientInfo patientInfo = new PatientInfo();
			// Set other fields in the patientInfo object using the provided parameters
			patientInfo.setFirstname(firstname);
			patientInfo.setMiddlename(middlename);
			patientInfo.setLastname(lastname);
			patientInfo.setPatientage(patientage);
			patientInfo.setPatientgender(patientgender);
			patientInfo.setPatientregdate(patientregdate);
			patientInfo.setPatientmobile1(patientmobile1);
			patientInfo.setPatientmobile2(patientmobile2);
			patientInfo.setPatientmedicalhistory(patientmedicalhistory);
			// Set the info_cashier_name
			patientInfo.setCashiername(cashierName);
			
	        if (patientReports != null && !patientReports.isEmpty()) {
	            // Set the file content in the patientInfo object
	        	//patientInfo.setPatientReports(patientReports.getBytes());

	            	String timestamp = String.valueOf(System.currentTimeMillis());
	            	File fileObj = convertMultiPartFileToFile(patientReports);
	            	String filename = timestamp+"_"+patientReports.getOriginalFilename();
	            	patientInfo.setReportlocation(filename);
	            	s3Client.putObject(new PutObjectRequest(bucketName,filename,fileObj));
	        }

			patientInfoRepository.save(patientInfo);
		}

 
	private File convertMultiPartFileToFile(MultipartFile file) {
	    File convertedFile = new File(file.getOriginalFilename());
	    try (FileOutputStream fos = new FileOutputStream(convertedFile)) { // Correct typo
	        fos.write(file.getBytes());
	    } catch (IOException e) {
	    	System.err.println("Error converting multipartfile to file: " + e.getMessage());
	    }
	    return convertedFile;
	}
	

	public void downloadReportFromS3(String reportLocation, HttpServletResponse response) {
	    try {
	        // Check if the object exists in S3
	        boolean doesObjectExist = s3Client.doesObjectExist(bucketName, reportLocation);

	        if (!doesObjectExist) {
	            // Set appropriate response status and message
	            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
	            response.getWriter().println("File not found on S3.");
	            return; // Exit the method
	        }

	        // If the file exists, proceed with downloading
	        InputStream inputStream = s3Client.getObject(new GetObjectRequest(bucketName, reportLocation)).getObjectContent();

	        // Set the content type and headers for the response
	        response.setContentType("application/octet-stream");
	        response.setHeader("Content-Disposition", "attachment; filename=" + reportLocation);

	        // Copy the input stream to the response output stream
	        OutputStream outputStream = response.getOutputStream();
	        byte[] buffer = new byte[1024];
	        int bytesRead;
	        while ((bytesRead = inputStream.read(buffer)) != -1) {
	            outputStream.write(buffer, 0, bytesRead);
	        }
	        inputStream.close();
	        outputStream.close();
	    } catch (IOException e) {
	        // Handle other IOExceptions or log them as needed
	        System.err.println("Error downloading report from S3: " + e.getMessage());
	    }
	}


	public void deletePatientInfoById(int id) {
		this.patientInfoRepository.deleteById(id);
	}


	public PatientInfo getPatientInfoById(int id) {
		return patientInfoRepository.findById(id).orElse(null);
	}

	public void updatePatientInfo(
	        int patientNumber, // Add the patient number as a parameter to identify the patient to update
	        MultipartFile patientReports, 
	        String firstname,
	        String middlename,
	        String lastname,
	        int patientage,
	        String patientgender,
	        Date patientregdate,
	        long patientmobile1,
	        long patientmobile2,
	        String patientmedicalhistory,
	        String cashierName) throws IOException {

	    // Find the existing patient information by ID
	    PatientInfo existingPatientInfo = patientInfoRepository.findById(patientNumber)
	            .orElseThrow();

	    // Update the patient information fields
	    existingPatientInfo.setFirstname(firstname);
	    existingPatientInfo.setMiddlename(middlename);
	    existingPatientInfo.setLastname(lastname);
	    existingPatientInfo.setPatientage(patientage);
	    existingPatientInfo.setPatientgender(patientgender);
	    existingPatientInfo.setPatientregdate(patientregdate);
	    existingPatientInfo.setPatientmobile1(patientmobile1);
	    existingPatientInfo.setPatientmobile2(patientmobile2);
	    existingPatientInfo.setPatientmedicalhistory(patientmedicalhistory);
	    existingPatientInfo.setCashiername(cashierName);

	    // Update the patientReports if a new file is provided
	    if (patientReports != null && !patientReports.isEmpty()) {
	        existingPatientInfo.setPatientReports(patientReports.getBytes());
	    }

	    // Save the updated patient information
	    patientInfoRepository.save(existingPatientInfo);
	}
	
	
	
	
	
	
	@Override
	public ByteArrayOutputStream exportPatientsAndProceduresToExcel() throws IOException {
	    List<PatientInfo> patients = patientInfoRepository.findAll();

	    Workbook workbook = new XSSFWorkbook();
	    Sheet sheet = workbook.createSheet("Patients, Procedures, and Sittings");

	    Row headerRow = sheet.createRow(0);
	    // Add headers for PatientInfo
	    headerRow.createCell(0).setCellValue("Patient Number");
	    headerRow.createCell(1).setCellValue("First Name");
	    headerRow.createCell(2).setCellValue("Middle Name");
	    headerRow.createCell(3).setCellValue("Last Name");
	    headerRow.createCell(4).setCellValue("Age");
	    headerRow.createCell(5).setCellValue("Gender");
	    headerRow.createCell(6).setCellValue("Registration Date");
	    headerRow.createCell(7).setCellValue("Mobile 1");
	    headerRow.createCell(8).setCellValue("Mobile 2");
	    headerRow.createCell(9).setCellValue("Cashier Name");
	    // Add headers for PatientProcedure
	    headerRow.createCell(10).setCellValue("Procedure Date");
	    headerRow.createCell(11).setCellValue("Procedure Type");
	    headerRow.createCell(12).setCellValue("Procedure Detail");
	    headerRow.createCell(13).setCellValue("Estimate Amount");
	    headerRow.createCell(14).setCellValue("Cash Payment");
	    headerRow.createCell(15).setCellValue("Online Payment");
	    headerRow.createCell(16).setCellValue("Payment Amount");
	    headerRow.createCell(17).setCellValue("Balance Amount");
	    headerRow.createCell(18).setCellValue("Lab Name");
	    headerRow.createCell(19).setCellValue("External Doctor");
	    headerRow.createCell(20).setCellValue("Cashier Name");
	    // Add headers for Sittings
	    headerRow.createCell(21).setCellValue("Sitting Date");
	    headerRow.createCell(22).setCellValue("Sitting Details");
	    headerRow.createCell(23).setCellValue("Sitting Cash Payment");
	    headerRow.createCell(24).setCellValue("Sitting Online Payment");
	    headerRow.createCell(25).setCellValue("Sitting Payment Amount");
	    headerRow.createCell(26).setCellValue("Sitting Lab Name");
	    headerRow.createCell(27).setCellValue("Sitting External Doctor");
	    headerRow.createCell(28).setCellValue("Sitting Proc Cashier Name");

	    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

	    int rowNum = 1;
	    for (PatientInfo patient : patients) {
	        if (patient.getPatientprocedure().isEmpty()) {
	            // Add an empty row for patients with no procedures
	            Row dataRow = sheet.createRow(rowNum++);
	            addPatientInfoToRow(dataRow, patient, dateFormat);
	        } else {
	            for (PatientProcedure procedure : patient.getPatientprocedure()) {
	                if (procedure.getProceduresitting().isEmpty()) {
	                    // Add a row for patients with procedures but no sittings
	                    Row dataRow = sheet.createRow(rowNum++);
	                    addPatientProcedureToRow(dataRow, patient, procedure, dateFormat);
	                } else {
	                    for (Sittings sitting : procedure.getProceduresitting()) {
	                        // Add a row for patients with procedures and sittings
	                        Row dataRow = sheet.createRow(rowNum++);
	                        addPatientSittingToRow(dataRow, patient, procedure, sitting, dateFormat);
	                    }
	                }
	            }
	        }
	    }

	    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	    workbook.write(outputStream);
	    workbook.close();

	    return outputStream;
	}

	private void addPatientInfoToRow(Row dataRow, PatientInfo patient, SimpleDateFormat dateFormat) {
	    // Add data for PatientInfo
	    dataRow.createCell(0).setCellValue(patient.getPatientnumber());
	    dataRow.createCell(1).setCellValue(patient.getFirstname());
	    dataRow.createCell(2).setCellValue(patient.getMiddlename());
	    dataRow.createCell(3).setCellValue(patient.getLastname());
	    dataRow.createCell(4).setCellValue(patient.getPatientage());
	    dataRow.createCell(5).setCellValue(patient.getPatientgender());
	    dataRow.createCell(6).setCellValue(dateFormat.format(patient.getPatientregdate()));
	    dataRow.createCell(7).setCellValue(patient.getPatientmobile1());
	    dataRow.createCell(8).setCellValue(patient.getPatientmobile2());
	    dataRow.createCell(9).setCellValue(patient.getCashiername());
	}

	private void addPatientProcedureToRow(Row dataRow, PatientInfo patient, PatientProcedure procedure, SimpleDateFormat dateFormat) {
	    // Add data for PatientInfo
	    addPatientInfoToRow(dataRow, patient, dateFormat);
	    // Add data for PatientProcedure
	    dataRow.createCell(10).setCellValue(dateFormat.format(procedure.getProceduredate()));
	    dataRow.createCell(11).setCellValue(procedure.getProceduretype());
	    dataRow.createCell(12).setCellValue(procedure.getProceduredetail());
	    dataRow.createCell(13).setCellValue(procedure.getEstimateamount());
	    dataRow.createCell(14).setCellValue(procedure.getCashpayment() != null ? procedure.getCashpayment() : 0.0);
	    dataRow.createCell(15).setCellValue(procedure.getOnlinepayment() != null ? procedure.getOnlinepayment() : 0.0);
	    dataRow.createCell(16).setCellValue(procedure.getPaymentamount() != null ? procedure.getPaymentamount() : 0.0);
	    dataRow.createCell(17).setCellValue(procedure.getBalanceamount() != null ? procedure.getBalanceamount() : 0.0);
	    dataRow.createCell(18).setCellValue(procedure.getLabname());
	    dataRow.createCell(19).setCellValue(procedure.getExternaldoctor());
	    dataRow.createCell(20).setCellValue(procedure.getCashiername());
	}

	private void addPatientSittingToRow(Row dataRow, PatientInfo patient, PatientProcedure procedure, Sittings sitting, SimpleDateFormat dateFormat) {
	    // Add data for PatientProcedure
	    addPatientProcedureToRow(dataRow, patient, procedure, dateFormat);
	    // Add data for Sittings
	    dataRow.createCell(21).setCellValue(dateFormat.format(sitting.getSittingdate()));
	    dataRow.createCell(22).setCellValue(sitting.getSittingdetails());
	    dataRow.createCell(23).setCellValue(sitting.getSittingcashpayment());
	    dataRow.createCell(24).setCellValue(sitting.getSittingonlinepayment());
	    dataRow.createCell(25).setCellValue(sitting.getSittingpaymentamount());
	    dataRow.createCell(26).setCellValue(sitting.getSittinglabname());
	    dataRow.createCell(27).setCellValue(sitting.getSittingexternaldoctor());
	    dataRow.createCell(28).setCellValue(sitting.getSittingproccashiername());
	}

	
	
	
	
	@Override
    public byte[] getMedicalReportById(int patientId) throws IOException {
        java.util.Optional<PatientInfo> patientInfoOptional = patientInfoRepository.findById(patientId);

        if (patientInfoOptional.isPresent()) {
            // Assuming patientReports is a byte[] field in your PatientInfo entity
            byte[] medicalReportContent = patientInfoOptional.get().getPatientReports();

            // You may want to perform additional checks or processing here

            return medicalReportContent;
        } else {
            throw new IOException("Medical report not found for patient ID: " + patientId);
        }
    }

	

}
