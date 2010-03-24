package org.openmrs.module.nbsmodule.web;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.nbsmodule.NBSModuleResponse;
import org.openmrs.module.nbsmodule.NBSService;

public class NBSServlet extends HttpServlet {

	private static final long serialVersionUID = 1239820102033L;
	private Log log = LogFactory.getLog(this.getClass());

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		log.debug("doGet");
		PrintWriter writer = response.getWriter();
		writer.print("New Born Screen!! (from Servlet doGet)<br/>");
		String pid = request.getQueryString();
	
		try {
			Object o = Context.getService(NBSService.class);
			log.debug("Got service object: " + o);
			NBSService service = (NBSService)o;
			NBSModuleResponse r = service.getNBSModuleResponse(pid);
			writer.print("Response: " + r.getFormInstanceId() + "\n");
			}
		catch (APIException apiexception) {
			apiexception.printStackTrace(writer);
		}	
		
	}
		
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		log.debug("doPost");
		response.getWriter().print("New Born Screening!!! (from Servlet doPost)");
	}

}
