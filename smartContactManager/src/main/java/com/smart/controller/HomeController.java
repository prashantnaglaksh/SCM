package com.smart.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.smart.dao.UserRepository;
import com.smart.entity.User;
import com.smart.helper.Message;

@Controller
public class HomeController {
	@Autowired
	private UserRepository userRepository;
	
	@RequestMapping("/")
	public String home(Model model) {
		model.addAttribute("title","Home - Smart Contact Manager");
		return "home";
	}
	
	@RequestMapping("/about")
	public String about(Model model) {
		model.addAttribute("title","About - Smart Contact Manager");
		return "about";
	}
	
	@RequestMapping("/signup")
	public String signup(Model model) {
		model.addAttribute("title","Signup - Smart Contact Manager");
		model.addAttribute("user", new User());
		return "signup";
	}
	
	//handler for registering user
	@PostMapping("/do_register")
	public String registerUser (@ModelAttribute("user") User user,
			@RequestParam(value="agreement", defaultValue = "false") boolean agreement, Model model, HttpSession session){
		try {
			if(!agreement)
				throw new Exception("You Have Not Agreed Terms And Condition");
			user.setRole("Role_User");
			user.setEnabled(true);
			System.out.println("user " + user);
			System.out.println("agreement " +  agreement);
			User result = userRepository.save(user);
			
			model.addAttribute("user", new User());
			session.setAttribute("message", new Message("Sussfully Registerd !!", "alert-success"));
			return "signup";
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("user", user);
			session.setAttribute("message", new Message("Something Went Wrong !!" + e.getMessage(), "alert-error"));
			return "signup";
		}
	}

}
