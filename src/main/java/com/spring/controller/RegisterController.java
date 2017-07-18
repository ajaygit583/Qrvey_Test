package com.spring.controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class RegisterController {
	
	private static final String DELIMITER = ":::";
	
	private @Autowired ServletContext servletContext;
	

	@RequestMapping(value = "/register", method = RequestMethod.GET)
	public String init(Model model) {
		model.addAttribute("msg", "Please Register");
		return "register";
	}
	
	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public String submit(Model model, @ModelAttribute("loginBean") LoginBean loginBean) throws Exception {
		String userName = loginBean.getUserName();
		String password = loginBean.getPassword();
		if( userName == null || userName.trim().isEmpty() ||
				password == null || password.trim().isEmpty()) {
			model.addAttribute("error", "Invalid Details");
			return "register";
		}
		if(!userName.matches("[a-zA-Z0-9]*") || !password.matches("[a-zA-Z0-9]*")){
			model.addAttribute("error", "Username/password should only contain alphabets/numbers!!!");
			return "register";
		}
		InputStream inputStream = servletContext.getResourceAsStream("/WEB-INF/classes/passswords.txt");
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        BufferedWriter bw = null;
		try {
		    StringBuilder sb = new StringBuilder();
		    String line = br.readLine();

		   
		    while (line != null) {
		    	String[] userDetails = line.split(DELIMITER);
		        String existingUser = userDetails[0];
		        if(userName.trim().equals(existingUser)){
		        	model.addAttribute("error", "User already exists");
		        	return "register";
		        }
		        
		        sb.append(line);
		        sb.append(System.lineSeparator());
		        line = br.readLine();
		    }
		    String everything = sb.toString();
		    everything = everything + loginBean.getUserName()+DELIMITER+loginBean.getPassword();
		   // everything = everything + ":::" + new Date().toString();
		    String path  = servletContext.getRealPath("/WEB-INF/classes/");

		    FileWriter fw = new FileWriter(path + "/passswords.txt");
		    bw = new BufferedWriter(fw);
		    bw.write(everything);
		   
		} finally {
			if(br != null){
				br.close();
		    
			}
			if(bw != null) {
				bw.close();
			}
			if(inputStream != null){
				inputStream.close();
			}
		}
		System.out.println("In register method");
			return "redirect:/login";
		
	}
	
}
