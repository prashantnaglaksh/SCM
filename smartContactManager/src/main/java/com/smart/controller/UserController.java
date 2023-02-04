package com.smart.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.smart.dao.ContactRepository;
import com.smart.dao.UserRepository;
import com.smart.entity.Contact;
import com.smart.entity.User;
import com.smart.helper.Message;

@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ContactRepository contactRepository;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	//with the help of this annotation this attr is automativcalyy added to all the handelers
	@ModelAttribute
	public void addCommanData(Model model, Principal principal) {
		String userName = principal.getName();
		// fetching user info using userName i.e, emailId
		User user = userRepository.getUserByUserName(userName);
		model.addAttribute("user", user);
	}
	
	// user dashboard i.e, home handler
	@RequestMapping("/index")
	public String dashboard(Model model, Principal principal) {
		return "user/user_dashboard";
	}
	//open add form handeler
	@GetMapping("/add-contact")
	public String openAddContactForm(Model model) {
		model.addAttribute("title", "Add Contact");
		model.addAttribute("contact", new Contact());
		return "user/add_contact_form";
	}
	//processing add contact form
	@PostMapping("/process-contact")
	public String processContact(@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile file,	
			Principal principal, HttpSession session) {
		try {
			System.out.println("i am file " + file);
			String name = principal.getName();
			User user = this.userRepository.getUserByUserName(name);
			// processing and uploading file
			// here we are saving the image to our local folder and in database we are saving the url of the image
			if(file.isEmpty()){
				contact.setImage("defaultContactImg.png");
			}else {
				contact.setImage(file.getOriginalFilename());
				File savefile = new ClassPathResource("static/img").getFile();
				System.out.println("i am savefile" + savefile);
				Path path = Paths.get(savefile.getAbsolutePath() + File.separator + file.getOriginalFilename());
				System.out.println("i am savefile.getAbsolutePath()" + savefile.getAbsolutePath());
				System.out.println("i am File.separator" + File.separator);
				System.out.println("i am file.getOriginalFilename()" + file.getOriginalFilename());
				System.out.println("i am path " + path);
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				System.out.println("i am file.getInputStream() " + file.getInputStream());
			}
			
			contact.setUser(user);
			user.getContacts().add(contact);
			this.userRepository.save(user);
			System.out.println("contact added ");
			// success msg
			session.setAttribute("message", new Message("Your Contact is Added...", "success"));
		}catch(Exception e) {
			e.printStackTrace();
			//error msg
			session.setAttribute("message", new Message("Something Went Wrong...", "danger"));
		}
		
		//System.out.println("Contact :: " + contact);
		
		return "user/add_contact_form";
	}
	
	//show contact handeler
	//per page 5[n]
	//current page = 0[page]
	@GetMapping("/show-contacts/{page}")
	public String showContacts(@PathVariable("page") Integer page, Model model, Principal principal) {
		model.addAttribute("title", "View Contacts");
		
		//fetching contacts
		String userName = principal.getName();
		User user = this.userRepository.getUserByUserName(userName);
		int userId = user.getId();
		PageRequest pageRequest = PageRequest.of(page, 5);
		Page<Contact> contacts = this.contactRepository.findContactByUser(userId,pageRequest);
		model.addAttribute("contacts", contacts);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", contacts.getTotalPages());
		
		return "user/show_contacts";
		
	}
	
	//showing particular contact details
	@GetMapping("/{cId}/contact")
	public String showContactDetail(@PathVariable("cId") Integer cId, Model model, Principal principal) {
		
		Optional<Contact> contactOptional = this.contactRepository.findById(cId);
		Contact contact = contactOptional.get();
		//we are adding this if condition so another user not able to see the contact of another user as we have
		//contact id in our URL so he can change the id and can see the contact details
		String userName = principal.getName();
		User user = this.userRepository.getUserByUserName(userName);
		if(user.getId() == contact.getUser().getId())
			model.addAttribute("contact", contact);
		
		return "user/contact_detail";
		
	}
	
	@GetMapping("/delete/{cId}")
	public String deleteContact(@PathVariable("cId") Integer cId, Model model, HttpSession httpSession, Principal principal) {
		Optional<Contact> contactOptional =  this.contactRepository.findById(cId);
		Contact contact = contactOptional.get();
		//contact.setUser(null); // we are setting user as null as in Entitiy class we have specify CASCADE ALL true so here contact
							   // contact is link to the user and it wont let the contact to be deleted
		//this.contactRepository.delete(contact);
		String userOptional = principal.getName();
		User user = this.userRepository.getUserByUserName(userOptional);
		user.getContacts().remove(contact);
		this.userRepository.save(user);
		
		//httpSession.setAttribute("message", "Contact deleted Successfully");
		httpSession.setAttribute("message", new Message("Contact Deleted Successfully....", "success"));
		return "redirect:/user/show-contacts/0";
	}
	
	// open update form handeler
	@PostMapping("/update-contact/{cId}")
	public String updateform(@PathVariable("cId") Integer cId, Model model) {
		
		model.addAttribute("title", "Update Contact");
		Optional<Contact> contactOptional = this.contactRepository.findById(cId);
		Contact contact = contactOptional.get();
		
		model.addAttribute("contact", contact);
		
		return "user/update_form";
	}

	// upadte contact handeler
	@PostMapping("/process-update")
	public String updateHandler(@ModelAttribute Contact contact,  @RequestParam("profileImage") MultipartFile file, Model model,
			HttpSession httpSession, Principal principal) {
		Contact oldContact = this.contactRepository.findById(contact.getcId()).get(); // finfById vs getById ?
		try {
			if(!file.isEmpty()) {
				// file work 
				// re write
				// delete old photo
				File deleteFile = new ClassPathResource("static/img").getFile();
				File file1 = new File(deleteFile, oldContact.getImage());
				file1.delete();
				
				// update new photo
				File savefile = new ClassPathResource("static/img").getFile();
				Path path = Paths.get(savefile.getAbsolutePath() + File.separator + file.getOriginalFilename());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				contact.setImage(file.getOriginalFilename());
			}else {
				contact.setImage(oldContact.getImage());
			}
			String userName = principal.getName();
			User user = this.userRepository.getUserByUserName(userName);
			contact.setUser(user);
			this.contactRepository.save(contact);
		 httpSession.setAttribute("message", new Message("Your Contact Is Updated...", "success"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "redirect:/user/" + contact.getcId() + "/contact";
		//return "user/update_form";
	}
	
	//your profile handeler
	@GetMapping("/profile")
	public String yourProfile(Model model) {
		model.addAttribute("title", "Profile Page");
		return "user/profile";
	}
	
	//open setting handeler
	@GetMapping("/setting")
	public String openSetting() {
		return "user/setting";
	}
	//change password handeler
	@PostMapping("/change-password")
	public String changePassword(@RequestParam("oldPassword") String oldPassword, @RequestParam("newPassword") String newPassword,
			Principal principal, HttpSession httpSession) {
		
		String userName = principal.getName();
		User user = this.userRepository.getUserByUserName(userName);
		if(this.bCryptPasswordEncoder.matches(oldPassword, user.getPassword())) {
			user.setPassword(this.bCryptPasswordEncoder.encode(newPassword));
			this.userRepository.save(user);
			httpSession.setAttribute("message", new Message("Your Password Is Updated...", "success"));
		}else {
			httpSession.setAttribute("message", new Message("Your oldPassword Password Is InCorrect...", "danger"));
			return "redirect:/user/setting";
		}
		
		return "redirect:/user/index";
	}
	
	// payment handler
	@PostMapping("/create_order")
	@ResponseBody
	public String createOrder(@RequestBody Map<String, Object> data) throws Exception {
		try {
			String amount = (String) data.get("amount");
			int lAmount = Integer.valueOf(amount);
			System.out.println("i am here ");
			RazorpayClient client = new RazorpayClient("id", "key");
			
			  JSONObject orderRequest = new JSONObject();
			  orderRequest.put("amount", lAmount*100); // amount in the smallest currency unit
			  orderRequest.put("currency", "INR");
			  orderRequest.put("receipt", "order_rcptid_11");

			  Order order = client.Orders.create(orderRequest);
			  // we should save order details in our db
			  System.out.println(order);
			  return order.toString();
			  
			} catch (Exception e) {
			  // Handle Exception
			  System.out.println(e.getMessage());
			  return e.getMessage();
			}
		//return order.toString();
	}
	
	
}
