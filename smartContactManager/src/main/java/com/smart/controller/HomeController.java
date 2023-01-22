package com.smart.controller;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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
	private BCryptPasswordEncoder passwordEncoder;
	
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
	public String registerUser (@Valid @ModelAttribute("user") User user,BindingResult res,
			@RequestParam(value="agreement", defaultValue = "false") boolean agreement, Model model, HttpSession session){
		try {
			if(!agreement)
				throw new Exception("You Have Not Agreed Terms And Condition");
			if(res.hasErrors()) {
				model.addAttribute("user", user);
				return "signup";
			}
			
			user.setRole("ROLE_USER");
			user.setEnabled(true);
			user.setPassword(passwordEncoder.encode(user.getPassword()));
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
	
	// handeler for custom login
	@GetMapping("/signin")
	public String customLogin(Model model) {
		model.addAttribute("title", "Login Page");
		return "login";
	}
	
	@ResponseBody
	@GetMapping("/dummy")
	public String dummy() {
		User user = userRepository.getUserByUserName("");
		return user.getName();
	}

}
