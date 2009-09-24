<%@ include file="/WEB-INF/template/include.jsp" %>

<openmrs:require privilege="Add Hello World Response" otherwise="/login.htm" redirect="/module/helloworld/addResponse.form" />
	
<%@ include file="/WEB-INF/template/header.jsp" %>

<br/>

<form method="post">

	<spring:message code="helloworld.response"/>: 
	<input type="text" name="response" value="" />
	
	<br/><br/>
	
	<input type="submit" value="<spring:message code="general.save" />" />

</form>

<h3><spring:message code="helloworld.responses"/></h3>

<c:forEach items="${responses}" var="responseObj">

	${responseObj.response} <br/>

</c:forEach>

<br/>

<%@ include file="/WEB-INF/template/footer.jsp" %>