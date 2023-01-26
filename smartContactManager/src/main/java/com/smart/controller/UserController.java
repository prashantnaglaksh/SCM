package com.smart.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.dao.UserRepository;
import com.smart.entity.Contact;
import com.smart.entity.User;
import com.smart.helper.Message;

@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private UserRepository userRepository;
	
	//with the help of this annotation this attr is automativcalyy added to all the handelers
	@ModelAttribute
	public void addCommanData(Model model, Principal principal) {
		String userName = principal.getName();
		System.out.println("i am userName " + userName);
		// fetching user info using userName i.e, emailId
		User user = userRepository.getUserByUserName(userName);
		System.out.println("i am user " + user);
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
			// processing and uploadin file
			if(file.isEmpty()){
				
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

}
