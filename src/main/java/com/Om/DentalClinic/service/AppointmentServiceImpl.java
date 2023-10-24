package com.Om.DentalClinic.service;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import com.Om.DentalClinic.model.Appointment;
import com.Om.DentalClinic.repository.AppointmentRepository;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;



@Service
public class AppointmentServiceImpl implements AppointmentService {

	@Autowired
	private AppointmentRepository appointmentRepository;

	@Override
	public void saveAppointment(Appointment appointment) {
	this.appointmentRepository.save(appointment);
	
	}
	
	
	public ByteArrayOutputStream exportAppointmentsToExcel() throws IOException {
	    List<Appointment> appointments = appointmentRepository.findAll();

	    Workbook workbook = new XSSFWorkbook();
	    Sheet sheet = workbook.createSheet("Appointments");

	    // Create header row
	    Row headerRow = sheet.createRow(0);
	    headerRow.createCell(0).setCellValue("Appointment Number");
	    headerRow.createCell(1).setCellValue("First Name");
	    headerRow.createCell(2).setCellValue("Middle Name");
	    headerRow.createCell(3).setCellValue("Last Name");
	    headerRow.createCell(4).setCellValue("Treatment");
	    headerRow.createCell(5).setCellValue("Start Time");
	    headerRow.createCell(6).setCellValue("End Time");
	    headerRow.createCell(7).setCellValue("Mobile Number");

	    int rowNum = 1;
	    for (Appointment appointment : appointments) {
	        Row dataRow = sheet.createRow(rowNum++);
	        dataRow.createCell(0).setCellValue(appointment.getAppointmentnum());
	        dataRow.createCell(1).setCellValue(appointment.getFirstname());
	        dataRow.createCell(2).setCellValue(appointment.getMiddlename());
	        dataRow.createCell(3).setCellValue(appointment.getLastname());
	        dataRow.createCell(4).setCellValue(appointment.getTreatment());
	        dataRow.createCell(5).setCellValue(appointment.getStarttime().toString());
	        dataRow.createCell(6).setCellValue(appointment.getEndtime().toString());
	        dataRow.createCell(7).setCellValue(appointment.getPatientmobile1());
	    }

	    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	    workbook.write(outputStream);
	    workbook.close();

	    return outputStream;
	}
	
	
//	public boolean isAppointmentExists(Date startTime, Date endTime) {
//        // Check if appointment exists for the given date and time
//        return appointmentRepository.existsByStarttimeBeforeAndEndtimeAfter(startTime, endTime);
//    }
	
	
	public List<Appointment> getAppointmentsByDate(Date date) {
        // Calculate start and end time for the given date
        Calendar startOfDay = Calendar.getInstance();
        startOfDay.setTime(date);
        startOfDay.set(Calendar.HOUR_OF_DAY, 0);
        startOfDay.set(Calendar.MINUTE, 0);
        startOfDay.set(Calendar.SECOND, 0);

        Calendar endOfDay = Calendar.getInstance();
        endOfDay.setTime(date);
        endOfDay.set(Calendar.HOUR_OF_DAY, 23);
        endOfDay.set(Calendar.MINUTE, 59);
        endOfDay.set(Calendar.SECOND, 59);

        // Fetch appointments between start and end time
        return appointmentRepository.findByStarttimeBetween(startOfDay.getTime(), endOfDay.getTime());
    }
	
	public void deleteAppointmentById(int id) {
		this.appointmentRepository.deleteById(id);
	}
	
}
