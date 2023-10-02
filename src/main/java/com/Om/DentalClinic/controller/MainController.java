package com.Om.DentalClinic.controller;

import java.io.IOException;

import java.security.Principal;

import java.util.ArrayList;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import com.Om.DentalClinic.model.PatientInfo;
import com.Om.DentalClinic.model.PatientProcedure;
import com.Om.DentalClinic.model.User;
import com.Om.DentalClinic.repository.UserRepository;
import com.Om.DentalClinic.service.PatientInfoService;
import com.Om.DentalClinic.service.PatientProcedureService;
import com.Om.DentalClinic.service.PatientProcedureServiceImpl;
import com.Om.DentalClinic.service.UserServiceImpl;


import jakarta.servlet.http.HttpSession;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;




@Controller
public class MainController {

	@Autowired
	private PatientInfoService patientInfoService;
	
	@Autowired
	private PatientProcedureService patientProcedureService;
	
	@Autowired
	private PatientProcedureServiceImpl patientProcedureServiceImpl;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private UserServiceImpl userServiceImpl;
	
	
	public MainController(UserServiceImpl userServiceImpl) {
	this.userServiceImpl=userServiceImpl;
	}
	

	 @GetMapping("/login")
	 public String showLogin() {
		 return "login";
	 }

	 @GetMapping("/adminHome")
	 public String adminHome()
	 {
		 return "adminHome";
	 }

	 
	 @GetMapping("/userhome")
	 public String userHome(Model model) {
	        
	     return "home"; 
	  }
	 

	
	 @GetMapping("/")
	 public String showLogin(Model model, HttpSession session) {
			@SuppressWarnings("unchecked")
			List<String> messages = (List<String>) session.getAttribute("MY_SESSION_MESSAGES");

			if (messages == null) {
				messages = new ArrayList<>();
			}
			model.addAttribute("sessionMessages", messages);

			return "login";

		}
	 
	 @PostMapping("/login")
	    public String login(@RequestParam String username, @RequestParam String password, Model model, HttpServletRequest request, HttpServletResponse response) throws IOException { // Declare IOException
	        User user = userServiceImpl.findByUsername(username);

	        if (user != null && user.getPassword().equals(password)) {
	            if ("ADMIN".equals(user.getRole())) {
	                return "adminHome";
	            } else if ("RECEP1".equals(user.getRole())) {
	                return "home";
	            }
	        }

	        if (user == null) {
	            System.out.println("Invalid Details");
	        } 
	        
	        else {
	            HttpSession session = request.getSession();
	            session.setAttribute("currentUser", user);
	            response.sendRedirect("adminHome.html");
	             
	            
	        } 

	        model.addAttribute("error", "Invalid username or password.");
	        return "login";
	    }
	 
	 @GetMapping("/logout")
	    public String logout() {
	        // Add logout logic here
	        return "redirect:/";
	    }
		
	 


// Santosh's Controller for PatientInfo------------------------------------------------------------------------------	 
 
	 @GetMapping("/patientinfo")
	 public String showPatientinfo(Model model) {
	     PatientInfo patientinfo = new PatientInfo(); 
	     model.addAttribute("patientinfo", patientinfo);	     
	     return "patientinfo";
	 } 
	 
		@GetMapping("/patientList")
		public String showPatientList(Model model,Principal principal) {
		   model.addAttribute("listpatients", patientInfoService.getAllPatientInfo());
			return "patientList";
		}
		
		
		@GetMapping("/deletePatientInfo/{id}")
		public String deletePatientInfo(@PathVariable(value = "id") int id) {
			this.patientInfoService.deletePatientInfoById(id);
			return "redirect:/patientList";
		}
		
