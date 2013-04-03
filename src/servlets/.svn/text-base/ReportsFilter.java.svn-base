/**************************************************************************
 Copyright (c)  2011 Arizona State University
 The following code is the property of Arizona State University and
 the Arizona Board of Regents. Various other restrictions may apply.
 Unauthorized use of this software, in whole or parts thereof, is
 strictly prohibited.

 Contact email: softwareenterprise@asu.edu
        phone: 480 727 1373.

 $Revision$
 $LastChangedBy$
 $LastChangedDate$
 $HeadURL$

**************************************************************************/

package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import dao.ConnectionDAO;

import beans.FilterParameters;

import properties.PropertiesClass;

/**
 * Servlet implementation class ReportsFilter
 */
public class ReportsFilter extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private ConnectionDAO connection = new ConnectionDAO();

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		response.setContentType("text/html");
		
		response.setHeader("Expires", "0");
		response.setHeader("Cache-Control", "no-cache");
		response.setHeader("Pragma", "no-cache");
		
		HttpSession session = request.getSession(true);
		String userId = (String) session.getAttribute("admin");
		if (userId != null) {
		RequestDispatcher dispatcher = request
				.getRequestDispatcher("/static/ASU_Header.html");
		dispatcher.include(request, response);
		out.println(generateHtml(request));
		dispatcher = request.getRequestDispatcher("/static/ASU_Footer.html");
		dispatcher.include(request, response);
		} else {
			response.sendRedirect(response.encodeRedirectURL("Admin"));
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		FilterParameters filters = new FilterParameters();
		HttpSession session = request.getSession(true);
		
		filters.setDegreeProgram(request.getParameter("educationdropdown"));
		filters.setGpa(request.getParameter("gpadropdown"));
		filters.setGradYear(request.getParameter("gradyeardropdown"));
		filters.setEntCourses(request.getParameterValues("entCourses"));
		
		ArrayList<String> users = getUsersMatchingTheFilter(filters);
		
		if(users != null && users.size()==0)
		{
			String errors = "There are no users matching the given criteria. Please Try again!!";
			request.setAttribute("filterErrors", errors);
			doGet(request, response);
			return;
		}
		session.setAttribute("filters", filters);
		session.setAttribute("usersMatchingTheFilters", users);
		response.sendRedirect(response.encodeRedirectURL("Reports"));
		
	}
	
	private ArrayList<String> getUsersMatchingTheFilter(FilterParameters filters)
	{
		ArrayList<String> users = connection.getUsersMatchingTheFilter(filters);
		return users;
	}

	private ArrayList<String> getCourses() {
		ArrayList<String> courses = new ArrayList<String>();
		courses.add("Bachelor of Applied Computer Science");
		courses.add("Bachelor of Computer Systems - Embedded Systems Concentration");
		courses.add("Bachelor of Computer Systems - Hardware Concentration");
		courses.add("Bachelor of Applied Science");
		courses.add("Master of Computing Studies");
		courses.add("other undergrad");
		courses.add("other grad");
		return courses;
	}

	private ArrayList<String> getGPADropDown() {
		ArrayList<String> gpaDD = new ArrayList<String>();
		gpaDD.add("below 2.0");
		gpaDD.add("2.00 - 2.49");
		gpaDD.add("2.50 - 2.99");
		gpaDD.add("3.00 - 3.49");
		gpaDD.add("3.5 or above");
		return gpaDD;
	}

	private String generateHtml(HttpServletRequest request) {
		HttpSession session = request.getSession(true);
		session.removeAttribute("fromNsfccli");
		StringBuilder sb = new StringBuilder("");
		String errors = (String) request.getAttribute("filterErrors");
		String strTemp = "";
		String education = "";
		String gradYear = "";
		String gpa = "";
		String[] entCourses = null;
		
		FilterParameters filters = (FilterParameters)session.getAttribute("filters");
		
		if(errors != null && errors.length()>0)
		{
			education = request.getParameter("educationdropdown");
			gradYear = request.getParameter("gradyeardropdown");
			gpa = request.getParameter("gpadropdown");
			entCourses = request.getParameterValues("entCourses");
		}
		
		else if(filters == null)
			filters = new FilterParameters("","","",null);
		
		else
		{
		
		if(!filters.getDegreeProgram().trim().equals(""))
			education = filters.getDegreeProgram();
		if(!filters.getGradYear().trim().equals(""))
			gradYear = filters.getGradYear();
		if(!filters.getGpa().trim().equals(""))
			gpa = filters.getGpa();
		if(filters.getEntCourses() != null)
			entCourses = filters.getEntCourses();
		}		

		ArrayList<String> courses = getCourses();
		ArrayList<String> gpaDD = getGPADropDown();
		int cst315 = 0;
		int cst316 = 0;
		int cst415 = 0;
		int cst416 = 0;

		sb.append("<title>Reports Filter</title>");
		sb.append("<h1 class='title' align='center'>");
		sb.append(PropertiesClass.title);
		sb.append("</h1>");

		if (errors != null && errors.length() > 0) {
			sb.append("<table align='center'><tr><td><font color='red'>");
			sb.append(errors);
			sb.append("</font></td></tr></table>");
		}

		sb.append("<h3 id='pagetitle'>Reports Filters</h3>");
		String applicationContext = "";
		sb.append("<table align='right'><tr><td>");
		sb.append("<a href='"+applicationContext+"AdminLogout'>Logout</a>");
		sb.append("</td></tr></table>");
		sb.append("<a href='"+applicationContext+"AdminHome'>Home</a><br/><br/>");
		
		sb
				.append("<form id='filterfForm' name='filterfForm' action='' method='post'>");
		sb.append("<table>");
		sb.append("<tr><td>Degree Program:</td><td>");

		sb.append("<select name='educationdropdown' id='educationdropdown'>");
		sb.append("<option value='select'>Select</option>");
		if (isEmpty(education)) {
			for (int i = 0; i < courses.size(); i++) {
				sb.append("<option value='" + courses.get(i) + "'> "
						+ courses.get(i) + "</option>");
			}
		} else {
			for (int i = 0; i < courses.size(); i++) {
				strTemp = (String) courses.get(i);
				if (strTemp.equalsIgnoreCase(education.trim())) {
					sb.append("<option value='" + courses.get(i)
							+ "' selected='true'> " + courses.get(i)
							+ "</option>");
				} else {
					sb.append("<option value='" + courses.get(i) + "'> "
							+ courses.get(i) + "</option>");
				}
			}
		}
		sb.append("</select>");
		sb.append("</td></tr></table>");

		sb.append("<table><tr><td>Year of Graduation:</td><td>");
		sb.append("<select name='gradyeardropdown' id='gradyeardropdown'>");
		sb.append("<option value='select'>Select</option>");

		for (int index = 2004; index < 2016; index++) {
			strTemp = Integer.toString(index);
			if (strTemp.equalsIgnoreCase(gradYear)) {
				sb.append("<option value='" + index + "' selected='true'>"
						+ index + "</option>");
			} else {
				sb.append("<option value='" + index + "'>" + index
						+ "</option>");
			}
		}
		sb.append("</select>");
		sb.append("</td></tr>");

		sb.append("<tr><td>GPA:</td><td>");
		sb.append("<select name='gpadropdown' id='gpadropdown'>");
		sb.append("<option value='select'>Select</option>");
		if (isEmpty(gpa)) {
			for (int j = 0; j < gpaDD.size(); j++) {
				sb.append("<option value=' " + gpaDD.get(j) + "'>"
						+ gpaDD.get(j) + "</option>");
			}
		} else {
			for (int j = 0; j < gpaDD.size(); j++) {
				strTemp = (String) gpaDD.get(j);
				if (strTemp.trim().equalsIgnoreCase(gpa.trim())) {
					sb
							.append("<option value=' " + gpaDD.get(j)
									+ "' selected='true'>" + gpaDD.get(j)
									+ "</option>");
				} else {
					sb.append("<option value=' " + gpaDD.get(j) + "'>"
							+ gpaDD.get(j) + "</option>");
				}
			}
		}
		sb.append("</select>");
		sb.append("</td></tr></table><br>");

		if (entCourses != null) {
			for (int index = 0; index < entCourses.length; index++) {
				if (entCourses[index].equalsIgnoreCase("CST315"))
					cst315 = 1;
				else if (entCourses[index].equalsIgnoreCase("CST316"))
					cst316 = 1;
				else if (entCourses[index].equalsIgnoreCase("CST415"))
					cst415 = 1;
				else if (entCourses[index].equalsIgnoreCase("CST416"))
					cst416 = 1;
			}
		}

		sb
				.append("<table><tr><td>Software Enterprise courses:</td></tr></table>");
		sb.append("<table><tr><td>&nbsp</td><td>");
		if (cst315 == 1)
			sb
					.append("<input type='checkbox' name='entCourses' value='CST315' checked='true' >CST315</input>");
		else
			sb
					.append("<input type='checkbox' name='entCourses' value='CST315' >CST315</input>");
		sb.append("</td></tr>");
		sb.append("<tr><td>&nbsp</td><td>");
		if (cst316 == 1)
			sb
					.append("<input type='checkbox' name='entCourses' value='CST316' checked='true'>CST316</input>");
		else
			sb
					.append("<input type='checkbox' name='entCourses' value='CST316'>CST316</input>");
		sb.append("</td></tr>");
		sb.append("<tr><td>&nbsp</td><td>");
		if (cst415 == 1)
			sb
					.append("<input type='checkbox' name='entCourses' value='CST415' checked='true'>CST415</input>");
		else
			sb
					.append("<input type='checkbox' name='entCourses' value='CST415'>CST415</input>");
		sb.append("</td></tr>");
		sb.append("<tr><td>&nbsp</td><td>");
		if (cst416 == 1)
			sb
					.append("<input type='checkbox' name='entCourses' value='CST416' checked='true'>CST416</input>");
		else
			sb
					.append("<input type='checkbox' name='entCourses' value='CST416'>CST416</input>");
		sb.append("</td></tr>");
		sb.append("</table><br>");

		sb.append("<table><tr><td>");
		sb.append("<input type='submit' name='submit' value='Submit'>&nbsp");
		sb.append("</td></tr></table><br>");
		sb.append("</form>");
		return sb.toString();
	}

	private boolean isEmpty(String str) {
		if (str != null && str.length() > 0)
			return false;
		else
			return true;
	}

}
