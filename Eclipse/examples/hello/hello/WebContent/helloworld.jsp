<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import = "hello.HelloWorld" %>
<%@ page import = "hello.SerialTest" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>Hello world incredible</title>
	</head>
	<body>
	
		<%-- This is a JSP Comment before JSP Scriplet --%>
		<%
			/*
			//Prints out to console
			System.out.println("Hello World in Console!");
		 
			//Prints out to HTML page
			out.println("<h1>Hello World!</h1>");*/
		%>
		  
		<% 
		
			
			out.println ("<h1>La ostia de titulo...</h1>");			
			
			HelloWorld t = new HelloWorld(out);
			
			try {Thread.sleep(3000);} catch (InterruptedException ie) {}
			
			t.connectionError();
		
			/*
		    while (!t.isFinished())
		    {
		    	try {Thread.sleep(100);} catch (InterruptedException ie) {}		    	
		    }*/
		    
		    out.println ("website is loaded OK from Arduino.");
			
			//t.test("Joy");
		%>
		
	</body>
</html>