		@GetMapping("/editPatientinfo/{id}")
		public String editPatientInfoForm(@PathVariable("id") int id, Model model) {
			PatientInfo patientinfo = patientInfoService.getPatientInfoById(id);
			model.addAttribute("patientinfo", patientinfo);
			return "editPatientInfo";
		}
		
		
		@PostMapping("/updatePatientInfo")
		public String updatePatientInfo(@RequestParam("patientReports") MultipartFile patientReports,
		@RequestParam("firstname") String firstname,
		@RequestParam("middlename") String middlename,
		@RequestParam("lastname") String lastname,
		@RequestParam("patientage") int patientage,
		@RequestParam("patientgender") String patientgender,
		@RequestParam("patientregdate")@DateTimeFormat(pattern = "yyyy-MM-dd") Date patientregdate,
		@RequestParam("patientmobile1") long patientmobile1,
		@RequestParam("patientmobile2") long patientmobile2,
		@RequestParam("patientmedicalhistory") String patientmedicalhistory) throws IOException
		{
		    if (patientReports.isEmpty()) {
		        return "redirect:/patientinfo?fileError=1";
		    }
			 patientInfoService.savePatientInfo(patientReports,firstname,middlename,lastname,patientage,patientgender,patientregdate,patientmobile1,patientmobile2,patientmedicalhistory);
			
			 return"redirect:/patientList";
		}
	
		 
		@PostMapping("/SavePatientInfo")
		public String savePatientInfo(@RequestParam("patientReports") MultipartFile patientReports,
		@RequestParam("firstname") String firstname,
		@RequestParam("middlename") String middlename,
		@RequestParam("lastname") String lastname,
		@RequestParam("patientage") int patientage,
		@RequestParam("patientgender") String patientgender,
		@RequestParam("patientregdate")@DateTimeFormat(pattern = "yyyy-MM-dd") Date patientregdate,
		@RequestParam("patientmobile1") long patientmobile1,
		@RequestParam("patientmobile2") long patientmobile2,
		@RequestParam("patientmedicalhistory") String patientmedicalhistory) throws IOException
		{
		    if (patientReports.isEmpty()) {
		        return "redirect:/patientinfo?fileError=1"; // Redirect with an error code
		    }
			 patientInfoService.savePatientInfo(patientReports,firstname,middlename,lastname,patientage,patientgender,patientregdate,patientmobile1,patientmobile2,patientmedicalhistory);
			
			 return"redirect:/patientList";
		}
	
	
//PatientInfo Code Ends here----------------------------------------------------------------------------------------------
	

//PatientProcedure controller ------------------------------------------------------------------------------	
	
		
		 @GetMapping("/patientDetails/{patientId}")
		 public String showPatientDetail(@PathVariable("patientId") int patientId,Model model) {
			 List<PatientProcedure> patientProcedures = patientProcedureService.getProceduresByPatientId(patientId);
			 model.addAttribute("patientProcedures", patientProcedures);
			 return "patientDetails";
		 }
		
		
		   @GetMapping("/procedureDetails/{patientId}")
		   public String showProcedureDetail(@PathVariable("patientId") int patientId, Model model) {
		       PatientProcedure patientprocedure = new PatientProcedure();
		       model.addAttribute("patientprocedure", patientprocedure);
		       PatientInfo patientInfo = patientInfoService.getPatientInfoById(patientId);
		        model.addAttribute("patientinfo", patientInfo);
		       return "procedureDetails";
		   }


		   @PostMapping("/SavePatientProcedure/{patientnumber}")
		   public String savePatientProcedure(
		       @ModelAttribute PatientProcedure patientProcedure,
		       @PathVariable("patientnumber") int patientNumber) {
		       PatientInfo patientInfo = patientInfoService.getPatientInfoById(patientNumber);
		       patientProcedure.setProcedurenumber(patientInfo);
		       patientProcedureService.savePatientProcedure(patientProcedure);
		       return "redirect:/patientList";
		   }


		   
//PatientProcedure controller ENDs		
	
	@PostMapping("/persistMessage")
	public String persistMessage(@RequestParam("msg") String msg, HttpServletRequest request) {
		@SuppressWarnings("unchecked")
		List<String> messages = (List<String>) request.getSession().getAttribute("MY_SESSION_MESSAGES");
		if (messages == null) {
			messages = new ArrayList<>();
			request.getSession().setAttribute("MY_SESSION_MESSAGES", messages);
		}
		messages.add(msg);
		request.getSession().setAttribute("MY_SESSION_MESSAGES", messages);
		return "redirect:/login";
	}

	@PostMapping("/destroy")
	public String destroySession(HttpServletRequest request) {
		request.getSession().invalidate();
		return "redirect:/login";
	}
	
		
}
