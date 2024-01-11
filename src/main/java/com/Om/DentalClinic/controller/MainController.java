package com.Om.DentalClinic.controller;

import java.io.IOException;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.Om.DentalClinic.model.Appointment;
import com.Om.DentalClinic.model.PatientInfo;
import com.Om.DentalClinic.model.PatientProcedure;
import com.Om.DentalClinic.model.Sittings;
import com.Om.DentalClinic.model.User;
import com.Om.DentalClinic.repository.PatientInfoRepository;
import com.Om.DentalClinic.repository.PatientProcedureRepository;
import com.Om.DentalClinic.repository.UserRepository;
import com.Om.DentalClinic.service.AppointmentService;
import com.Om.DentalClinic.service.PatientInfoService;
import com.Om.DentalClinic.service.PatientProcedureService;
import com.Om.DentalClinic.service.SittingService;
import com.Om.DentalClinic.service.UserService;
import com.Om.DentalClinic.service.UserServiceImpl;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.ByteArrayOutputStream;
//
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;


@Controller
public class MainController {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private UserServiceImpl userServiceImpl;
	
	@Autowired
	private PatientInfoService patientInfoService;
	
	@Autowired
	private PatientProcedureService patientProcedureService;
		
	@Autowired
	private PatientInfoRepository patientInfoRepository;
	
	@Autowired
	private PatientProcedureRepository patientProcedureRepository;
	
	@Autowired
	private AppointmentService appointmentService;
	
	@Autowired
	private SittingService sittingService;
	
	
	

//Controller for Login-----------------------------------------------------------------------------------------	

	@GetMapping("/")
	 public String showLogin(Model model, HttpSession session) {
		User user=new User();
		model.addAttribute("user", user);
			return "login";

		}
	 
	@GetMapping("/login")
	public ModelAndView homepage() {
		ModelAndView mav = new ModelAndView("login");
		return mav;
	}
	 
	 @GetMapping("/logout")
	    public String logout() {
	        // Add logout logic here
	        return "redirect:/";
	    }


	 @PostMapping("/login")
	    public String login(HttpServletRequest request, @RequestParam String username, @RequestParam String password, Model model,RedirectAttributes redirectAttributes) {
	        User user = userServiceImpl.findByUsername(username);

	        if (user != null && user.getPassword().equals(password)) {
	            HttpSession session = request.getSession();
	            session.setAttribute("username", username);

	            if ("ADMIN".equals(user.getRole())) {
	            	redirectAttributes.addFlashAttribute("successMessage", "Login successful!");
	            	return "redirect:/adminHome";
	            } else if ("RECEP1".equals(user.getRole()) || "RECEP2".equals(user.getRole()) || "RECEP3".equals(user.getRole())) {
	                // Update patient_info and patient_procedure tables
	            	redirectAttributes.addFlashAttribute("successMessage", "Login successful!");
	                return "redirect:/userhome";
	            }
	        }

	        model.addAttribute("error", "Invalid username or password.");
	        return "login";
	    }

	 // Registration Controller (Prasad)
		@GetMapping("/register")
		public String home(HttpServletRequest request, Model model, Principal principal) {
			HttpSession session = request.getSession();
		     String username = (String) session.getAttribute("username");
		     
		     if (username != null) {
		            User user = userServiceImpl.findByUsername(username);

		            // Check if the user has the 'ADMIN' role
		            if ("ADMIN".equals(user.getRole())) {
		                // Pass the user role to the view
		                model.addAttribute("userRole", user.getRole());
		                model.addAttribute("username", username);
		                return "register";
		            } else {
		                // Redirect to an error page or display an error message
		                return "redirect:/accessdenied";
		            }
		        }

		        model.addAttribute("error", "User Not Found");
		        return "redirect:/login";
		    }
		
		
		@PostMapping("/register")
		public String create(@ModelAttribute("users") User user, HttpSession session) {
			
			
			boolean u = userServiceImpl.checkUsername(user.getUsername());
			if (u) {
				System.out.println("User is already exist");
			} else {
				System.out.println(user);
				// password encryption
				//user.setPassword(bp.encode(user.getPassword()));
				// user.setRole(user.getRole());
				
//				if("ADMIN".equals(user.getRole())) {
//					// Perform the registration process for "ADMIN" users
//		            // For example, you can save the user to the database here
//					userRepository.save(user);
//				}else {
//					// If the user's role is not "ADMIN," redirect them to the login page
//		            return "redirect:/login?accessdenied";
//				}

				session.setAttribute("msg", "Registration  successfully!");
				userRepository.save(user);
			}

			return "redirect:/usersList";
		}
	 
		@GetMapping("/accessdenied")
		public String errorpage() {
			return "accessdenied";
		}
		 
		@GetMapping("/usersList")
		public String showUsersList(HttpServletRequest request, Model model,Principal principal) {
			HttpSession session = request.getSession();
			String username = (String) session.getAttribute("username");		     
			
			if(username!=null) {
				User user=userServiceImpl.findByUsername(username);
				if("ADMIN".equals(user.getRole())) {
					model.addAttribute("userRole", user.getRole());
					model.addAttribute("username", username);
					
					model.addAttribute("usersList", userServiceImpl.getAllUsers());
					return "usersList";
					
				}else {
	                // Redirect to an error page or display an error message
	                return "redirect:/accessdenied";
	            }
				
			}
 
			model.addAttribute("error","User Not Found");
			return "redirect:/login";
		
			
		}
		
