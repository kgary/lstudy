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
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import dao.ConnectionDAO;

import properties.PropertiesClass;
import util.LoggerUtil;

/**
 * @author Srikesh Mandala(smandal2@exchange.asu.edu)
 *
 * The Registration servlet is registration page
 * shown to user
 */
public class Registration extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private ConnectionDAO connection = new ConnectionDAO();
	ArrayList questionsList = new ArrayList();
	private static Logger logger = LoggerUtil.getClassLogger();

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(true);
		String email = request.getParameter("token");
		PrintWriter out = response.getWriter();
		response.setContentType("text/html");
		RequestDispatcher dispatcher = request
				.getRequestDispatcher("/static/ASU_Header.html");
		dispatcher.include(request, response);
		if (email != null) {
			email = decodeEmailAddress(email);
			if(email ==null)
			{
				out.println(generateNoEmailHtml());
				dispatcher = request.getRequestDispatcher("/static/ASU_Footer.html");
				dispatcher.include(request, response);
				return;
			}
			session.setAttribute("email1", email);
			if (!email.equals("")) {
				if (checkIfUserHasAlreadyAccessed(email.trim())) {
					logger
							.info("user has already accessed-displaying error message");
					request.setAttribute("asUserAlreadyAccessed", "true");
				}
				updateEmail(email);
				out.println(generateHtml(request));
			}
		}
		else
		{
			out.println(generateNoEmailHtml());
		}
		dispatcher = request.getRequestDispatcher("/static/ASU_Footer.html");
		dispatcher.include(request, response);
	}
	
	private String generateNoEmailHtml()
	{
		StringBuffer sb = new StringBuffer("");
		sb.append("<title>Registration</title>");
		sb.append("<h1 class='title' align='center'>");
		sb.append(PropertiesClass.title);
		sb.append("</h1>");
		sb.append("<br><table><tr><td>Please ensure that complete URL sent to you in the email" +
				" is typed in the address bar." +
				"</td></tr></table><br>");
		
		return sb.toString();
	}
	
	private void updateEmail(String email) {
		try {
			connection.updateTrackedEmail(email);
		} catch (Exception ex) {
			logger.error("Unable to update tracked email exception "
					+ ex.getMessage());
		}
	}

	private boolean checkIfUserHasAlreadyAccessed(String email) {
		return connection.checkIfUserHasAlreadyAccessed(email);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(true);

		
		String email = (String)session.getAttribute("email");
		String email1 = (String)session.getAttribute("email1");
		if(email != null && email1 != null)
		{
			if(email1.trim().equalsIgnoreCase(email.trim()))
			{
				logger
				.info("user has already registered. displaying the error message.");
				request.setAttribute("registartionErrors", "You have already registered. "
				+"<a href='LandingPageForUser'>Click Here</a>"
				+" to goto LandingPage page");
				doGet(request, response);
				return;
			}
		}
		String errors = validateDetails(request);
		if (!isEmpty(errors)) {
			request.setAttribute("registartionErrors", errors);
			doGet(request, response);
			return;
		}
		int i = registerUser(request);
		String errors1 = (String) session.getAttribute("registerTheUserErrors");
		logger.info("user registartion status: " + errors);
		if (!isEmpty(errors1)) {
			if (errors1.equalsIgnoreCase("Registration succesfull")) {
				logger
						.info("user registered succesfully-redirecting to PostRegistration page");
				session.setAttribute("userAuthenticated", "true");
				session.setAttribute("registartionsuccess",
						"Registration is successfull.");
				response
						.sendRedirect(response
								.encodeRedirectUrl("PostRegistration"));
			} else {
				request.setAttribute("registartionErrors", errors1);
				doGet(request, response);
				return;
			}
		}

	}
	
	private String decodeEmailAddress(String txtInHex)
	{
		byte [] txtInByte = new byte [txtInHex.length() / 2];
		int j = 0;
		try
		{
			for (int i = 0; i < txtInHex.length(); i += 2)
			{
				txtInByte[j++] = Byte.parseByte(txtInHex.substring(i, i + 2), 16);
			}
		}
		catch(Exception ex)
		{
			logger.error("exception in method decodeEmailAddress"+ex.getMessage());
			return null;
		}
		String txt = new String(txtInByte);
		System.out.println(txt);
		return txt;
	}

	private int registerUser(HttpServletRequest request) {
		return connection.registerUser(request);
	}

	private String validateDetails(HttpServletRequest request) {
		StringBuilder errorsSB = new StringBuilder("");
		// personal details
		String education = request.getParameter("educationdropdown");
		String yearOfGraduation = request.getParameter("gradyeardropdown");
		String gpa = request.getParameter("gpadropdown");
		String[] entCourses = request.getParameterValues("entCourses");
		// secret questions
		String question1 = request.getParameter("q1dropdown");
		String question2 = request.getParameter("q2dropdown");
		String question3 = request.getParameter("question3");
		String ans1 = request.getParameter("ans1");
		String ans2 = request.getParameter("ans2");
		String ans3 = request.getParameter("ans3");
		if (isEmpty(ans1)) {
			errorsSB.append("Please provide answer for question 1<br>");
		} else if (!isAnswerValid(ans1)) {
			errorsSB.append("Answer 1 can have alphabets and digits only<br>");
		}
		if (isEmpty(ans2)) {
			errorsSB.append("Please provide answer for question 2<br>");
		} else if (!isAnswerValid(ans2)) {
			errorsSB.append("Answer 2 can have alphabets and digits only<br>");
		}
		if (isEmpty(ans3)) {
			errorsSB.append("Please provide answer for question 3<br>");
		} else if (!isAnswerValid(ans3)) {
			errorsSB.append("Answer 3 can have alphabets and digits only<br>");
		}
		if (question1.equalsIgnoreCase("Select")) {
			errorsSB.append("Please select question1 <br>");
		}
		if (question2.equalsIgnoreCase("Select")) {
			errorsSB.append("Please select question2 <br>");
		}
		if (isEmpty(question3)) {
			errorsSB.append("Please select question3 <br>");
		}
		if (isEmpty(errorsSB.toString())) {
			if (question1.equalsIgnoreCase(question2))
				errorsSB.append("Both the security questions are same."
						+ " Please select 2 different questions");
		}
		if (education.equalsIgnoreCase("select")) {
			errorsSB.append("Please choose education<br>");
		}
		if (yearOfGraduation.equalsIgnoreCase("select")) {
			errorsSB.append("Please choose year of Graduation<br>");
		}
		if (gpa.equalsIgnoreCase("select")) {
			errorsSB.append("Please choose GPA<br>");
		}
		
		return errorsSB.toString();
	}

	private void getQuestionFromDB() {
		questionsList = connection.getQuestionsFromDb();
	}

	private boolean isEmpty(String str) {
		if (str != null && str.length() > 0)
			return false;
		else
			return true;
	}

	private ArrayList getCourses() {
		ArrayList courses = new ArrayList();
		courses.add("Bachelor of Applied Computer Science");
		courses
				.add("Bachelor of Computer Systems - Embedded Systems Concentration");
		courses.add("Bachelor of Computer Systems - Hardware Concentration");
		courses.add("Bachelor of Applied Science");
		courses.add("Master of Computing Studies");
		courses.add("other undergrad");
		courses.add("other grad");
		return courses;
	}

	private ArrayList getGPADropDown() {
		ArrayList gpaDD = new ArrayList();
		gpaDD.add("below 2.0");
		gpaDD.add("2.00 - 2.49");
		gpaDD.add("2.50 - 2.99");
		gpaDD.add("3.00 - 3.49");
		gpaDD.add("3.5 or above");
		return gpaDD;
	}

	private ArrayList getWorkDropDown() {
		ArrayList work = new ArrayList();
		work.add("0 hours/week");
		work.add("part-time(less than 30hrs/week)");
		work.add("full-time(more than 30hrs/week)");
		return work;
	}

	private ArrayList getWorkFieldDropDown() {
		ArrayList workField = new ArrayList();
		workField.add("N/A (Not Applicable)");
		workField.add("Software Engineering/Developer");
		workField.add("Software Engineering/Non-Development");
		workField.add(" Other Computing related");
		workField.add("Other Technology related");
		workField.add("Non Technology related");
		return workField;
	}

	private String generateHtml(HttpServletRequest request) {
		HttpSession session = request.getSession(true);
		ArrayList courses = getCourses();
		ArrayList gpaDD = getGPADropDown();
		ArrayList work = getWorkDropDown();
		ArrayList workField = getWorkFieldDropDown();
		String[] entCourses = request.getParameterValues("entCourses");
		String strTemp = "";
		int cst315 = 0;
		int cst316 = 0;
		int cst415 = 0;
		int cst416 = 0;
		StringBuilder sb = new StringBuilder("");
		sb.append("<title>Registration</title>");
		sb.append("<h1 class='title' align='center'>");
		sb.append(PropertiesClass.title);
		sb.append("</h1>");
		
		sb.append("<table align='center'><tr><td>");
		sb.append("If you already have 7 character ID please ");
		sb.append("<a href='Login'>Click Here</a>");
		sb.append(" to goto login page");
		sb.append("</td></tr></table><br>");
		
		String hasUserAccessed = (String) request
				.getAttribute("asUserAlreadyAccessed");
		String errors = (String) request.getAttribute("registartionErrors");
		if(errors ==null || errors.length()==0)
		{
			if (hasUserAccessed != null && hasUserAccessed.length() > 0) {
				sb.append("<table align='center'><tr><td>");
				sb.append("You might have already registered. If yes ");
				sb.append("<a href='Login'>Click Here</a>");
				sb.append(" to goto login page");
				sb.append("</td></tr></table>");
			}
		}
		
		if (errors != null && errors.length() > 0) {
			sb.append("<table align='center'><tr><td><font color='red'>");
			sb.append(errors);
			sb.append("</font></td></tr></table>");
		}
		sb.append("<h3 id='pagetitle'>New User - Registration </h3>");

		sb
				.append("<form id='registrationForm' name='registrationForm' action='' method='post'>");
		sb.append("<table>");
		sb.append("<tr><td>Degree Program:</td><td>");

		sb.append("<select name='educationdropdown' id='educationdropdown'>");
		sb.append("<option value='select'>Select</option>");
		if (isEmpty(request.getParameter("educationdropdown"))) {
			for (int i = 0; i < courses.size(); i++) {
				sb.append("<option value='" + courses.get(i) + "'> "
						+ courses.get(i) + "</option>");
			}
		} else {
			for (int i = 0; i < courses.size(); i++) {
				strTemp = (String) courses.get(i);
				if (strTemp.equalsIgnoreCase(request
						.getParameter("educationdropdown"))) {
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
			if(strTemp.equalsIgnoreCase(request.getParameter("gradyeardropdown")))
			{
				sb.append("<option value='" + index + "' selected='true'>" + index + "</option>");
			}
			else
			{
				sb.append("<option value='" + index + "'>" + index + "</option>");
			}
		}
		sb.append("</select>");
		sb.append("</td></tr>");
		sb.append("<tr><td>GPA:</td><td>");
		sb.append("<select name='gpadropdown' id='gpadropdown'>");
		sb.append("<option value='select'>Select</option>");
		if (isEmpty(request.getParameter("gpadropdown"))) {
			for (int j = 0; j < gpaDD.size(); j++) {
				sb.append("<option value=' " + gpaDD.get(j) + "'>"
						+ gpaDD.get(j) + "</option>");
			}
		} else {
			String str1 = request.getParameter("gpadropdown");
			for (int j = 0; j < gpaDD.size(); j++) {
				strTemp = (String) gpaDD.get(j);
				if (strTemp.trim().equalsIgnoreCase(str1.trim())) {
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
		
		
		
		if(entCourses != null)
		{
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
				.append("<table><tr><td>Which of the following Software Enterprise courses have you taken:</td></tr></table>");
		sb.append("<table><tr><td>&nbsp</td><td>");
		if(cst315 ==1)
			sb
			.append("<input type='checkbox' name='entCourses' value='CST315' checked='true' >CST315</input>");	
		else
		sb
				.append("<input type='checkbox' name='entCourses' value='CST315' >CST315</input>");
		sb.append("</td></tr>");
		sb.append("<tr><td>&nbsp</td><td>");
		if(cst316 ==1)
		sb
				.append("<input type='checkbox' name='entCourses' value='CST316' checked='true'>CST316</input>");
		else
			sb
			.append("<input type='checkbox' name='entCourses' value='CST316'>CST316</input>");
		sb.append("</td></tr>");
		sb.append("<tr><td>&nbsp</td><td>");
		if(cst415 ==1)
			sb
			.append("<input type='checkbox' name='entCourses' value='CST415' checked='true'>CST415</input>");
		else
		sb
				.append("<input type='checkbox' name='entCourses' value='CST415'>CST415</input>");
		sb.append("</td></tr>");
		sb.append("<tr><td>&nbsp</td><td>");
		if(cst416 ==1)
			sb
			.append("<input type='checkbox' name='entCourses' value='CST416' checked='true'>CST416</input>");
		else
		sb
				.append("<input type='checkbox' name='entCourses' value='CST416'>CST416</input>");
		sb.append("</td></tr>");
		sb.append("</table><br>");
		sb.append("<table><tr><td>");
		sb
				.append("For security purposes and to preserve your anonymity we ask you 3 secret questions");
		sb.append("</td></tr></table>");
		sb.append(generateQuestionsHtml(request));
		return sb.toString();
	}

	private String generateQuestionsHtml(HttpServletRequest request) {
		questionsList.clear();
		getQuestionFromDB();
		StringBuilder sb = new StringBuilder("");
		String strTemp = "";
		sb.append("<table>");
		sb.append("<tr><td>Question1:</td><td>");
		sb.append("<select name='q1dropdown' id='q1dropdown'>");
		sb.append("<option value='select'>Select</option>");
		if (isEmpty(request.getParameter("q1dropdown"))) {
			for (int i = 0; i < questionsList.size(); i++) {
				sb.append("<option value='" + (String) questionsList.get(i)
						+ "'>" + (String) questionsList.get(i) + "</option>");
			}
		} else {
			for (int i = 0; i < questionsList.size(); i++) {
				strTemp = (String) questionsList.get(i);
				if (strTemp
						.equalsIgnoreCase(request.getParameter("q1dropdown"))) {
					sb.append("<option value='" + (String) questionsList.get(i)
							+ "' selected='true'>"
							+ (String) questionsList.get(i) + "</option>");
				} else {
					sb.append("<option value='" + (String) questionsList.get(i)
							+ "'>" + (String) questionsList.get(i)
							+ "</option>");
				}
			}
		}
		sb.append("</select>");
		sb.append("</td></tr>");

		sb.append("<tr><td>Answer:</td><td>");
		if (isEmpty(request.getParameter("ans1"))) {
			sb.append("<input type='text' size='30' name='ans1'>&nbsp");
		} else {
			sb.append("<input type='text' size='30' name='ans1' value='"
					+ request.getParameter("ans1") + "'>&nbsp");
		}
		sb.append("</td></tr>");

		sb.append("<tr><td>Question2:</td><td>");
		sb.append("<select name='q2dropdown' onchange='checkIfBothSecurityQuestionsAreSame(this.value);'>");
		sb.append("<option value='select'>Select</option>");

		if (isEmpty(request.getParameter("q2dropdown"))) {
			for (int i = 0; i < questionsList.size(); i++) {
				sb.append("<option value='" + (String) questionsList.get(i)
						+ "'>" + (String) questionsList.get(i) + "</option>");
			}
		} else {
			for (int i = 0; i < questionsList.size(); i++) {
				strTemp = (String) questionsList.get(i);
				if (strTemp
						.equalsIgnoreCase(request.getParameter("q2dropdown"))) {
					sb.append("<option value='" + (String) questionsList.get(i)
							+ "' selected='true'>"
							+ (String) questionsList.get(i) + "</option>");
				} else {
					sb.append("<option value='" + (String) questionsList.get(i)
							+ "'>" + (String) questionsList.get(i)
							+ "</option>");
				}
			}
		}
		sb.append("</select>");
		sb.append("</td></tr>");
		sb.append("<tr><td>Answer:</td><td>");
		if (isEmpty(request.getParameter("ans2"))) {
			sb.append("<input type='text' size='30' name='ans2' >&nbsp");
		} else {
			sb.append("<input type='text' size='30' name='ans2' value='"
					+ request.getParameter("ans2") + "'>&nbsp");
		}
		sb.append("</td></tr></table>");
		sb
				.append("<table><tr colspan='2'><td>Please enter your own question here"
						+ "</td></tr></table>");
		sb.append("<table><tr><td>Question3:</td><td>");
		if (isEmpty(request.getParameter("question3"))) {
			sb.append("<input type='text' size='30' name='question3' >&nbsp");
		} else {
			sb.append("<input type='text' size='30' name='question3' value='"
					+ request.getParameter("question3") + "'>&nbsp");
		}
		sb.append("</td></tr>");

		sb.append("<tr><td>Answer:</td><td>");
		if (isEmpty(request.getParameter("ans3"))) {
			sb.append("<input type='text' size='30' name='ans3' >&nbsp");
		} else {
			sb.append("<input type='text' size='30' name='ans3' value='"
					+ request.getParameter("ans3") + "'>&nbsp");
		}
		sb.append("</td></tr></table>");

		sb.append("<table><tr><td>");
		sb.append("<input type='submit' name='submit' value='Submit'>&nbsp");
		sb.append("</td></tr></table>");
		sb.append("</form>");
		return sb.toString();
	}

	private boolean isAnswerValid(String answer) {
		String regex = "[A-Za-z0-9 A-Za-z0-9 ]*";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(answer);
		return m.matches();
	}

}
