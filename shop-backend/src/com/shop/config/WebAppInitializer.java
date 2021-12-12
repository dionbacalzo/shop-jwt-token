package com.shop.config;

import java.io.File;
import java.nio.file.Paths;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletContext;  
import javax.servlet.ServletException;  
import javax.servlet.ServletRegistration.Dynamic;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.WebApplicationInitializer;  
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;  
import org.springframework.web.servlet.DispatcherServlet;

import com.shop.util.PropertyUtil;  
public class WebAppInitializer implements WebApplicationInitializer {
	
	protected static final Logger logger = LogManager.getLogger(WebAppInitializer.class);
	
	private String TMP_FOLDER = PropertyUtil.getProperty("upload.temp.path"); 
	private int MAX_UPLOAD_SIZE = Integer.parseInt(PropertyUtil.getProperty("max.upload.size"));
	
	public void onStartup(ServletContext servletContext) throws ServletException {  
        AnnotationConfigWebApplicationContext ctx = new AnnotationConfigWebApplicationContext();          
        ctx.register(AppConfig.class);
        ctx.setServletContext(servletContext);    
        Dynamic dynamic = servletContext.addServlet("dispatcher", new DispatcherServlet(ctx));
        dynamic.addMapping("/");  
        dynamic.setLoadOnStartup(1);
        
        // file upload path, used user home directory for platform independence
        String filePath = System.getProperty("user.home")+File.separator+Paths.get(TMP_FOLDER);
        logger.info("file temporary upload path " + filePath);
        //create directory if it doesn't exist 
        new File(filePath).mkdirs();
        
        //file upload configuration
        MultipartConfigElement multipartConfigElement = new MultipartConfigElement(filePath, 
                MAX_UPLOAD_SIZE, MAX_UPLOAD_SIZE * 2, MAX_UPLOAD_SIZE / 2);
               
        dynamic.setMultipartConfig(multipartConfigElement);
        
   }  
} 