		@GetMapping("/deleteUser/{id}")
		public String deleteuser(@PathVariable(value = "id") int id) {
			this.userService.deleteUserById(id);
			
			return "redirect:/usersList";
		}
		
	 
//User Controller------------------------------------------------------------------------------	 
	 		
	 @GetMapping("/adminHome")
	 public String adminHome(HttpServletRequest request, Model model,Principal principal){
		 HttpSession session = request.getSession();
	     String username = (String) session.getAttribute("username");
	     
	     User user = userServiceImpl.findByUsername(username);
		 model.addAttribute("userRole", user.getRole());
	     
	     if(username!=null) {
	    	 model.addAttribute("username", username);
			 return "adminHome";
	     }
	     model.addAttribute("error","User Not Found");
			return "redirect:/login";
	     

	 }

	 
	 @GetMapping("/userhome")
	 public String userHome(HttpServletRequest request,Model model,Principal principal) {
		 HttpSession session = request.getSession();
	     String username = (String) session.getAttribute("username");
	     
	     User user = userServiceImpl.findByUsername(username);
		 model.addAttribute("userRole", user.getRole());
	     
	     if(username!=null) {
	    	 model.addAttribute("username", username);
			 return "home";
	     }
	     model.addAttribute("error","User Not Found");
			return "redirect:/login";


	  }


//Controller for PatientInfo------------------------------------------------------------------------------	 
 
	 @GetMapping("/patientinfo")
	 public String showPatientinfo(HttpServletRequest request, Model model,Principal principal) {
		 HttpSession session = request.getSession();
	     String username = (String) session.getAttribute("username");
	     
	     User user = userServiceImpl.findByUsername(username);
		 model.addAttribute("userRole", user.getRole());
	     // 
	     if(username!=null) {
	    	  PatientInfo patientinfo = new PatientInfo();
	    	  if(patientinfo!=null) {

	 	    	model.addAttribute("username", username);
	 	    	model.addAttribute("patientinfo", patientinfo);
	 			return "patientinfo";
	    	  }
	    	
	     }
	     model.addAttribute("error","User Not Found");
			return "redirect:/login";
	     
	     

	 } 
	 
	
	 @GetMapping("/patientList")
		public String showPatientList(HttpServletRequest request, Model model,Principal principal) {
			HttpSession session = request.getSession();
			String username = (String) session.getAttribute("username");		     
			
			if(username!=null) {
				User user=userServiceImpl.findByUsername(username);
				if(user !=null) {
					model.addAttribute("userRole", user.getRole());
					model.addAttribute("username", username);
					model.addAttribute("listpatients", patientInfoService.findAllByOrderByPatientregdateDesc());
					return "patientList";
					
				}
				
			}
 
			model.addAttribute("error","User Not Found");
			return "redirect:/login";
		    
		}
		
		@GetMapping("/deletePatientInfo/{id}")
		public String deletePatientInfo(@PathVariable(value = "id") int id) {
			this.patientInfoService.deletePatientInfoById(id);
			return "redirect:/patientList";
		}
		
		@GetMapping("/editPatientinfo/{id}")
		public String editPatientInfoForm(HttpServletRequest request, @PathVariable("id") int id, Model model) {
			HttpSession session = request.getSession();
		    String username = (String) session.getAttribute("username");
		    
		    User user = userServiceImpl.findByUsername(username);
			model.addAttribute("userRole", user.getRole());
		    
		    // by prasad 
		    if(username!=null) {
		    	PatientInfo patientinfo = patientInfoService.getPatientInfoById(id);
				if(patientinfo !=null) {
					
					model.addAttribute("username", username);
					model.addAttribute("patientinfo", patientinfo);
					return "editPatientInfo";
					
				}
				
			}
 
			model.addAttribute("error","User Not Found");
			return "redirect:/login";

		}
		
