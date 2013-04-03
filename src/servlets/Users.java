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

import beans.GroupBean;

import properties.PropertiesClass;
import util.LoggerUtil;

import dao.ConnectionDAO;

/**
 * @author Srikesh Mandala(smandal2@exchange.asu.edu)
 *
 * The Users servlet is used to get list of
 * users/groups in the survey
 */
public class Users extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private ConnectionDAO connection = new ConnectionDAO();
	private static Logger logger = LoggerUtil.getClassLogger();

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		response.setContentType("text/html");
		response.setHeader("Expires", "0");
		response.setHeader("Cache-Control", "no-cache");
		response.setHeader("Pragma", "no-cache");
		HttpSession session = request.getSession(true);
		String userId = (String) session.getAttribute("admin");
		String surveyName = (String) session.getAttribute("surveyName");
		if (userId != null) {
			if (surveyName != null) {
				RequestDispatcher dispatcher = request
						.getRequestDispatcher("/static/ASU_Header.html");
				dispatcher.include(request, response);
				out.println(generateHtml(request));
				dispatcher = request
						.getRequestDispatcher("/static/ASU_Footer.html");
				dispatcher.include(request, response);
			} else {
				response
						.sendRedirect(response
								.encodeRedirectUrl("AddUserToSurvey"));
			}

		} else {
			response.sendRedirect(response
					.encodeRedirectUrl("Admin"));
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(true);
		String userId = (String) session.getAttribute("admin");
		if (userId == null) {
			doGet(request, response);
			return;
		}
		String surveyName = (String) session.getAttribute("surveyName");
		String addOrRemoveAction = request.getParameter("Add");
ArrayList<String> users = new ArrayList<String>();
ArrayList<String> groups = new ArrayList<String>();
		if (addOrRemoveAction != null) {
			String[] selectedUsers = request
					.getParameterValues("usersNotInSurveyddl");
			if (selectedUsers == null) {
				request.setAttribute("addUserToSurveyErrors",
						"Please select atleast one user!!");
				doGet(request, response);
				return;
			} else {
				for(int i=0;i<selectedUsers.length;i++)
				{
					if(isANumber(selectedUsers[i]))
						groups.add(selectedUsers[i]);
					else
						users.add(selectedUsers[i]);
				}
				addUsersToSurvey(users, surveyName, request);
				addGroupsToSurvey(groups, surveyName, request);
				StringBuffer errors = new StringBuffer();
				errors.append((String) session
						.getAttribute("addSurveysToUsermessages"));
				errors.append((String) session
						.getAttribute("addSurveysToGroupmessages"));
				request.setAttribute("addUserToSurveyErrors", errors.toString());
				doGet(request, response);
				return;
			}
		} else {
			String[] selectedUsers = request
					.getParameterValues("usersInSurveyddl");
			if (selectedUsers == null) {
				request.setAttribute("addUserToSurveyErrors",
						"Please select atleast one user!!");
				doGet(request, response);
				return;
			} else {
				removeUsersFromSurvey(selectedUsers, surveyName, request);
				String errors = (String) session
						.getAttribute("removeUsersFromSurveyErrors");
				request.setAttribute("addUserToSurveyErrors", errors);
				doGet(request, response);
				return;
			}
		}
	}
	
	private boolean isANumber(String str)
	{
		String regex = "[0-9]*";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(str);
		return m.matches();
	}
	
	private void addGroupsToSurvey(ArrayList<String> groups,String surveyName,HttpServletRequest request)
			{
		connection.addGroupsToSurvey(groups, surveyName, request);
			}

	private void addUsersToSurvey(ArrayList<String> selectedUsers, String surveyName,
			HttpServletRequest request) {
		connection.addSurveysToUser(selectedUsers, surveyName, request);
	}

	private String generateHtml(HttpServletRequest request) {
		HttpSession session = request.getSession(true);
		String applicationContext = "";
		GroupBean groupBean;
		ArrayList<GroupBean> groupsNotInSurvey = getAllGroupsWhichAreNotInSurvey(request);
		ArrayList<GroupBean> groupsInSurvey = (ArrayList<GroupBean>) session.getAttribute("allGroupsInSurvey");
		ArrayList<String> usersWhoAreNotInSurvey = getAllUsersWhoAreNotInSurvey(request);
		ArrayList<String> usersInSurvey = (ArrayList<String>) session
				.getAttribute("allusersInSurvey");
		StringBuilder sb = new StringBuilder("");
		sb.append("<title>AddUsers</title>");
		sb.append("<h1 class='title' align='center'>");
		sb.append(PropertiesClass.title);
		sb.append("</h1>");
		sb.append("<table align='right'><tr><td>");
		sb.append("<a href='"+applicationContext+"/AdminLogout'>Logout</a>");
		sb.append("</td></tr></table>");
		sb.append("<table><tr><td>");
		sb
				.append("<a href='"+applicationContext+"/AdminHome'>Home</a></td></tr><tr><td>");
		sb
				.append("<a href='"+applicationContext+"/TriggerEmails'>Trigger Emails</a></td></tr></table>");
		sb.append("<h3 id='pagetitle'>Add Users To Survey </h3>");
		String errors = (String) request.getAttribute("addUserToSurveyErrors");
		if (errors != null && errors.length() > 0) {
			sb.append("<table align='center'><tr><td><font color=\"red\">");
			sb.append(errors);
			sb.append("</font></td></tr></table>");
		}

		sb.append("<form name='addSurveysForm' action='' method='post'>");
		sb.append("<table align='center'><tr><td>");
		sb.append("<label>Potential Users</label></td>");
		sb.append("<td> </td><td></td>");
		sb.append("<td><label>Existing Users</label></td></tr>");
		sb.append("<tr><td>");
		sb
				.append("<select class='dropdown' name='usersNotInSurveyddl'multiple>");
		sb.append("<option value='' disabled='disabled'>Groups</option>");
		for(int index1=0;index1<groupsNotInSurvey.size();index1++)
		{
			groupBean = groupsNotInSurvey.get(index1);
			sb.append("<option value='"
					+ groupBean.getGroupId() + "'>"
					+ groupBean.getGroupName() + "</option>");
		}
		sb.append("<option value='' disabled='disabled'>Users</option>");
		for (int index = 0; index < usersWhoAreNotInSurvey.size(); index++) {
			sb.append("<option value='"
					+ (String) usersWhoAreNotInSurvey.get(index) + "'>"
					+ (String) usersWhoAreNotInSurvey.get(index) + "</option>");
		}
		sb.append("</select>");
		sb.append("</td>&nbsp&nbsp&nbsp&nbsp<td>");
		sb.append("<input type='submit' name='Add' value='Add>>'>&nbsp");
		sb.append("</td>&nbsp&nbsp&nbsp&nbsp<td>");
		sb.append("<input type='submit' name='Remove' value='<<Remove'>&nbsp");
		sb.append("</td>&nbsp&nbsp&nbsp&nbsp<td>");
		sb.append("<select class='dropdown' name='usersInSurveyddl' multiple>");
		sb.append("<option value='' disabled='disabled'>Groups</option>");
		for(int index2=0;index2<groupsInSurvey.size();index2++)
		{
			groupBean = groupsInSurvey.get(index2);
			sb.append("<option value='" + groupBean.getGroupId()
					+ "'>" + groupBean.getGroupName() + "</option>");
		}
		sb.append("<option value='' disabled='disabled'>Users</option>");
		for (int index1 = 0; index1 < usersInSurvey.size(); index1++) {
			sb.append("<option value='" + (String) usersInSurvey.get(index1)
					+ "'>" + (String) usersInSurvey.get(index1) + "</option>");
		}
		sb.append("</select>");
		sb.append("&nbsp&nbsp&nbsp&nbsp</td></tr></table></form>");
		return sb.toString();
	}

	private ArrayList<String> getAllUsers() {
		ArrayList<String> users = connection.getAllUsers();
		return users;
	}
	
	private ArrayList<GroupBean> getAllGroups()
	{
		ArrayList<GroupBean> groups = connection.getUserGroups();
		return groups;
	}
	
	private ArrayList<GroupBean> getAllGroupsWhichAreNotInSurvey(HttpServletRequest request)
	{
		HttpSession session = request.getSession(true);
		String surveyName = (String) session.getAttribute("surveyName");
		ArrayList<GroupBean> groups = getAllGroups();
		ArrayList<GroupBean> groupsInSurvey = getAllGroupsInSurvey(surveyName);
		GroupBean groupBean;
		GroupBean groupBean1;
		String strTemp="";
		for(int i=0;i<groupsInSurvey.size();i++)
		{
			groupBean = groupsInSurvey.get(i);
			strTemp = groupBean.getGroupName();
			
			for(int j=0;j<groups.size();j++)
			{
				groupBean1 = groups.get(j);
				if(strTemp.trim().equalsIgnoreCase(groupBean1.getGroupName().trim()))
				{
					groups.remove(j);
					break;
				}
			}
			
		}
		session.setAttribute("allGroupsInSurvey", groupsInSurvey);
		session.setAttribute("allGroupsWhichAreNotInSurvey", groups);
		return groups;
	}
	
	private ArrayList<GroupBean> getAllGroupsInSurvey(String surveyName)
	{
		ArrayList<GroupBean> groupsInSurvey = connection.getAllGroupsInSurvey(surveyName);
		return groupsInSurvey;
	}

	private ArrayList<String> getAllUsersWhoAreNotInSurvey(HttpServletRequest request) {
		HttpSession session = request.getSession(true);
		String surveyName = (String) session.getAttribute("surveyName");
		ArrayList<String> allusers = getAllUsers();
		ArrayList<String> allusersInSurvey = getListOfUsersForSurvey(surveyName,
				request);
		ArrayList<String> usersAccessed = connection.getListOfAccessedUsersForSurvey(surveyName);
		String user;
		String userInSurvey;
		for (int i = 0; i < allusersInSurvey.size(); i++) {
			userInSurvey = (String) allusersInSurvey.get(i);
			for (int j = 0; j < allusers.size(); j++) {
				user = (String) allusers.get(j);

				if (user.equalsIgnoreCase(userInSurvey)) {
					allusers.remove(j);
					break;
				}

			}
		}
		for (int i = 0; i < usersAccessed.size(); i++) {
			userInSurvey = (String) usersAccessed.get(i);
			for (int j = 0; j < allusers.size(); j++) {
				user = (String) allusers.get(j);

				if (user.equalsIgnoreCase(userInSurvey)) {
					allusers.remove(j);
					break;
				}

			}
		}
		session.setAttribute("allusersInSurvey", allusersInSurvey);
		return allusers;
	}

	private ArrayList<String> getListOfUsersForSurvey(String surveyName,
			HttpServletRequest request) {
		ArrayList<String> users = connection.getListOfUsersForSurvey(surveyName);
		return users;
	}

	public void removeUsersFromSurvey(String[] selectedUsers,
			String surveyName, HttpServletRequest request) {
		connection.removeUsersFromSurvey(selectedUsers, surveyName, request);
	}

}
