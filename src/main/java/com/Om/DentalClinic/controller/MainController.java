package com.Om.DentalClinic.controller;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.Om.DentalClinic.model.PatientInfo;
import com.Om.DentalClinic.service.PatientInfoService;


@RestController
public class MainController {

	@Autowired
	private PatientInfoService patientInfoService;
	
	
	@GetMapping("/listPatientInfo")
	public List<PatientInfo> getAllPatientInfo() {		
		return  this.patientInfoService.getAllPatientInfo();
	}	

	@PostMapping("/SavePatientInfo")
	public String savePatientInfo(@RequestParam("file") MultipartFile file,
	@RequestParam("patientnumber") String patientnumber,
	@RequestParam("patientname") String patientname,
	@RequestParam("patientage") int patientage,
	@RequestParam("patientgender") String patientgender,
	@RequestParam("patientregdate") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX") Date patientregdate,
	@RequestParam("patientmobile") Long patientmobile,
	@RequestParam("patientmedicalhistory") String patientmedicalhistory) throws IOException
	{
		return patientInfoService.savePatientInfo(file,patientnumber,patientname,patientage,patientgender,patientregdate,patientmobile,patientmedicalhistory);
	}

	
}