		@GetMapping("/viewPatientinfo/{id}")
		public String viewPatientInfoForm(HttpServletRequest request, @PathVariable("id") int id, Model model) {
			HttpSession session = request.getSession();
		    String username = (String) session.getAttribute("username");
		    
		    User user = userServiceImpl.findByUsername(username);
			model.addAttribute("userRole", user.getRole());
			
		    if(username!=null) {
		    	PatientInfo patientinfo = patientInfoService.getPatientInfoById(id);
				if(patientinfo !=null) {
					
					model.addAttribute("username", username);
					model.addAttribute("patientinfo", patientinfo);
					return "viewPatient";
					
				}
			} 
			model.addAttribute("error","User Not Found");
			return "redirect:/login";

		}
		
		
		@PostMapping("/updatePatientInfo")
		public String updatePatientInfo(@RequestParam(value ="patientReports",required = false) MultipartFile patientReports,
		                                @RequestParam("firstname") String firstname,
		                                @RequestParam("middlename") String middlename,
		                                @RequestParam("lastname") String lastname,
		                                @RequestParam("patientage") int patientage,
		                                @RequestParam(value ="patientgender", defaultValue = "") String patientgender,
		                                @RequestParam("patientregdate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date patientregdate,
		                                @RequestParam("patientmobile1") long patientmobile1,
		                                @RequestParam("patientmobile2") long patientmobile2,
		                                @RequestParam("patientmedicalhistory") String patientmedicalhistory,
		                                @RequestParam("patientnumber") int patientnumber,
		                                HttpServletRequest request) throws IOException {
		    HttpSession session = request.getSession();
		    String username = (String) session.getAttribute("username");

		    // Load the existing patient information from the database
		    PatientInfo existingPatientInfo = patientInfoService.getPatientInfoById(patientnumber);

		    // Check if the patient information exists
		    if (existingPatientInfo != null) {
		        // Update the fields of the existing patient information
		        existingPatientInfo.setFirstname(firstname);
		        existingPatientInfo.setMiddlename(middlename);
		        existingPatientInfo.setLastname(lastname);
		        existingPatientInfo.setPatientage(patientage);
		        existingPatientInfo.setPatientgender(patientgender);
		        existingPatientInfo.setPatientregdate(patientregdate);
		        existingPatientInfo.setPatientmobile1(patientmobile1);
		        existingPatientInfo.setPatientmobile2(patientmobile2);
		        existingPatientInfo.setPatientmedicalhistory(patientmedicalhistory);
		        existingPatientInfo.setCashiername(username);

		        // Update the patientReports if a new file is provided
		        if (patientReports != null && !patientReports.isEmpty()) {
		            existingPatientInfo.setPatientReports(patientReports.getBytes());
		        }
		        // Save the updated patient information using the .save method
		        patientInfoRepository.save(existingPatientInfo);
		    }

		    return "redirect:/patientList";
		}



		
		@PostMapping("/SavePatientInfo")
		public String savePatientInfo(@RequestParam(value ="patientReports",required = false) MultipartFile patientReports,
		                              @RequestParam("firstname") String firstname,
		                              @RequestParam("middlename") String middlename,
		                              @RequestParam("lastname") String lastname,
		                              @RequestParam("patientage") int patientage,
		                              @RequestParam(value ="patientgender", defaultValue = "") String patientgender,
		                              @RequestParam("patientregdate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date patientregdate,
		                              @RequestParam("patientmobile1") long patientmobile1,
		                              @RequestParam("patientmobile2") long patientmobile2,
		                              @RequestParam("patientmedicalhistory") String patientmedicalhistory,
		                              HttpServletRequest request) throws IOException {
		    
		    // Get username from session
		    HttpSession session = request.getSession();
		    String username = (String) session.getAttribute("username");

		    // Save patient information along with the cashier's username
		    patientInfoService.savePatientInfo(patientReports, firstname, middlename, lastname, patientage, patientgender, patientregdate, patientmobile1, patientmobile2, patientmedicalhistory, username);
		    
		    return "redirect:/patientList";
		}
		
		
		@GetMapping("/patients/excel")
		public void exportPatientsAndProceduresToExcel(HttpServletResponse response) throws IOException {
		    ByteArrayOutputStream excelData = patientInfoService.exportPatientsAndProceduresToExcel();
		    // Generate the file name with the current date
		    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		    String currentDate = dateFormat.format(new Date());
		    String fileName = "patient_and_procedure_" + currentDate + ".xlsx";
		    response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		    response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
		    excelData.writeTo(response.getOutputStream());
		    excelData.close();
		}

			
//PatientInfo Code Ends here----------------------------------------------------------------------------------------------
	

//PatientProcedure controller ------------------------------------------------------------------------------	

			 @GetMapping("/patientDetails/{patientId}")
			 public String showPatientDetail(HttpServletRequest request, @PathVariable("patientId") int patientId,Model model) {

					HttpSession session = request.getSession();
				     String username = (String) session.getAttribute("username");	
				     if (username != null) {
				         User user = userServiceImpl.findByUsername(username);

				         if (user != null) {
				             // Pass the user's role to the view
				             model.addAttribute("userRole", user.getRole());

				             // Pass the username to the view
				             model.addAttribute("username", username);
				             PatientInfo patientInfo = patientInfoService.getPatientInfoById(patientId);
				             model.addAttribute("patientinfo", patientInfo);

				             List<PatientProcedure> patientProcedures = patientProcedureService.getProceduresByPatientId(patientId);
				             model.addAttribute("patientProcedures", patientProcedures);
				             return "patientDetails";
				         }
				     }

				     // Handle the case where the user is null
				     model.addAttribute("error", "User not found.");
				     return "redirect:/login";

				     
			 }
		
		
		   @GetMapping("/procedureDetails/{patientId}")
		   public String showProcedureDetail(HttpServletRequest request, @PathVariable("patientId") int patientId, Model model) {
			     HttpSession session = request.getSession();
			     String username = (String) session.getAttribute("username");
			     
			     User user = userServiceImpl.findByUsername(username);
				 model.addAttribute("userRole", user.getRole());
			     
			     // Pass the username to the view
			     model.addAttribute("username", username); 	   
			   PatientProcedure patientprocedure = new PatientProcedure();
		       model.addAttribute("patientprocedure", patientprocedure);
		       PatientInfo patientInfo = patientInfoService.getPatientInfoById(patientId);
		        model.addAttribute("patientinfo", patientInfo);
		       return "procedureDetails";
		   }


		   @PostMapping("/SavePatientProcedure/{patientnumber}")
		   public String savePatientProcedure(@ModelAttribute PatientProcedure patientProcedure,
				   @PathVariable("patientnumber") int patientNumber, HttpServletRequest request) {				   
		       HttpSession session = request.getSession(); // Get username from session
		       String username = (String) session.getAttribute("username");
		       PatientInfo patientInfo = patientInfoService.getPatientInfoById(patientNumber);	// Fetch patient information
		       patientProcedure.setCashiername(username); // Set proc_cashier_name and procedure number
		       patientProcedure.setProcedurenumber(patientInfo);
		       patientProcedure.setPaymentamount(patientProcedure.getFirstpayment());//this will add payment amount same as first payment
		       // Save patient procedure
		       patientProcedureService.savePatientProcedure(patientProcedure);
		       return "redirect:/patientList";
		   }
		   

//		   @PostMapping("/UpdatePatientProcedure/{patientnumber}")
//		   public String updatePatientProcedure(@ModelAttribute PatientProcedure patientProcedure, @PathVariable("patientnumber") int patientNumber, HttpServletRequest request) {
//		       // Your controller logic here
//			   HttpSession session = request.getSession(); // Get username from session
//		       String username = (String) session.getAttribute("username");
		   
		   
//		       PatientInfo patientInfo = patientInfoService.getPatientInfoById(patientNumber);	// Fetch patient information
		   
		   
//		       patientProcedure.setCashiername(username); // Set proc_cashier_name and procedure number
//		       patientProcedure.setProcedurenumber(patientInfo);
		   
		   
//		       // Save patient procedure
//		       patientProcedureService.savePatientProcedure(patientProcedure);
//		       return "redirect:/patientDetails/" + patientNumber;
//		   }

		   @PostMapping("/UpdatePatientProcedure/{patientnumber}")
		   public String updatePatientProcedure(@RequestParam("proceduredate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date proceduredate ,
			   									@RequestParam("proceduretype") String proceduretype,
			   									@RequestParam("proceduredetail") String proceduredetail,
			   								//	@RequestParam("estimateamount") Double estimateamount,
			   								//	@RequestParam("balanceamount") Double balanceamount,
			   									@RequestParam("labname") String labname,
			   									@RequestParam("externaldoctor") String externaldoctor,			   									
			   									@RequestParam("procedureid") int procedureid,
			   									@PathVariable("patientnumber") int patientNumber,
			   									HttpServletRequest request) {
			   									
		       // Your controller logic here
			   HttpSession session = request.getSession();
		       String username = (String) session.getAttribute("username");
		       
		       PatientProcedure existingprocedure = patientProcedureService.getPatientProcedureById(procedureid);
		       
		       if(existingprocedure!=null) {
		       existingprocedure.setCashiername(username);
		       existingprocedure.setProceduredate(proceduredate);
		       existingprocedure.setProceduretype(proceduretype);
		       existingprocedure.setProceduredetail(proceduredetail);
		 //      existingprocedure.setEstimateamount(estimateamount);
		//       existingprocedure.setBalanceamount(balanceamount);
		       existingprocedure.setLabname(labname);
		       existingprocedure.setExternaldoctor(externaldoctor);
//		       PatientInfo patientInfo = patientInfoService.getPatientInfoById(patientNumber);	
//		       patientProcedure.setProcedurenumber(patientInfo);
		       }
		       patientProcedureRepository.save(existingprocedure);
		       return "redirect:/patientDetails/" + patientNumber;
		   }
			
			@GetMapping("editProcedure/{patientId}/{procedureId}")
			public String editProcedureForm(HttpServletRequest request,@PathVariable(value = "patientId") int patientId,
					@PathVariable("procedureId") int procedureId, Model model) {
				HttpSession session = request.getSession();
			     String username = (String) session.getAttribute("username");
			     User user = userServiceImpl.findByUsername(username);
			     // Pass the username to the view
			     model.addAttribute("username", username); 	
			     model.addAttribute("userRole", user.getRole());
			    PatientProcedure patientProcedure = patientProcedureService.getPatientProcedureById(procedureId);
				model.addAttribute("patientProcedure", patientProcedure);
			    PatientInfo patientInfo = patientInfoService.getPatientInfoById(patientId);
			    model.addAttribute("patientinfo", patientInfo);
				return "editProcedure";
			}
			
		   @GetMapping("/deleteProcedure/{patientId}/{procedureId}")
		   public String deletePatientProcedure(@PathVariable(value = "patientId") int patientId,
				   @PathVariable(value = "procedureId") int procedureId) {
		       this.patientProcedureService.deletePatientProcedureById(procedureId);
		       return "forward:/patientDetails/" + patientId; // Use "forward" to stay on the same page
		   }

 
//PatientProcedure controller ENDs		
	

		   @GetMapping("/displayAmount")
		   public String showAmount(HttpServletRequest request, Model model,Principal principal) {
		       // Initialize fromDate and toDate with default values if needed
		       Date fromDate = null; // Set your default fromDate value here
		       Date toDate = null; // Set your default toDate value here

		       // Add fromDate and toDate to the model so they can be pre-populated in the form
		       HttpSession session = request.getSession();
			   String username = (String) session.getAttribute("username");
			   
			   User user = userServiceImpl.findByUsername(username);
			   model.addAttribute("userRole", user.getRole());
			   
			   model.addAttribute("username", username); 	
		       model.addAttribute("fromDate", fromDate);
		       model.addAttribute("toDate", toDate);

		       return "displayAmount";
		   }
		   
		   @GetMapping("/doctorReport")
		   public String showDrReport(HttpServletRequest request, Model model,Principal principal) {
		       // Initialize fromDate and toDate with default values if needed
		       Date fromDate = null; // Set your default fromDate value here
		       Date toDate = null; // Set your default toDate value here

		       // Add fromDate and toDate to the model so they can be pre-populated in the form
		       HttpSession session = request.getSession();
			   String username = (String) session.getAttribute("username");
			   
			   User user = userServiceImpl.findByUsername(username);
			   model.addAttribute("userRole", user.getRole());
			   
			   model.addAttribute("username", username); 	
		       model.addAttribute("fromDate", fromDate);
		       model.addAttribute("toDate", toDate);

		       return "externalDrReport";
		   }
		   
		   @GetMapping("/labReport")
		   public String showlabReport(HttpServletRequest request, Model model,Principal principal) {
		       // Initialize fromDate and toDate with default values if needed
		       Date fromDate = null; // Set your default fromDate value here
		       Date toDate = null; // Set your default toDate value here

		       // Add fromDate and toDate to the model so they can be pre-populated in the form
		       HttpSession session = request.getSession();
			   String username = (String) session.getAttribute("username");
			   
			   User user = userServiceImpl.findByUsername(username);
			   model.addAttribute("userRole", user.getRole());
			   
			   model.addAttribute("username", username); 	
		       model.addAttribute("fromDate", fromDate);
		       model.addAttribute("toDate", toDate);

		       return "LabReport";
		   }

		   
//		   @PostMapping("/filterData")
//		    public String filterProcedures(
//		            @RequestParam("fromDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date fromDate,
//		            @RequestParam("toDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date toDate,
//		            @RequestParam("session") String session, HttpServletRequest request,
//		            Model model) {
//			   
//		        // Get filtered procedures based on dates and session
//		        List<PatientProcedure> filteredProcedures = patientProcedureService.getFilteredProcedures(fromDate, toDate, session);
//		        
//		        HttpSession Session = request.getSession();
//				String username = (String) Session.getAttribute("username");
//				model.addAttribute("username", username); 
//		        // Add the filtered procedures to the model for displaying in the view
//		        model.addAttribute("patientProcedures", filteredProcedures);
//		        // Add fromDate and toDate to the model for display in the view if needed
//		        model.addAttribute("fromDate", fromDate);
//		        model.addAttribute("toDate", toDate);
//		        model.addAttribute("session", session);
//		        return "displayAmount";
//		    }
		   
		   @PostMapping("/filterData")
		   public String filterProcedures(
		           @RequestParam("fromDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date fromDate,
		           @RequestParam("toDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date toDate,
		           @RequestParam("session") String session, HttpServletRequest request,
		           Model model) {

		       // Get filtered procedures from tbl_patient_procedure based on dates and session
		       List<PatientProcedure> filteredPatientProcedures = patientProcedureService.getFilteredProcedures(fromDate, toDate, session);

		       // Get filtered procedures from tbl_proc_sittings based on dates and session
		       List<Sittings> filteredProcSittings = sittingService.getFilteredProcSittings(fromDate, toDate, session);

		       // Combine the results into a single list
		       List<Object> combinedResults = new ArrayList<>();
		       combinedResults.addAll(filteredPatientProcedures);
		       combinedResults.addAll(filteredProcSittings);

		       HttpSession Session = request.getSession();
		       String username = (String) Session.getAttribute("username");
		       model.addAttribute("username", username);

		       // Add the combined procedures to the model for displaying in the view
		       model.addAttribute("combinedProcedures", combinedResults);

		       // Add fromDate and toDate to the model for display in the view if needed
		       model.addAttribute("fromDate", fromDate);
		       model.addAttribute("toDate", toDate);
		       model.addAttribute("session", session);

		       return "displayAmount";
		   }
		   
		   @PostMapping("/filterDoctor")
		   public String filterProceduresOnDoctor(
		           @RequestParam("fromDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date fromDate,
		           @RequestParam("toDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date toDate,
		           @RequestParam("doctor") String doctor, HttpServletRequest request,
		           Model model) {

		       // Get filtered procedures from tbl_patient_procedure based on dates and session
		       List<PatientProcedure> filteredPatientProcedures = patientProcedureService.getFilteredProceduresByDoctor(fromDate, toDate, doctor);

		       // Get filtered procedures from tbl_proc_sittings based on dates and session
		       List<Sittings> filteredProcSittings = sittingService.getFilteredProcSittingsByDoctor(fromDate, toDate, doctor);

		       // Combine the results into a single list
		       List<Object> combinedResults = new ArrayList<>();
		       combinedResults.addAll(filteredPatientProcedures);
		       combinedResults.addAll(filteredProcSittings);

		       HttpSession Session = request.getSession();
		       String username = (String) Session.getAttribute("username");
		       model.addAttribute("username", username);

		       // Add the combined procedures to the model for displaying in the view
		       model.addAttribute("combinedProcedures", combinedResults);

		       // Add fromDate and toDate to the model for display in the view if needed
		       model.addAttribute("fromDate", fromDate);
		       model.addAttribute("toDate", toDate);
		       model.addAttribute("doctor", doctor);

		       return "externalDrReport";
		   }
		   
		   @PostMapping("/filterLab")
		   public String filterProceduresOnLab(
		           @RequestParam("fromDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date fromDate,
		           @RequestParam("toDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date toDate,
		           @RequestParam("lab") String lab, HttpServletRequest request,
		           Model model) {

		       // Get filtered procedures from tbl_patient_procedure based on dates and session
		       List<PatientProcedure> filteredPatientProcedures = patientProcedureService.getFilteredProceduresByLab(fromDate, toDate, lab);

		       // Get filtered procedures from tbl_proc_sittings based on dates and session
		       List<Sittings> filteredProcSittings = sittingService.getFilteredProcSittingsByLab(fromDate, toDate, lab);

		       // Combine the results into a single list
		       List<Object> combinedResults = new ArrayList<>();
		       combinedResults.addAll(filteredPatientProcedures);
		       combinedResults.addAll(filteredProcSittings);

		       HttpSession Session = request.getSession();
		       String username = (String) Session.getAttribute("username");
		       model.addAttribute("username", username);

		       // Add the combined procedures to the model for displaying in the view
		       model.addAttribute("combinedProcedures", combinedResults);

		       // Add fromDate and toDate to the model for display in the view if needed
		       model.addAttribute("fromDate", fromDate);
		       model.addAttribute("toDate", toDate);
		       model.addAttribute("lab", lab);

		       return "LabReport";
		   }





//Appointment controller--------------------------------------------------------------------------------------------------------		
		   
		   
		   @GetMapping("/appointment")
		   public String showAppointment(HttpServletRequest request, Model model, Principal principal) {
			   HttpSession Session = request.getSession();
				String username = (String) Session.getAttribute("username");
				User user=userServiceImpl.findByUsername(username);
				model.addAttribute("userRole",user.getRole());
				
				model.addAttribute("username", username); 				
				Appointment appointment = new Appointment();
				model.addAttribute("appointment", appointment); 
				return "appointment";
		   }  
		   
		   @PostMapping("/saveAppointment")
		   public String saveAppointment(@RequestParam("starttime") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") Date starttime,
		                                 @RequestParam("endtime") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") Date endtime,
		                                 @RequestParam("firstname") String firstname,
		                                 @RequestParam("middlename") String middlename,
		                                 @RequestParam("lastname") String lastname,
		                                 @RequestParam("treatment") String treatment,
		                                 @RequestParam("patientmobile1") long patientmobile1,
		                                 HttpServletRequest request, 
		                                 Model model) {

		       HttpSession session = request.getSession();
		       String username = (String) session.getAttribute("username");

		       boolean isAppointmentSaved = appointmentService.saveAppointment(starttime, endtime, firstname, middlename, lastname, treatment, patientmobile1, username);

		       if (isAppointmentSaved) {
		           model.addAttribute("successMessage", "Appointment scheduled successfully!");
		       } else {
		           model.addAttribute("dateError", "Error occurred while scheduling the appointment.");
		       }

		       // Create a new Appointment object and add it to the model for the form
		       Appointment appointment = new Appointment();
		       model.addAttribute("appointment", appointment);

		       return "appointment"; // Return to the appointment form with a success message or error
		   }


		


		   
		   @PostMapping("/updateAppointment")
		   public String updateAppointment(@RequestParam("starttime") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") Date starttime,
		                                 @RequestParam("endtime") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") Date endtime,
		                                 @RequestParam("firstname") String firstname,
		                                 @RequestParam("middlename") String middlename,
		                                 @RequestParam("lastname") String lastname,
		                                 @RequestParam("treatment") String treatment,
		                                 @RequestParam("patientmobile1") long patientmobile1,
		                                 @RequestParam("appointmentnum") int appointmentnum) {
		       appointmentService.updateAppointment(starttime, endtime, firstname, middlename, lastname, treatment, patientmobile1,appointmentnum);            
		       return "redirect:/viewAppointment";
		   }
	
			@GetMapping("/editAppointment/{id}")
			public String editAppointment(@PathVariable("id") int id, Model model,Principal principal,HttpServletRequest request) {
				HttpSession Session = request.getSession();
				String username = (String) Session.getAttribute("username");
				User user = userServiceImpl.findByUsername(username);
			     model.addAttribute("userRole", user.getRole());
				
				Appointment appointment = appointmentService.getAppointmentById(id);		    							
				model.addAttribute("appointment", appointment);						
				return "editAppointment";
			}
		   
			@GetMapping("/deleteAppointment/{id}")
			public String deleteAppointment(@PathVariable(value = "id") int id) {
				this.appointmentService.deleteAppointmentById(id);				
				return "redirect:/viewAppointment";
			}
		   
		   @GetMapping("/viewAppointment")
		   public String showAppointmentView(HttpServletRequest request, Model model) {
			   HttpSession Session = request.getSession();
				String username = (String) Session.getAttribute("username");
				User user = userServiceImpl.findByUsername(username);
			     model.addAttribute("userRole", user.getRole());
				model.addAttribute("username", username); 
				model.addAttribute("username", username); 			
				return "viewAppointment";
		   }
		   		   
		   @PostMapping("/filterAppointments")
		   public String filterAppointments(@RequestParam("appointmentDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date appointmentDate, HttpServletRequest request, Model model) {
			   HttpSession Session = request.getSession();
				String username = (String) Session.getAttribute("username");
				User user = userServiceImpl.findByUsername(username);
			     
			     model.addAttribute("userRole", user.getRole());
				model.addAttribute("username", username); 
		       List<Appointment> filteredAppointments = appointmentService.getAppointmentsByDate(appointmentDate);
		       model.addAttribute("filteredAppointments", filteredAppointments);
		       model.addAttribute("selectedDate", appointmentDate);
		       return "viewAppointment";
		   }
		   		   
		   @GetMapping("/appointment/excel")
		    public void exportAppointmentsToExcel(HttpServletResponse response) throws IOException {
		        ByteArrayOutputStream excelData = appointmentService.exportAppointmentsToExcel();
		        // Generate the file name with the current date
		        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		        String currentDate = dateFormat.format(new Date());
		        String fileName = "appointments_" + currentDate + ".xlsx";
		        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
		        excelData.writeTo(response.getOutputStream());
		        excelData.close();
		    }
		   
//-Sitting Controller ------------------------------------------------------------------------------------------------------------------------------------------------	
	
		   @GetMapping("/sitting/{procedureid}")
		   public String showSittingForm(HttpServletRequest request, @PathVariable("procedureid") int procedureid, Model model) {
			     HttpSession session = request.getSession();
			     String username = (String) session.getAttribute("username");
			     User user = userServiceImpl.findByUsername(username);
			     model.addAttribute("userRole", user.getRole());
			     
			     model.addAttribute("username", username); 	   		   
			     Sittings sitting = new Sittings();
			     model.addAttribute("sitting", sitting);		       
			     PatientProcedure procedureProcedure = patientProcedureService.getPatientProcedureById(procedureid);
		        model.addAttribute("procedureProcedure", procedureProcedure);
		       return "sitting";
		   }
		   

		   @PostMapping("/saveSitting/{procedureid}")
		   public String saveSitting(@ModelAttribute Sittings sitting, @PathVariable("procedureid") int procedureId, HttpServletRequest request) {
		       HttpSession session = request.getSession();
		       String username = (String) session.getAttribute("username");
		       
		       PatientProcedure patientprocedure = patientProcedureService.getPatientProcedureById(procedureId);
		       double balance = patientprocedure.getBalanceamount();
		       
		       double totalPayment = sitting.getSittingcashpayment() + sitting.getSittingonlinepayment();
		       double finalBalance = balance-totalPayment ;
		       patientprocedure.setBalanceamount(finalBalance > 0 ? finalBalance : 0.0);
		       
		       double paymentamount = patientprocedure.getPaymentamount();
		       double finalpaymentamount = paymentamount+totalPayment;
		       patientprocedure.setPaymentamount(finalpaymentamount>0? finalpaymentamount : 0.0);
		       
		       sitting.setSittingidproc(patientprocedure);
		       sitting.setSittingproccashiername(username);
		       
		       PatientInfo patientId = patientprocedure.getProcedurenumber();
		       int id = patientId.getPatientnumber();
		       
		       sittingService.saveSitting(sitting);
		       
		       return "redirect:/patientDetails/"+id;
		       
		   }
		   
//		   @PostMapping("/UpdatePatientProcedure/{patientnumber}")
//		   public String updatePatientProcedure(@ModelAttribute PatientProcedure patientProcedure, @PathVariable("patientnumber") int patientNumber, HttpServletRequest request) {
//		       // Your controller logic here
//			   HttpSession session = request.getSession(); // Get username from session
//		       String username = (String) session.getAttribute("username");
		   
		   
//		       PatientInfo patientInfo = patientInfoService.getPatientInfoById(patientNumber);	// Fetch patient information
		   
		   
//		       patientProcedure.setCashiername(username); // Set proc_cashier_name and procedure number
//		       patientProcedure.setProcedurenumber(patientInfo);
		   
		   
//		       // Save patient procedure
//		       patientProcedureService.savePatientProcedure(patientProcedure);
//		       return "redirect:/patientDetails/" + patientNumber;
//		   }
		   
		   
		   @PostMapping("/updateSitting/{procedureid}")
		   public String updateSitting(@ModelAttribute Sittings sitting, @PathVariable("procedureid") int procedureId, HttpServletRequest request) {
		       HttpSession session = request.getSession();
		       String username = (String) session.getAttribute("username");
		       
		       PatientProcedure patientprocedure = patientProcedureService.getPatientProcedureById(procedureId);
		       
		       sitting.setSittingidproc(patientprocedure);
		       sitting.setSittingproccashiername(username);

		       sittingService.saveSitting(sitting);
		       
		       return "redirect:/sittingList/"+procedureId;
		       
		   }
		   

			 @GetMapping("/sittingList/{procedureid}")
			 public String showSitting(HttpServletRequest request, @PathVariable("procedureid") int procedureid,Model model) {
					HttpSession session = request.getSession();
				     String username = (String) session.getAttribute("username");	
				     if (username != null) {
				         User user = userServiceImpl.findByUsername(username);
				         if (user != null) {
				             // Pass the user's role to the view
				             model.addAttribute("userRole", user.getRole());
				             // Pass the username to the view
				             model.addAttribute("username", username);   
				             PatientProcedure patientprocedure = patientProcedureService.getPatientProcedureById(procedureid);
				             model.addAttribute("patientprocedure", patientprocedure);
				             List<Sittings> sitting = sittingService.getSittingByProcedureId(procedureid);
				             model.addAttribute("sitting", sitting);
				             return "sittingList";
				         }
				     }
				     // Handle the case where the user is null
				     model.addAttribute("error", "User not found.");
				     return "redirect:/login";	     
			 }
  
			 
			 @GetMapping("/deleteSitting/{sittingid}/{procedureid}")
			   public String deleteSitting(@PathVariable(value = "sittingid") int sittingid,@PathVariable(value = "procedureid") int procedureid) {
			       this.sittingService.deleteSittingById(sittingid);
			       return "forward:/sittingList/" + procedureid; // Use "forward" to stay on the same page
			   }

			 

			 
			 
					 @GetMapping("viewSitting/{sittingid}/{procedureid}")
					public String viewSittingForm(HttpServletRequest request,@PathVariable(value = "sittingid") int sittingid,
						@PathVariable("procedureid") int procedureid, Model model) {
					HttpSession session = request.getSession();
				     String username = (String) session.getAttribute("username");
				     User user = userServiceImpl.findByUsername(username);
				     // Pass the username to the view
				     model.addAttribute("username", username); 	
				     model.addAttribute("userRole", user.getRole());
			  		  PatientProcedure patientProcedure = patientProcedureService.getPatientProcedureById(procedureid);
					model.addAttribute("patientProcedure", patientProcedure);
					 Sittings sitting = sittingService.getSittingById(sittingid);
				   model.addAttribute("sitting", sitting);
					return "viewSitting";
					}
					 
					 
					 @GetMapping("editSitting/{sittingid}/{procedureid}")
					public String editSittingForm(HttpServletRequest request,@PathVariable(value = "sittingid") int sittingid,
						@PathVariable("procedureid") int procedureid, Model model) {
					HttpSession session = request.getSession();
				     String username = (String) session.getAttribute("username");
				     User user = userServiceImpl.findByUsername(username);
				     // Pass the username to the view
				     model.addAttribute("username", username); 	
				     model.addAttribute("userRole", user.getRole());
			  		  PatientProcedure patientProcedure = patientProcedureService.getPatientProcedureById(procedureid);
					model.addAttribute("patientProcedure", patientProcedure);
					 Sittings sitting = sittingService.getSittingById(sittingid);
				   model.addAttribute("sitting", sitting);
					return "editSitting";
					}
					 					 
	   
//--Sitting Controller Ends-------------------------------------------------------------------------------------------------------------------------------------------------	

//		   @GetMapping("/download/{patientId}")
//		    public ResponseEntity<byte[]> downloadMedicalReport(@PathVariable int patientId) {
//		        try {
//		            byte[] medicalReportContent = patientInfoService.getMedicalReportById(patientId);
//
//		            HttpHeaders headers = new HttpHeaders();
//		            headers.setContentType(MediaType.APPLICATION_PDF);
//		            headers.setContentDispositionFormData("inline", "medical_report.pdf");
//
//		            return ResponseEntity.ok()
//		                .headers(headers)
//		                .body(medicalReportContent);
//		        } catch (IOException e) {
//		            // Handle the case where the medical report is not found
//		            return ResponseEntity.notFound().build();
//		        }
//		    }
					 

//					 @GetMapping("/download/{patientId}")
//					 public void downloadReport(@PathVariable int patientId, HttpServletResponse response) {
//					     PatientInfo patientinfo=patientInfoService.getPatientInfoById(patientId);
//					     String reportLocation = patientinfo.getReportlocation();	     
//					     if (reportLocation != null) {
//					         patientInfoService.downloadReportFromS3(reportLocation, response);
//					     }
//					 }
//					 

		   

}
