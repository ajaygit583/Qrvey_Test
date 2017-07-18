package com.spring.controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class LoginController {
	
	private static final String DELIMITER = ":::";
	
	private @Autowired ServletContext servletContext;
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String homePage(Model model) {
		return "redirect:/login";
	}
	
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String init(Model model) {
		model.addAttribute("msg", "Please Enter Your Login Details");
		return "login";
	}
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public String submit(Model model, @ModelAttribute("loginBean") LoginBean loginBean) throws Exception {
		//FileSystemResource resource = new FileSystemResource("/WEB-INF/classes/passswords.txt");
		//resource.getFile();
		//BufferedReader br = new BufferedReader(new FileReader(resource.getFile()));
		InputStream inputStream = servletContext.getResourceAsStream("/WEB-INF/classes/passswords.txt");
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        BufferedWriter bw = null;
        boolean foundUser = false;
        String lastLogin = null;
		try {
		    StringBuilder sb = new StringBuilder();
		    String line = br.readLine();

		   
		    while (line != null) {
		        
		        String[] userDetails = line.split(DELIMITER);
		        String userName = userDetails[0];
		        String password = userDetails[1];
		        //String lastLogin = userDetails[2];
		        if (loginBean != null && loginBean.getUserName() != null & loginBean.getPassword() != null) {
		        	if (loginBean.getUserName().equals(userName) && loginBean.getPassword().equals(password)) {
		        		System.out.println("Found");
		        		foundUser = true;
		        		if(userDetails.length == 3) {
		        			lastLogin = userDetails[2];
		        		}else{
		        			lastLogin = "This is the first time you are logggin in!!!";
		        		}
		        		line = userName + DELIMITER + password + DELIMITER + new Date().toString();
		        	}
		        }
		        sb.append(line);
		        sb.append(System.lineSeparator());
		        line = br.readLine();
		    }
		    String everything = sb.toString();
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
		
		if (foundUser) {
			model.addAttribute("lastLogin", lastLogin);
				return "success";
			 
		} else {
			model.addAttribute("error", "Invalid Details");
			return "login";
		}
	}
}
