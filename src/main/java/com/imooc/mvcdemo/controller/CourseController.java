package com.imooc.mvcdemo.controller;


import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;














import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.imooc.mvcdemo.model.Course;
import com.imooc.mvcdemo.service.CourseService;


@Controller
@RequestMapping("/courses")
// /courses/**
public class CourseController {
	
	private static Logger log = LoggerFactory.getLogger(CourseController.class);

	private CourseService courseService;

	@Autowired
	public void setCourseService(CourseService courseService) {
		this.courseService = courseService;
	}
	
	//本方法将处理/courses/view?courseId=123,@RequestParam("courseId")用于标记传过来的参数
	//本方法将处理 /courses/view?courseId=123 形式的URL
		@RequestMapping(value="/view", method=RequestMethod.GET)
		public String viewCourse(@RequestParam("courseId") Integer courseId,
				Model model) {
			
			System.out.println("!!!!");
			log.debug("In viewCourse, courseId = {}", courseId);
			Course course = courseService.getCoursebyId(courseId);
			model.addAttribute(course);
			return "course_overview";
		}
		
		// /courses/view2/{courseId}
		@RequestMapping(value="/view2/{courseId}", method=RequestMethod.GET)
		public String viewCourse2(@PathVariable("courseId")Integer courseId,Map<String,Object> model){
			log.debug("In viewCourse2,courseId = {}",courseId);
			Course course = courseService.getCoursebyId(courseId);
			model.put("course", course);
			return "course_overview";
		}
		
		// /courses/view3?courseId=456形式的URL
		@RequestMapping("view3")
		public String viewCourse3(HttpServletRequest request){
			Integer courseId = Integer.valueOf(request.getParameter("courseId"));
			log.debug("In viewCourse3,courseId = {}",courseId);
			Course course = courseService.getCoursebyId(courseId);
			request.setAttribute("course", course);
			return "course_overview";
		}
		
		@RequestMapping(value="/admin",method=RequestMethod.GET,params="add")
		public String createCourse(){
			return "course_admin/edit";
		}
		
		
		@RequestMapping(value="/save",method=RequestMethod.POST)
		public String doSave(@ModelAttribute Course course){
			log.debug("Info Course:");
			log.debug(ReflectionToStringBuilder.toString(course));
			//在此进行业务操作，比如数据库持久化操作
			course.setCourseId(123);
			return "redirect:view2/"+course.getCourseId();
		}
		
		@RequestMapping(value="/upload",method=RequestMethod.GET)
		public String showUploadPage(){
			return "course_admin/file";
		}
		
		@RequestMapping(value="/doUpload",method=RequestMethod.POST)
		//将表单file.jsp中的<input type="file" name="file"/>的name="file"@RequestParam("file") 
		//name="file"与@RequestParam("file")进行关联
		public String doUpLoadFile(@RequestParam("file") MultipartFile file) throws IOException{
			if(!file.isEmpty()){
				log.debug("Process file:{}",file.getOriginalFilename());
				FileUtils.copyInputStreamToFile(file.getInputStream(), new File("D:\\Workspaces\\MyEclipse Professional 2014\\imooc\\spring-mvc-study\\temp\\imooc",System.currentTimeMillis()+file.getOriginalFilename()));
			}
			return "success";
		}
		
		@RequestMapping(value="/{courseId}",method=RequestMethod.GET)
		public @ResponseBody Course getCourseInJson(@PathVariable Integer courseId){
			return courseService.getCoursebyId(courseId);
		}
		
		//不需要引入@ResponseBody
		@RequestMapping(value="/jsontype/{courseId}",method=RequestMethod.GET)
		public ResponseEntity<Course> getCourseInJson2(@PathVariable Integer courseId){
			Course course = courseService.getCoursebyId(courseId);
			return new ResponseEntity<Course>(course,HttpStatus.OK);
		}
}
