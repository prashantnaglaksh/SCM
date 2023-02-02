package com.smart.controller;

import java.util.Random;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.service.EmailService;

@Controller
public class ForgotController {
	
	@Autowired
	private EmailService emailService;

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
			session.setAttribute("otp", "otp");
			return "verify_otp";
		}
		else {
			session.setAttribute("message", "Invalid email");
			return "forgot_email_form";
		}
	}
}
