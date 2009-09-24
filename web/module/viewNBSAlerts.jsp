<%@ include file="/WEB-INF/template/include.jsp" %>

<openmrs:require privilege="View Hello World" otherwise="/login.htm" redirect="/module/nbsmodule/viewHelloWorld.htm" />
	
<%@ include file="/WEB-INF/template/header.jsp" %>

<br/>

<%@ page import="org.openmrs.api.context.Context" %> 
<%@ page import="org.openmrs.Patient" %> 
<%@ page import="org.openmrs.module.nbsmodule.NBSService" %> 
<%@ page import="java.util.List" %> 
<%@ page import="org.openmrs.module.nbsmodule.NBSModuleResponse" %>
<%

	 NBSService nbs = (NBSService)Context.getService(NBSService.class);
	 String pid = request.getQueryString();
	 List<NBSModuleResponse> nbsResponses  = nbs.getResponses();
	 
	 for(NBSModuleResponse currResponse: nbsResponses)
	 {
	  out.print(currResponse.getAlertId()+"<BR/>");
	 }
    
%>

<%@ include file="/WEB-INF/template/footer.jsp" %>