package com.smart.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.smart.dao.UserRepository;
import com.smart.entity.User;

@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private UserRepository userRepository;
	
	@RequestMapping("/index")
	public String dashboard(Model model, Principal principal) {
		String userName = principal.getName();
		System.out.println("i am userName " + userName);
		// fetching user info using userName i.e, emailId
		User user = userRepository.getUserByUserName(userName);
		System.out.println("i am user " + user);
		model.addAttribute("user", user);
		return "user/user_dashboard";
	}

}
