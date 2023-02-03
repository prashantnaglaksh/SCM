package com.smart.controller;

import java.util.Random;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dao.UserRepository;
import com.smart.entity.User;
import com.smart.service.EmailService;

@Controller
public class ForgotController {
	
	@Autowired
	private EmailService emailService;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	Random random = new Random(1000);
	//forgot password handeler
	@RequestMapping("/forgot")
	public String forgotEmailForm() {
		return "forgot_email_form";
	}
	
	@PostMapping("/send-otp")
	public String sendOTP(@RequestParam("email") String email,HttpSession session) {
		
		int otp = random.nextInt(9999);
		System.out.println("i am otp " + otp);
		boolean lFlag = this.emailService.sendOTP("Smart Contact Mnager", String.valueOf(otp), email);
		if(lFlag) {
			session.setAttribute("otp", otp);
			session.setAttribute("email", email);
			
			return "verify_otp";
		}
		else {
			session.setAttribute("message", "Invalid email");
			return "forgot_email_form";
		}
	}
	
	// verify otp handelere
	@PostMapping("/verify-otp")
	public String verifyOTP(@RequestParam("otp") int otp,HttpSession session) {
		int myOtp = (int)session.getAttribute("otp");
		String email = (String) session.getAttribute("email");
		if(myOtp == otp) {
			User user = this.userRepository.getUserByUserName(email);
			if(user == null) {
				session.setAttribute("message", "No User With This Email");
				return "verify_otp";
			}else {
				return "password_change_form";
			}
		}else {
			session.setAttribute("message", "Wrong OTP");
			return "verify_otp";
		}
	}
	
	// change password handeler
	@PostMapping("/change-password")
	public String changePassword(@RequestParam("newPassword") String newPassword, HttpSession session) {
		String email = (String) session.getAttribute("email");
		User user = this.userRepository.getUserByUserName(email);
		user.setPassword(this.bCryptPasswordEncoder.encode(newPassword));
		this.userRepository.save(user);
		
		return "redirect:signin?change=password change successfully...!";
	}
	
}
