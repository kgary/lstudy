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

package dao;

import java.sql.*;
import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import properties.PropertiesClass;

import util.LoggerUtil;
import beans.EmailContentBean;
import beans.FilterParameters;
import beans.GroupBean;
import beans.QuestionsBean;
import beans.ResponseForAQuestionBean;
import beans.SubQuestionsBean;
import beans.SurveyBean;
import beans.SurveyQuestionsBean;
import beans.UserBean;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import java.util.Calendar;

/**
 * @author Srikesh Mandala(smandal2@exchange.asu.edu)
 *
 * The ConnectionDAO class consists of methods 
 * which interact with database and run queries
 */
public class ConnectionDAO {

	public ConnectionDAO() {
	}

	private static Logger logger = LoggerUtil.getClassLogger();

	/**
     * The method getUserDetails gets the details 
     * like is the userid valid, if valid is it registered
     *  or not
     * @param  String userId
     * 
     * @return UserBean userBean object
     */
	public UserBean getUserDetails(String userId) {
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		UserBean user = null;
		try {
			conn = DBPool.getConnection();
			pst = conn
					.prepareStatement("select * from userdetails where userid=?");
			pst.setString(1, userId);
			rs = pst.executeQuery();
			if (rs.next()) {
				logger.info(userId+": user id  is valid");
				user = new UserBean();
				user.setUserValid(true);
				user.setIsUserRegistered(rs.getInt("isRegistered"));
			}
			else
			{
				logger.info(userId+": user id  is not  valid");
			}
		} catch (Exception ex) {
			logger.error("Exception in method getUserDetails() "
					+ ex.getMessage());
		} finally {
			closeConnection(conn, pst, rs);
		}
		logger.info("returning userdetails for userid: " + userId);
		return user;
	}
	
	/**
     * The method getQuestionsFromDb gets all
     * the questions from database
     *  or not
     * @param  none
     * 
     * @return ArrayList ArrayList of questions
     */
	public ArrayList<String> getQuestionsFromDb() {
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		ArrayList<String> questionsList = new ArrayList<String>();
		try {
			conn = DBPool.getConnection();
			pst = conn.prepareStatement("select * from user_secret_question");
			rs = pst.executeQuery();
			while (rs.next()) {
				questionsList.add(rs.getString("question"));
			}
		} catch (Exception ex) {
			logger.error("Exception in method getQuestionsFromDb() "
					+ ex.getMessage());
		} finally {
			closeConnection(conn, pst, rs);
		}
		logger.info("returning questions list of size " + questionsList.size());
		return questionsList;
	}

	/**
     * The method resetUsersSecretQuestions deletes
     * all the user questions and secret question
     * from user_secret_answers and user_own_question_ans
     * tables
     * @param  String userId
     * 
     * @return int whether deletion is successful or not
     */
	public int resetUsersSecretQuestions(String userid) {
		Connection conn = null;
		PreparedStatement pst = null;
		int i = 1;
		try {
			conn = DBPool.getConnection();
			pst = conn
					.prepareStatement("delete from user_secret_answers where userid=?");
			pst.setString(1, userid);
			pst.executeUpdate();

			pst = conn
					.prepareStatement("delete from user_own_question_ans where userid=?");
			pst.setString(1, userid);
			pst.executeUpdate();

			pst = conn
					.prepareStatement("update userdetails set isregistered=0 where userid=?");
			pst.setString(1, userid);
			pst.executeUpdate();

		} catch (Exception ex) {
			i = 0;
			logger.error("Exception in method resetUsersSecretQuestions() "
					+ ex.getMessage());
		} finally {
			closeConnection(conn, pst, null);
		}
		logger.info("is resetting users secret questions successfull: " + i);
		return i;
	}

	
	/**
     * The method registerTheUser inserts all the users details
     *  into profile table 
     *  
     * @param  HttpServletRequest request
     *         String userId
     * 
     * @return none
     */
	public void registerTheUser(HttpServletRequest request, String userId) {
		Connection conn = null;
		PreparedStatement pst = null;
		StringBuffer errorsSB = new StringBuffer("");
		HttpSession session = request.getSession(true);
		ArrayList<String> questions = new ArrayList<String>();
		questions.add(request.getParameter("q1dropdown"));
		questions.add(request.getParameter("q2dropdown"));
		questions.add(request.getParameter("question3"));

		ArrayList<String> answers = new ArrayList<String>();
		answers.add(request.getParameter("ans1"));
		answers.add(request.getParameter("ans2"));
		answers.add(request.getParameter("ans3"));
		int questionAnsMatchingCounter = 0;

		ResultSet rs = null;
		int i = 0;
		try {
			conn = DBPool.getConnection();
			conn.setAutoCommit(false);
			for (int ind = 0; ind < 2; ind++) {
				rs = null;
				//check if both the security question & answer pair
				//match for any 2 users. If yes check if own question& answer
				//pair is also same
				pst = conn
						.prepareStatement("select * from user_secret_answers"
								+ " where quest_id=(select question_id from user_secret_question "
								+ "where question=?) and answer=?");

				pst.setString(1, (String) questions.get(ind));
				pst.setString(2, (String) answers.get(ind));
				rs = pst.executeQuery();
				if (rs.next()) {
					questionAnsMatchingCounter++;
				}
			}
			//if both the security question & answer pair
			//match check if secret question& answer combination 
			//also match. if yes show an error message to user. else
			//continue with registartion
			if (questionAnsMatchingCounter == 2) {
				rs = null;
				pst = conn
						.prepareStatement("SELECT userid FROM user_own_question_ans u where question=? and answer=?");

				pst.setString(1, (String) questions.get(2));
				pst.setString(2, (String) answers.get(2));
				rs = pst.executeQuery();
				if (rs.next()) {
					questionAnsMatchingCounter++;
				}
			}

			if (questionAnsMatchingCounter == 3) {
				errorsSB
						.append("Please change answer to any one of the questions");
				return;
			}

			for (int index = 0; index < 2; index++) {
				pst = conn
						.prepareStatement("insert into user_secret_answers values"
								+ "(?,(select question_id from user_secret_question where question=?), ?)");
				pst.setString(1, userId);
				pst.setString(2, (String) questions.get(index));
				pst.setString(3, (String) answers.get(index));
				i = pst.executeUpdate();
				if (i != 1) {
					errorsSB.append("error while registering");
					conn.rollback();
					break;
				}
			}

			if (errorsSB != null && errorsSB.length() > 0) {
				logger.error("error while registering the user: "
						+ errorsSB.toString());
				session.setAttribute("registerTheUserErrors", errorsSB
						.toString());
			} else {
				pst = conn
						.prepareStatement("insert into user_own_question_ans values(?,?,?)");
				pst.setString(1, userId);
				pst.setString(2, (String) questions.get(2));
				pst.setString(3, (String) answers.get(2));
				pst.executeUpdate();

				pst = conn
						.prepareStatement("update userdetails set isregistered=1 where userid=?");
				pst.setString(1, userId);
				pst.executeUpdate();
			}

		} catch (Exception ex) {
			logger.error("Exception in method registerTheUser() "
					+ ex.getMessage());
			errorsSB
					.append("System is experiencing problems. Please try again after sometime");
			try {
				conn.rollback();
			} catch (Exception e) {
				logger.error("Exception in method registerTheUser() "
						+ e.getMessage());
			}
		} finally {
			if (errorsSB != null && errorsSB.length() > 0) {
				session.setAttribute("registerTheUserErrors", errorsSB
						.toString());
			} else {
				logger.info("Registration succesfull");
				session.setAttribute("registerTheUserErrors",
						"Registration succesfull");
			}

			try {
				rs.close();
				conn.commit();
			} catch (Exception e) {
				logger.error("Exception in method registerTheUser() "
						+ e.getMessage());
			}
			closeConnection(conn, pst, rs);
		}

	}

	/**
     * The method getEmailContent retreives the email content
     * (subject,body) from email_content table
     *  
     * @param  none
     * 
     * @return EmailContentBean object
     */
	public EmailContentBean getEmailContent() {
		PreparedStatement pst = null;
		Connection conn = null;
		ResultSet rs = null;
		EmailContentBean email = null;
		try {
			conn = DBPool.getConnection();
			pst = conn.prepareStatement("SELECT * FROM email_content");
			rs = pst.executeQuery();
			if (rs.next()) {
				email = new EmailContentBean();
				email.setBody(rs.getString("body"));
				email.setSubject(rs.getString("subject"));
			}
		} catch (Exception ex) {
			logger.error("Exception in method getEmailContent() "
					+ ex.getMessage());
		} finally {
			closeConnection(conn, pst, rs);
		}
		return email;
	}

	
	/**
     * The method checkIfUserHasAlreadyAccessed checks
     * if the user has already accessed Registration page
     *  
     * @param  String email
     * 
     * @return boolean
     */
	public boolean checkIfUserHasAlreadyAccessed(String email) {
		boolean hasUserAlreadyAccessed = false;
		PreparedStatement pst = null;
		Connection conn = null;
		ResultSet rs = null;
		try {
			conn = DBPool.getConnection();
			pst = conn
					.prepareStatement("select * from emaillist_tracked where accessed=1 and emailid=?");
			pst.setString(1, email);
			rs = pst.executeQuery();
			if (rs.next()) {
				hasUserAlreadyAccessed = true;
			}
		} catch (Exception ex) {
			logger.error("Exception in method checkIfUserHasAlreadyAccessed() "
					+ ex.getMessage());
		} finally {
			closeConnection(conn, pst, rs);
		}
		return hasUserAlreadyAccessed;
	}


	/**
     * The method getAllEmailIds retreives all
     * the email addresses from emaillist_tracked
     * table
     *  
     * @param  none
     * 
     * @return ArrayList of email addresses
     */
	public ArrayList<String> getAllEmailIds() {
		PreparedStatement pst = null;
		ArrayList<String> emailIds = new ArrayList<String>();
		Connection conn = null;
		ResultSet rs = null;
		try {
			conn = DBPool.getConnection();
			pst = conn
					.prepareStatement(PropertiesClass.sqlQueryForSendingEmails);
			rs = pst.executeQuery();
			while (rs.next()) {
				emailIds.add(rs.getString("emailid"));
			}
		} catch (Exception ex) {
			logger.error("Exception in method getEmailContent() "
					+ ex.getMessage());
		} finally {
			closeConnection(conn, pst, rs);
		}
		logger.info("returning arraylist of emails of size " + emailIds.size());
		return emailIds;
	}

	/**
     * The method getSecretQuestionsOfUser retreives all
     *  the secret questions of the user.
     *  
     * @param  String userId
     * 
     * @return QuestionsBean object
     */
	public QuestionsBean getSecretQuestionsOfUser(String userId) {
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pst = null;
		QuestionsBean ques = null;
		int i = 1;
		try {
			conn = DBPool.getConnection();
			pst = conn
					.prepareStatement("select user_secret_question.question , user_secret_answers.answer from user_secret_answers "
							+ "INNER JOIN user_secret_question on  user_secret_answers.quest_id = user_secret_question.question_id "
							+ "where user_secret_answers.userid=?");
			pst.setString(1, userId);
			rs = pst.executeQuery();
			ques = new QuestionsBean();
			while (rs.next()) {
				if (i == 1) {
					ques.setQuestion1(rs.getString("question"));
					ques.setAnswer1(rs.getString("answer"));
					i++;
				} else {
					ques.setQuestion2(rs.getString("question"));
					ques.setAnswer2(rs.getString("answer"));
				}
			}
		} catch (Exception ex) {
			logger.error("Exception in method getSecretQuestionsOfUser() "
					+ ex.getMessage());
		}
		finally {
			closeConnection(conn, pst, rs);
		}
		return ques;
	}

	/**
     * The method getUserQuestionFromDb get users own question
     *  and answer from user_own_question_ans table
     *  
     * @param  String userId
     * 
     * @return QuestionsBean object
     */
	public QuestionsBean getUserQuestionFromDb(String userId) {
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pst = null;
		QuestionsBean ques = null;
		try {
			conn = DBPool.getConnection();
			pst = conn
					.prepareStatement("select * from user_own_question_ans where userid=?");
			pst.setString(1, userId);
			rs = pst.executeQuery();
			if (rs.next()) {
				ques = new QuestionsBean();
				logger.info("returning own secret question of user: "
						+ rs.getString("question"));
				ques.setOwnQuestion(rs.getString("question"));
				ques.setOwnAnswer(rs.getString("answer"));
			}
		} catch (Exception ex) {
			logger.error("Exception in method getUserQuestionFromDb() "
					+ ex.getMessage());
		} finally {
			closeConnection(conn, pst, rs);
		}
		return ques;
	}

	/**
     * The method getAdminCredentials checks whether
     * the gicen admin credentials are valid or not
     *  
     * @param  String userId,String password
     * 
     * @return boolean valid or not
     */
	public boolean getAdminCredentials(String userId, String password) {
		boolean isUserValid = false;
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pst = null;
		try {
			conn = DBPool.getConnection();
			pst = conn
					.prepareStatement("select * from admin_user where userid=? and password=?");
			pst.setString(1, userId);
			pst.setString(2, password);
			rs = pst.executeQuery();
			if (rs.next()) {
				isUserValid = true;
			}
		} catch (Exception ex) {
			logger.error("Exception in method getAdminCredentials() "
					+ ex.getMessage());
		} finally {
			closeConnection(conn, pst, rs);
		}
		logger.info("are admin credentials valid: " + isUserValid);
		return isUserValid;
	}

	/**
     * The method saveSurveyDetails saves given 
     * survey details to database
     *  
     * @param  String surveyName,String surveyUrl,
     *         String surveyDesc
     * 
     * @return boolean whether save is successful or not
     */
	public boolean saveSurveyDetails(String surveyName, String surveyUrl,
			String surveyDesc) {
		boolean isSaveSuccessful = false;
		int i = 0;
		Connection conn = null;
		PreparedStatement pst = null;
		try {
			conn = DBPool.getConnection();
			pst = conn
					.prepareStatement("insert into surveyinfo (surveyname,url,description,created_date) values(?,?,?,curdate())");
			pst.setString(1, surveyName);
			pst.setString(2, surveyUrl);
			pst.setString(3, surveyDesc);
			i = pst.executeUpdate();

			if (i == 1) {
				isSaveSuccessful = true;
			}

		} catch (Exception ex) {
			logger.error("Exception in method saveSurveyDetails() "
					+ ex.getMessage());
		} finally {
			closeConnection(conn, pst, null);
		}
		logger.info("saved survey: " + surveyName);
		return isSaveSuccessful;
	}

	
	/**
     * The method getSurveyToolInfo retreives admin
     * URL of the survey tool
     * @param  none
     * 
     * @return String amin URL of surevy tool
     */
	public String getSurveyToolInfo() {
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pst = null;
		String adminURL = "";
		try {
			conn = DBPool.getConnection();
			pst = conn.prepareStatement("SELECT * FROM surveytool_info");
			rs = pst.executeQuery();
			if (rs.next()) {
				adminURL = rs.getString("url");
			}
		} catch (Exception ex) {
			logger.error("Exception in method getSurveyToolInfo() "
					+ ex.getMessage());
		} finally {
			closeConnection(conn, pst, rs);
		}
		logger.info("returning survey url: " + adminURL);
		return adminURL;
	}

	/**
     * The method updateLastLastLogin updates lastLogin column 
     * of userdetails table upon successful login
     * 
     * @param  String userId
     * 
     * @return none
     */
	public void updateLastLastLogin(String userId) {
		Connection conn = null;
		PreparedStatement pst = null;
		try {
			conn = DBPool.getConnection();
			pst = conn
					.prepareStatement("update userdetails set lastLogin=CURRENT_TIMESTAMP where userid=?");
			pst.setString(1, userId);
			pst.executeUpdate();
			logger.info("updated last login successfully");
		} catch (Exception ex) {
			logger.error("Exception in method updateLastLastLogin() "
					+ ex.getMessage());
		} finally {
			closeConnection(conn, pst, null);
		}
	}

	/**
     * The method getAllUsers gets all the users 
     * from userdetails table
     * 
     * @param  none
     * 
     * @return ArrayList of users
     */
	public ArrayList<String> getAllUsers() {
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pst = null;
		ArrayList<String> users = new ArrayList<String>();
		try {
			conn = DBPool.getConnection();
			pst = conn.prepareStatement("select userid from userdetails");
			rs = pst.executeQuery();
			while (rs.next()) {
				users.add(rs.getString("userid"));
			}
		} catch (Exception ex) {
			logger
					.error("Exception in method getAllUsers() "
							+ ex.getMessage());
		} finally {
			closeConnection(conn, pst, rs);
		}
		logger.info("returning all users arraylist of size: " + users.size());
		return users;
	}

	/**
     * The method saveMessage saves the message to
     * message_center table. 
     * 
     * @param  String[] selectedUsers, String message
     * 
     * @return int 1 if save is succesful,else 0
     */
	public int saveMessage(String[] selectedUsers, String message) {
		Connection conn = null;
		PreparedStatement pst = null;
		int i = 0;
		try {
			conn = DBPool.getConnection();
			for (int index = 0; index < selectedUsers.length; index++) {
				pst = conn
						.prepareStatement("insert into message_center (userid,message) values(?,?)");
				pst.setString(1, selectedUsers[index]);
				pst.setString(2, message);
				i = pst.executeUpdate();
				logger
						.info("inserted admin message into message_center successfull");
			}
		} catch (Exception ex) {
			logger
					.error("Exception in method saveMessage() "
							+ ex.getMessage());
		} finally {
			closeConnection(conn, pst, null);
		}
		return i;
	}

	/**
     * The method getAllSurveys gets all the surveys
     * from surveyinfo table
     * 
     * @param  none
     * 
     * @return ArrayList of SurveyBeans
     */
	public ArrayList<SurveyBean> getAllSurveys() {
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pst = null;
		ArrayList<SurveyBean> surveysList = new ArrayList<SurveyBean>();
		SurveyBean survey;
		try {
			conn = DBPool.getConnection();
			pst = conn
					.prepareStatement("select surveyname,description from surveyinfo");
			rs = pst.executeQuery();
			while (rs.next()) {
				survey = new SurveyBean();
				survey.setSurveyName(rs.getString("surveyname"));
				survey.setSurveyDescription(rs.getString("description"));
				surveysList.add(survey);
			}
		} catch (Exception ex) {
			logger.error("Exception in method getAllSurveys() "
					+ ex.getMessage());
		} finally {
			closeConnection(conn, pst, rs);
		}
		logger.info("returning allsurveys arraylist of size: "
				+ surveysList.size());
		return surveysList;
	}
	
	/**
     * The method addGroupsToSurvey adds groups to survey
     * 
     * @param  ArrayList<String> groups,
     * 		   String surveyName,
     * 		   HttpServletRequest request
     * 
     * @return none
     */
	public void addGroupsToSurvey(ArrayList<String> groups,String surveyName,HttpServletRequest request)
	{
		HttpSession session = request.getSession(true);
		StringBuilder errorsSB = new StringBuilder("");
		Connection conn = null;
		int groupIndex = 0;
		PreparedStatement pst = null;
		try {
			conn = DBPool.getConnection();
			for (groupIndex = 0; groupIndex < groups.size(); groupIndex++) {
				pst = conn
				.prepareStatement("insert into group_survey_assesment values" +
						"(?,(select surveyid from surveyinfo where surveyname=?))");
				pst.setString(1, groups.get(groupIndex));
				pst.setString(2, surveyName);
				pst.executeUpdate();
				if (groupIndex == 0) {
					errorsSB.append("<br>Survey- " + surveyName
							+ "has been added to group: "
							+ groups.get(groupIndex));
				}else {
					errorsSB.append(", " + groups.get(groupIndex));
				}
			}
			ArrayList<String> usersInAGroup = new ArrayList<String>();
			for(int i=0;i<groups.size();i++)
			{
				usersInAGroup = getUsersInAGroup(groups.get(i));
				addSurveysToUser(usersInAGroup, surveyName, request);
			}
			
		}
			catch (Exception ex) {
				logger.error("Exception in method addGroupsToSurvey() "
						+ ex.getMessage());
				errorsSB
						.append("System encountered problems while updating. Please try again after sometime");
			}
			finally {
				session.setAttribute("addSurveysToGroupmessages", errorsSB
						.toString());
				
				closeConnection(conn, pst, null);
			}
			logger.info("adding groups to survey :" + errorsSB.toString());
			
	}
	
	
	private ArrayList<String> getUsersInAGroup(String groupdId)
	{
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs;
		ArrayList<String> usersInAGroup =  new ArrayList<String>();
		try {
			conn = DBPool.getConnection();
				pst = conn
				.prepareStatement("select userid from user_group_memberships where is_member_of_group"+groupdId+"=1");
				pst.setString(1, groupdId);
				rs = pst.executeQuery();
				while(rs.next())
				{
					usersInAGroup.add(rs.getString("userid"));
				}
			}
		catch (Exception ex) {
			logger.error("Exception in method getUsersInAGroup() "
					+ ex.getMessage());
		}
		finally {
			
			closeConnection(conn, pst, null);
		}
		return usersInAGroup;
	}
	
	private ArrayList<String> getSurveysForAGroup(String groupdId)
	{
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs;
		ArrayList<String> surveysForAGroup =  new ArrayList<String>();
		try {
			conn = DBPool.getConnection();
				pst = conn
				.prepareStatement("select survey_id from group_survey_assesment where group_id=?");
				pst.setString(1, groupdId);
				rs = pst.executeQuery();
				while(rs.next())
				{
					surveysForAGroup.add(rs.getString("survey_id"));
				}
			}
			catch (Exception ex) {
				logger.error("Exception in method addGroupsToSurvey() "
						+ ex.getMessage());
			}
			finally {
				
				closeConnection(conn, pst, null);
			}
			return surveysForAGroup;
	}
	
	/**
     * The method addSurveysToUser adds users to survey
     * 
     * @param ArrayList<String> selectedUsers,
     * 		   String surveyName,
     * 		   HttpServletRequest request
     * 
     * @return none
     */
	
	public void addSurveysToUser(ArrayList<String> selectedUsers, String surveyName,
			HttpServletRequest request) {
		HttpSession session = request.getSession(true);
		StringBuilder errorsSB = new StringBuilder("");
		int userIndex = 0;
		Connection conn = null;
		PreparedStatement pst = null;
		try {
			conn = DBPool.getConnection();
			for (userIndex = 0; userIndex < selectedUsers.size(); userIndex++) {
				pst = conn
						.prepareStatement("insert into user_survey_assessment (userid,surveyid)"
								+ "values(?,(select surveyid from surveyinfo where surveyname=?))");
				pst.setString(1, selectedUsers.get(userIndex));
				pst.setString(2, surveyName);
				pst.executeUpdate();
				if (userIndex == 0) {
					errorsSB.append("<br>Survey- " + surveyName
							+ "has been added to user: "
							+ selectedUsers.get(userIndex));
				} else {
					errorsSB.append(", " + selectedUsers.get(userIndex));
				}

			}

		} catch (MySQLIntegrityConstraintViolationException ex) {
			errorsSB.append("<br>user " + selectedUsers.get(userIndex)
					+ " already has the survey " + surveyName);
		} catch (Exception ex) {
			logger.error("Exception in method addSurveysToUser() "
					+ ex.getMessage());
			errorsSB
					.append("System encountered problems while updating. Please try again after sometime");
		} finally {
			session.setAttribute("addSurveysToUsermessages", errorsSB
					.toString());
			closeConnection(conn, pst, null);
		}
		logger.info("adding users to survey :" + errorsSB.toString());
	}

	/**
     * The method checkIfQuesAnsCombinationIsValid checks if 
     * given question answer combination is valid. If yes it 
     * returns an arraylist of all matching userids 
     * 
     * @param String question,
			String answer
     * 
     * @return ArrayList of userids
     */
	
	public ArrayList<String> checkIfQuesAnsCombinationIsValid(String question,
			String answer) {
		ResultSet rs = null;
		Connection conn = null;
		ArrayList<String> userIds = new ArrayList<String>();
		PreparedStatement pst = null;
		try {
			conn = DBPool.getConnection();
			pst = conn
					.prepareStatement("select * from user_secret_answers where"
							+ " quest_id=(select question_id from user_secret_question where question=?)"
							+ " and answer=?");
			pst.setString(1, question);
			pst.setString(2, answer);
			rs = pst.executeQuery();
			while (rs.next()) {
				userIds.add( rs.getString("userid"));
			}

		} catch (Exception ex) {
			logger
					.error("Exception in method checkIfQuesAnsCombinationIsValid() "
							+ ex.getMessage());
		} finally {
			closeConnection(conn, pst, rs);
		}
		return userIds;
	}

	/**
     * The method getSecondSecretQuestion gets the 2nd
     * question/answer pair of the user
     * 
     * @param HttpServletRequest request
     * 
     * @return none
     */
	
	public void getSecondSecretQuestion(HttpServletRequest request) {
		HttpSession session = request.getSession(true);
		String userId = (String) session.getAttribute("userId");
		String firstQuestion = (String) session.getAttribute("firstQuestion");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pst = null;
		try {
			conn = DBPool.getConnection();
			pst = conn
					.prepareStatement("select user_secret_question.question ,"
							+ " user_secret_answers.answer from user_secret_answers "
							+ "INNER JOIN user_secret_question on  "
							+ "user_secret_answers.quest_id = user_secret_question.question_id "
							+ "where user_secret_answers.userid=?");
			pst.setString(1, userId);
			rs = pst.executeQuery();
			while (rs.next()) {
				if (!rs.getString("question").equalsIgnoreCase(firstQuestion)) {
					session.setAttribute("2ndQuestion", rs
							.getString("question"));
					session.setAttribute("2ndQuestionAns", rs
							.getString("answer"));
					break;
				}
			}
		} catch (Exception ex) {
			logger.error("Exception in method getSecondSecretQuestion() "
					+ ex.getMessage());
		} finally {
			closeConnection(conn, pst, rs);
		}
	}

	/**
     * The method getUsersOwnQuestion gets users own 
     * question
     * @param HttpServletRequest request,
     * 		  ArrayList idsRecovered
     * 
     * @return none
     */
	
	public void getUsersOwnQuestion(HttpServletRequest request,ArrayList<String> idsRecovered) {
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pst = null;
		HttpSession session = request.getSession(true);
	ArrayList<String> questions= new ArrayList<String>();
	ArrayList<String> answers= new ArrayList<String>();
		try {
			conn = DBPool.getConnection();
			for(int i=0;i<idsRecovered.size();i++)
			{
				pst = conn
						.prepareStatement("select question,answer from user_own_question_ans where userid=?");
				pst.setString(1, idsRecovered.get(i));
				rs = pst.executeQuery();
				if (rs.next()) {
					questions.add(rs.getString("question"));
					answers.add(rs.getString("answer"));
				}
			}
		} catch (Exception ex) {
			logger.error("Exception in method validateAnswer() "
					+ ex.getMessage());
		} finally {
			closeConnection(conn, pst, rs);
			session.setAttribute("secretquestionsList", questions);
			session.setAttribute("secretquesAnswersList", answers);
		}
		logger.info("returning user own question " + questions.size() + " and ans: " + answers.size());
	}
	
	/**
     * The method getAllGroupsInSurvey gets all the 
     * groups added for the survey
     * 
     * @param String surveyName
     * 
     * @return ArrayList of GroupBeans
     */
	
	public ArrayList<GroupBean> getAllGroupsInSurvey(String surveyName)
	{
		ArrayList<GroupBean> groupsInSurvey = new ArrayList<GroupBean>();
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		GroupBean groupBean;
		try {
			conn = DBPool.getConnection();
			pst = conn
			.prepareStatement("select group_survey_assesment.group_id ," +
					"USER_GROUPS.group_name from group_survey_assesment" +
					" INNER JOIN USER_GROUPS on group_survey_assesment.group_id = USER_GROUPS.group_id" +
					" where group_survey_assesment.survey_id=(select surveyid from surveyinfo where surveyname=?)");
			pst.setString(1, surveyName);
		rs = pst.executeQuery();
		while(rs.next())
		{
			groupBean = new GroupBean();
			groupBean.setGroupId(rs.getInt("group_id"));
			groupBean.setGroupName(rs.getString("group_name"));
			groupsInSurvey.add(groupBean);
		}
		}
		 catch (Exception ex) {
				logger.error("Exception in method getAllGroupsInSurvey() "
						+ ex.getMessage());
			} finally {
				closeConnection(conn, pst, rs);
			}
			logger
			.info("returning list of groups: "
					+ groupsInSurvey.size());
			return groupsInSurvey;
	}
	
	/**
     * The method getUserGroups gets all the 
     * groups the user is memeber of
     * 
     * @param none
     * 
     * @return ArrayList of GroupBeans
     */
	
	public ArrayList<GroupBean> getUserGroups()
	{
		ArrayList<GroupBean> groups = new ArrayList<GroupBean>();
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		GroupBean groupBean;
		try {
			conn = DBPool.getConnection();
			pst = conn
			.prepareStatement("select group_name,group_id from USER_GROUPS");
		rs = pst.executeQuery();
		while(rs.next())
		{
			groupBean = new GroupBean();
			groupBean.setGroupId(rs.getInt("group_id"));
			groupBean.setGroupName(rs.getString("group_name"));
			groups.add(groupBean);
		}
		}
		 catch (Exception ex) {
				logger.error("Exception in method getUserGroups() "
						+ ex.getMessage());
			} finally {
				closeConnection(conn, pst, rs);
			}
			logger
			.info("returning list of groups: "
					+ groups.size());
			return groups;
	}

	
	/**
     * The method getListOfSurveysForUser gets
     *  all the surveys the user has
     * 
     * @param HttpServletRequest request
     * 
     * @return none
     */
	
	public void getListOfSurveysForUser(HttpServletRequest request) {
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		HttpSession session = request.getSession(true);
		String userId = (String) session.getAttribute("loggedIn");
		ArrayList<SurveyBean> surveysList = new ArrayList<SurveyBean>();
		SurveyBean survey;
		try {
			conn = DBPool.getConnection();
			pst = conn
					.prepareStatement("select user_survey_assessment.userid ,"
							+ " surveyinfo.surveyname,surveyinfo.url,surveyinfo.created_date from surveyinfo "
							+ "INNER JOIN user_survey_assessment on "
							+ " user_survey_assessment.surveyid = surveyinfo.surveyid "
							+ "where user_survey_assessment.userid=? and "
							+ "user_survey_assessment.completed is null");
			pst.setString(1, userId);
			rs = pst.executeQuery();
			while (rs.next()) {
				survey = new SurveyBean();
				survey.setSurveyName(rs.getString("surveyname"));
				survey.setSurveyURL(rs.getString("url"));
				survey.setDateCreated(rs.getString("created_date"));
				surveysList.add(survey);
			}
		} catch (Exception ex) {
			logger.error("Exception in method getListOfSurveysForUser() "
					+ ex.getMessage());
		} finally {
			closeConnection(conn, pst, rs);
			session.setAttribute("surveysList", surveysList);
		}
		logger
				.info("returning list of surveys for user: "
						+ surveysList.size());
	}

	
	/**
     * The method getMessageCenter gets all
     *  the messages for user from message_center table
     * 
     * @param String userId
     * 
     * @return ArrayList of messages
     */
	
	public ArrayList<String> getMessageCenter(String userId) {
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		ArrayList<String> messages = new ArrayList<String>();
		try {
			conn = DBPool.getConnection();
			pst = conn
					.prepareStatement("SELECT message FROM message_center where userid=?");
			pst.setString(1, userId);
			rs = pst.executeQuery();
			while (rs.next()) {
				messages.add(rs.getString("message"));
			}
		} catch (Exception ex) {
			logger.error("Exception in method getListOfSurveysForUser() "
					+ ex.getMessage());
		} finally {
			closeConnection(conn, pst, rs);
		}
		logger.info("returning messages from message center of size "
				+ messages.size());
		return messages;
	}

	/**
     * The method getListOfCompletedSurveysForUser gets all
     *  the completed surveys of the user
     * 
     * @param HttpServletRequest request
     * 
     * @return none
     */
	
	public void getListOfCompletedSurveysForUser(HttpServletRequest request) {
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pst = null;
		HttpSession session = request.getSession(true);
		String userId = (String) session.getAttribute("loggedIn");
		SurveyBean survey;
		ArrayList<SurveyBean> surveysList = new ArrayList<SurveyBean>();
		try {
			conn = DBPool.getConnection();
			pst = conn
					.prepareStatement("select user_survey_assessment.userid , user_survey_assessment.dateCompleted,"
							+ " surveyinfo.surveyname from surveyinfo "
							+ "INNER JOIN user_survey_assessment on "
							+ " user_survey_assessment.surveyid = surveyinfo.surveyid "
							+ "where user_survey_assessment.userid=? and "
							+ "user_survey_assessment.completed=1");
			pst.setString(1, userId);
			rs = pst.executeQuery();
			while (rs.next()) {
				survey = new SurveyBean();
				survey.setSurveyName(rs.getString("surveyname"));
				survey.setDateCompleted(rs.getString("dateCompleted"));
				surveysList.add(survey);
			}
		} catch (Exception ex) {
			logger.error("Exception in method getListOfSurveysForUser() "
					+ ex.getMessage());
		} finally {
			session.setAttribute("CompletedsurveysList", surveysList);
			closeConnection(conn, pst, rs);
		}
		logger.info("returning list of completed surveys for user: "
				+ surveysList.size());
	}

	
	/**
     * The method getListOfUsersForSurvey gets all
     *  the surveys of the user
     * 
     * @param String surveyName
     * 
     * @return ArrayList of surveys
     */
	
	public ArrayList<String> getListOfUsersForSurvey(String surveyName) {
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		ArrayList<String> users = new ArrayList<String>();
		try {
			conn = DBPool.getConnection();
			pst = conn
					.prepareStatement("select user_survey_assessment.userid ,"
							+ "user_survey_assessment.accessed from surveyinfo "
							+ "INNER JOIN user_survey_assessment on"
							+ " user_survey_assessment.surveyid = surveyinfo.surveyid "
							+ "where surveyinfo.surveyname=?");
			pst.setString(1, surveyName);
			rs = pst.executeQuery();
			while (rs.next()) {
				users.add(rs.getString("userid"));
			}
		} catch (Exception ex) {
			logger.error("Exception in method getListOfSurveysForUser() "
					+ ex.getMessage());
		}
		finally {
			closeConnection(conn, pst, rs);
		}
		logger.info("returning list of users of size" + users.size()
				+ "for survey : " + surveyName);
		return users;
	}

	/**
     * The method getListOfAccessedUsersForSurvey gets all
     *  the surveys accessed by the user
     * 
     * @param String surveyName,
			  HttpServletRequest request
     * 
     * @return ArrayList of surveys
     */
	
	public ArrayList<String> getListOfAccessedUsersForSurvey(String surveyName) {
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		ArrayList<String> users = new ArrayList<String>();
		ArrayList<String> usersAccessed = new ArrayList<String>();
		try {
			conn = DBPool.getConnection();
			pst = conn
					.prepareStatement("select user_survey_assessment.userid ,"
							+ "user_survey_assessment.accessed from surveyinfo "
							+ "INNER JOIN user_survey_assessment on"
							+ " user_survey_assessment.surveyid = surveyinfo.surveyid "
							+ "where surveyinfo.surveyname=?");
			pst.setString(1, surveyName);
			rs = pst.executeQuery();
			while (rs.next()) {
				if (rs.getInt("accessed") == 0) {
					users.add(rs.getString("userid"));
				} else {
					usersAccessed.add(rs.getString("userid"));
				}
			}
		} catch (Exception ex) {
			logger.error("Exception in method getListOfSurveysForUser() "
					+ ex.getMessage());
		} finally {
			closeConnection(conn, pst, rs);
		}
		logger.info("returning list of accessed users for survey of size: "
				+ usersAccessed.size());
		return users;

	}

	/**
     * The method removeUsersFromSurvey removes 
     * the given list of users from survey
     * 
     * @param String[] selectedUsers,
	 *		  String surveyName, 
	 *        HttpServletRequest request
     * 
     * @return none
     */
	
	public void removeUsersFromSurvey(String[] selectedUsers,
			String surveyName, HttpServletRequest request) {
		HttpSession session = request.getSession(true);
		Connection conn = null;
		int counter = 0;
		PreparedStatement pst = null;
		try {
			conn = DBPool.getConnection();
			for (int i = 0; i < selectedUsers.length; i++) {
				pst = conn
						.prepareStatement("delete from user_survey_assessment"
								+ " where userid=? and surveyid=(select surveyid from surveyinfo where surveyname=?)");
				pst.setString(1, selectedUsers[i]);
				pst.setString(2, surveyName);
				pst.executeUpdate();

			}
		} catch (Exception ex) {
			logger.error("Exception in method getListOfSurveysForUser() "
					+ ex.getMessage());
		} finally {
			closeConnection(conn, pst, null);
		}
		if (counter == 0) {
			session.setAttribute("removeUsersFromSurveyErrors",
					"Selected users have been removed successfully from survey "
							+ surveyName);
			logger
					.info("Selected users have been removed successfully from survey "
							+ surveyName);
		}
	}

	private boolean hasUserAlreadyAccessedSurvey(String userid,
			String surveyName) {
		boolean hasUserAccessed = false;
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		try {
			conn = DBPool.getConnection();
			pst = conn
					.prepareStatement("select user_survey_assessment.userid ,"
							+ "surveyinfo.surveyname,surveyinfo.url from surveyinfo "
							+ "INNER JOIN user_survey_assessment on"
							+ " user_survey_assessment.surveyid = surveyinfo.surveyid "
							+ "where surveyinfo.surveyname=? and userid=? and accessed=1");
			pst.setString(1, surveyName);
			pst.setString(2, userid);
			rs = pst.executeQuery();
			if (rs.next())
				hasUserAccessed = true;
		} catch (Exception ex) {
			logger.error("Exception in method getListOfSurveysForUser() "
					+ ex.getMessage());
		} finally {
			closeConnection(conn, pst, rs);
		}
		return hasUserAccessed;
	}

	/**
     * The method updateUserSurveyAccessStatus updates
     * user survey access status
     * 
     * @param String userId,
     *        String survey
     * 
     * @return boolean whether the update is succesful or not
     */
	
	public boolean updateUserSurveyAccessStatus(String userId, String survey) {
		ResultSet rs = null;
		int surveyid = 0;
		boolean result = false;
		Connection conn = null;
		PreparedStatement pst = null;
		logger.error("Survey url in DB class :" + survey);
		String url = survey + "&lang=en";
		logger.error("Parsed url in DB class :" + url);
		try {
			conn = DBPool.getConnection();
			pst = conn
					.prepareStatement("select surveyid from surveyinfo where url like ?");
			pst.setString(1, "%"+url+"%");
			rs = pst.executeQuery();
			if (rs.next()) {
				surveyid = rs.getInt("surveyid");
				logger.error("Surveyid :" + surveyid);
			}
			if (surveyid > 0) {

				pst = conn
						.prepareStatement("update user_survey_assessment set accessed=1 where userid=? and surveyid=?");
				pst.setString(1, userId);
				pst.setInt(2, surveyid);
				pst.executeUpdate();
				result = true;
			}

		} catch (Exception ex) {
			logger.error("Exception in method updateLastLastLogin() "
					+ ex.getMessage());
		} finally {
			closeConnection(conn, pst, rs);
		}
		return result;
	}
	
	private void addSurvey(String userId,int groupId,HttpServletRequest request)
	{
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs=null;
		ArrayList<String> surveys = new ArrayList<String>();
		ArrayList<String> users = new ArrayList<String>();
		users.add(userId);
		try {
			conn = DBPool.getConnection();
			pst = conn
					.prepareStatement("select surveyinfo.surveyname from surveyinfo" +
							" INNER JOIN group_survey_assesment on group_survey_assesment.survey_id" +
							" = surveyinfo.surveyid where group_survey_assesment.group_id=?");
			pst.setInt(1, groupId);
			rs = pst.executeQuery();
			while(rs.next())
			{
				surveys.add(rs.getString("surveyname"));
			}
			for(int i=0;i<surveys.size();i++)
			{
				addSurveysToUser(users, surveys.get(i), request);
			}
			
		}catch (Exception ex) {
			logger.error("Exception in method addSurvey() "
					+ ex.getMessage());
		} finally {
			closeConnection(conn, pst, rs);
		}
		
	}
	
	private void addMemberships(HttpServletRequest request,String userId,String yearOfGraduation,
																int cst315,int cst316,int cst415,int cst416)
	{
		Connection conn = null;
		PreparedStatement pst = null;
		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		int group1=0;	
		int group2=0;
		int group3=0;
		int group4=0;	
		int group5=0;
		int group6=0;
		int group7=0;	
		int group8=0;
		if(cst315==1 && cst316==0 && cst415==0 && cst416==0)
		{
			group1=1;
			addSurvey(userId,1,request);
		}
		if(cst315==1 && cst415==1 && cst316==0  && cst416==0)
		{
			group2=1;
			addSurvey(userId,2,request);
		}
		if(cst416==1 &&cst415==1 && cst315==0  && cst316==0)
		{
			group3=1;
			addSurvey(userId,3,request);
		}
		if(cst315==1 && cst316==1 && cst415==1 && cst416==1)
		{
			group4=1;
			addSurvey(userId,4,request);
		}
		if(cst315==0 && cst316==0 && cst415==0 && cst416==0)
		{
			group5=1;
			addSurvey(userId,5,request);
		}
		if(Integer.parseInt(yearOfGraduation)>year)
		{
			group6=1;
			addSurvey(userId,6,request);
		}
			
		try {
			conn = DBPool.getConnection();
			pst = conn
					.prepareStatement("insert into user_group_memberships values(?,?,?,?,?,?,?,?,?)");
			pst.setString(1,userId);
			pst.setInt(2,group1);
			pst.setInt(3,group2);
			pst.setInt(4,group3);
			pst.setInt(5,group4);
			pst.setInt(6,group5);
			pst.setInt(7,group6);
			pst.setInt(8,group7);
			pst.setInt(9,group8);
			 pst.executeUpdate();
		} catch (Exception ex) {
			logger.error("Exception in method addMemberships() "
					+ ex.getMessage());
		} finally {
			closeConnection(conn, pst, null);
		}
	}

	/**
     * The method registerUser inserts user details
     * into profile table
     * 
     * @param HttpServletRequest request
     * 
     * @return int whether the insertion is succesful or not
     */
	
	public int registerUser(HttpServletRequest request) {
		HttpSession session = request.getSession(true);
		Connection conn = null;
		PreparedStatement pst = null;
		String userid = getunassignedUserid();
		int i = 0;
		int cst315 = 0;
		int cst316 = 0;
		int cst415 = 0;
		int cst416 = 0;
		String education = request.getParameter("educationdropdown");
		String yearOfGraduation = request.getParameter("gradyeardropdown");
		String gpa = request.getParameter("gpadropdown");
		String[] entCourses = request.getParameterValues("entCourses");
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
		try {
			conn = DBPool.getConnection();
			pst = conn
					.prepareStatement("insert into profile (userid,education,yearofgraduation,gpa"
							+ ",taken315,taken316,taken415,taken416,working,field_of_work) values "
							+ "(?,?,?,?,?,?,?,?,?,?)");
			pst.setString(1, userid);
			pst.setString(2, education);
			pst.setString(3, yearOfGraduation);
			pst.setString(4, gpa);
			pst.setInt(5, cst315);
			pst.setInt(6, cst316);
			pst.setInt(7, cst415);
			pst.setInt(8, cst416);
			pst.setString(9, null);
			pst.setString(10, null);
			i = pst.executeUpdate();
		}

		catch (Exception ex) {
			logger.error("Exception in method registerUser() "
					+ ex.getMessage());
		} finally {
			closeConnection(conn, pst, null);
		}
		if (i == 1) {
			String email = (String)session.getAttribute("email1");
			session.setAttribute("email", email);
			logger.info("updated profile table successfully");
			updateUserid(userid);
			registerTheUser(request, userid);
			addMemberships(request,userid,yearOfGraduation,
									cst315,cst316,cst415,cst416);
			session.setAttribute("loggedIn", userid);
		}
		return i;
	}

	private void updateUserid(String userid) {
		Connection conn = null;
		PreparedStatement pst = null;
		try {
			conn = DBPool.getConnection();
			pst = conn
					.prepareStatement("update userdetails set isassigned=1 where userid=?");
			pst.setString(1, userid);
			pst.executeUpdate();
			logger.info("updated userid " + userid + " successfully");
		} catch (Exception ex) {
			logger.error("Exception in method updateUserid() "
					+ ex.getMessage());
		} finally {
			closeConnection(conn, pst, null);
		}
	}

	private String getunassignedUserid() {
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		String userId = "";
		try {
			conn = DBPool.getConnection();
			pst = conn
					.prepareStatement("select userid from userdetails where isAssigned=0");
			rs = pst.executeQuery();
			if (rs.next()) {
				userId = rs.getString("userid");
			}
		} catch (Exception ex) {
			logger.error("Exception in method getunassignedUserid() "
					+ ex.getMessage());
		} finally {
			closeConnection(conn, pst, rs);
		}
		logger.info("returning userid " + userId);
		return userId;
	}

	/**
     * The method getSurveyId gets the survey id
     * for given survey
     * 
     * @param String surveyName
     * 
     * @return int survey id of the given survey
     */
	
	public int getSurveyId(String surveyName) {
 		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pst = null;
		int surveyId = 0;
		try {
			conn = DBPool.getConnection();
			pst = conn
					.prepareStatement("select surveyid from surveyinfo where surveyname like ?");
			pst.setString(1, "%"+surveyName+"%");
			rs = pst.executeQuery();
			if (rs.next()) {
				surveyId = rs.getInt("surveyid");
			}
		} catch (Exception ex) {
			logger
					.error("Exception in method getSurveyId() "
							+ ex.getMessage());
		} finally {
			closeConnection(conn, pst, rs);
		}
		logger.info("returning surveyid: " + surveyId + " for survey: "
				+ surveyName);
		return surveyId;
	}

	/**
     * The method updateTrackedEmail updates
     * accessed status of the given email address
     * 
     * @param String email
     * 
     * @return none
     */
	
	public void updateTrackedEmail(String email) {
		Connection conn = null;
		PreparedStatement pst = null;
		try {
			conn = DBPool.getConnection();
			pst = conn
					.prepareStatement("update emaillist_tracked set accessed=1 where emailid=?");
			pst.setString(1, email);
			pst.executeUpdate();
			logger.info("updated emaillist_tracked table successfully for id: "
					+ email);
		} catch (Exception ex) {
			logger.error("Exception in method updateTrackedEmail() "
					+ ex.getMessage());
		} finally {
			closeConnection(conn, pst, null);
		}
	}

	/**
     * The method updateUserSurveyCompleteStatus updates
     * survey completed status of the given user
     * 
     * @param String userId,
     *        int surveyid
     * 
     * @return boolean whether update is succesful or not
     */
	
	public boolean updateUserSurveyCompleteStatus(String userId, int surveyid) {
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pst = null;
		boolean result = false;
		try {
			conn = DBPool.getConnection();
			if (surveyid > 0) {
				pst = conn
						.prepareStatement("update user_survey_assessment set completed=1 where userid=? and surveyid=?");
				pst.setString(1, userId);
				pst.setInt(2, surveyid);
				pst.executeUpdate();
				result = true;
			}

		} catch (Exception ex) {
			logger.error("Exception in method updateLastLastLogin() "
					+ ex.getMessage());
		} finally {
			closeConnection(conn, pst, rs);
		}
		logger.info("updated user_survey_assessment successfully for user: "
				+ userId);
		return result;
	}

	/**
     * The method getEndMessage gets the message to be
     *  displayed to the user after he has completed the 
     *  survey
     * 
     * @param int surveyid
     * 
     * @return String message
     */
	
	public String getEndMessage(int surveyid) {
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		String messsage = null;
		try {
			conn = DBPool.getConnection();
			pst = conn
					.prepareStatement("select briefingpage from surveyinfo where surveyid=?");
			pst.setInt(1, surveyid);
			rs = pst.executeQuery();
			if (rs.next()) {
				messsage = rs.getString("briefingpage");
			}
		} catch (Exception ex) {
			logger.error("Exception in method validateAnswer() "
					+ ex.getMessage());
		} finally {
			closeConnection(conn, pst, rs);
		}
		logger.info("returning thank you message: " + messsage);
		return messsage;
	}
	
	
	public void updateEmailSent(String emailid)
	{
		Connection conn = null;
		PreparedStatement pst = null;
		try {
			conn = DBPool.getConnection();
			pst = conn
					.prepareStatement("update emaillist_tracked set sent=1 where emailid like ?");
			pst.setString(1,emailid);
			pst.executeUpdate();
		}
		catch (Exception ex) {
			logger.error("Exception in method updateEmailSent() "
					+ ex.getMessage());
		} finally {
			closeConnection(conn, pst, null);
		}
		logger.info("updated sent column of emaillist_tracked table successfully");
		
	}

	private void closeConnection(Connection conn, PreparedStatement pst,
			ResultSet rs) {
		try {
			if (rs != null)
				rs.close();
			if (pst != null)
				pst.close();
			if (conn != null)
				conn.close();
		} catch (Exception ex) {
			logger.error("Exception while closing the connection "
					+ ex.getMessage());
		}
	}
	
	//methods for release 2 start here
	
	private String getWhereClause(FilterParameters filters) 
	{
StringBuffer whereClause = new StringBuffer("");
		
		if(!filters.getDegreeProgram().trim().equalsIgnoreCase("select"))
		{
			whereClause.append("where education='"+filters.getDegreeProgram()+"' ");
		}
		if(!filters.getGpa().trim().equalsIgnoreCase("select"))
		{
			if(whereClause.toString().equals(""))
				whereClause.append("where gpa='"+filters.getGpa()+"' ");
			else
				whereClause.append(" and gpa='"+filters.getGpa()+"' ");	
		}
		if(!filters.getGradYear().trim().equalsIgnoreCase("select"))
		{
			if(whereClause.toString().equals(""))
				whereClause.append("where yearOfGraduation="+filters.getGradYear().trim());
			else
				whereClause.append(" and yearOfGraduation="+filters.getGradYear().trim());
		}
		String[] entCourses = filters.getEntCourses();
		if(entCourses != null)
		{
			for (int index = 0; index < entCourses.length; index++) {
				if (entCourses[index].equalsIgnoreCase("CST315"))
				{
					if(whereClause.toString().equals(""))
						whereClause.append("where taken315=1");
					else
						whereClause.append(" and taken315=1");
				}

				else if (entCourses[index].equalsIgnoreCase("CST316"))
				{
					if(whereClause.toString().equals(""))
						whereClause.append("where taken316=1");
					else
						whereClause.append(" and taken316=1");
				}

				else if (entCourses[index].equalsIgnoreCase("CST415"))
				{
					if(whereClause.toString().equals(""))
						whereClause.append("where taken415=1");
					else
						whereClause.append(" and taken415=1");
				}

				else if (entCourses[index].equalsIgnoreCase("CST416"))
				{
					if(whereClause.toString().equals(""))
						whereClause.append("where taken416=1");
					else
						whereClause.append(" and taken416=1");
				}

			}
		}
		
		return whereClause.toString();
	}
	
	public ArrayList<String> getUsersMatchingTheFilter(FilterParameters filters)
	{
		ArrayList<String> users = new ArrayList<String>();
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		String whereClause = getWhereClause(filters);
		
		try {
			conn = DBPool.getConnection();
			pst = conn
					.prepareStatement("SELECT userid FROM profile "+whereClause);
			rs = pst.executeQuery();
			while(rs.next())
			{
				users.add(rs.getString("userid"));
			}
		}
		catch (Exception ex) {
			logger.error("Exception in method getUsersMatchingTheFilter() "
					+ ex.getMessage());
		} finally {
			closeConnection(conn, pst, null);
		}
		
		return users;
		
	}
	
	public ArrayList<String> getAllSecretQuestionsFromDb()
	{
		ArrayList<String> questionsList = new ArrayList<String>();
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		try {
			conn = DBPool.getConnection();
			pst = conn
					.prepareStatement("SELECT question FROM user_own_question_ans ");
			rs = pst.executeQuery();
			while(rs.next())
			{
				questionsList.add(rs.getString("question"));
			}
		}
		catch (Exception ex) {
			logger.error("Exception in method getAllSecretQuestionsFromDb() "
					+ ex.getMessage());
		} finally {
			closeConnection(conn, pst, null);
		}
		
		return questionsList;
		
	}
	
	public String checkIfSecretQandACombinationIsValid(String question,String ans)
	{
		String userid = "";
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		try {
			conn = DBPool.getConnection();
			pst = conn
					.prepareStatement("select userid from user_own_question_ans where question=? and answer=?");
			pst.setString(1, question);
			pst.setString(2, ans);
			rs = pst.executeQuery();
			if(rs.next())
			{
				userid = rs.getString("userid");
			}
		}
		catch (Exception ex) {
			logger.error("Exception in method getAllSecretQuestionsFromDb() "
					+ ex.getMessage());
		} finally {
			closeConnection(conn, pst, null);
		}
		
		return userid;
		
	}
	
	public ArrayList<SurveyQuestionsBean> getAllSurveyQuestions()
	{
		ArrayList<SurveyQuestionsBean> questions = new ArrayList<SurveyQuestionsBean>();
		SurveyQuestionsBean questionBean;
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			conn = DBPool.getConnection();
			pst = conn
					.prepareStatement("SELECT qid,question,gid FROM phpsv_questions");
			rs = pst.executeQuery();
			while(rs.next())
			{
				questionBean = new SurveyQuestionsBean();
				questionBean.setQuestion(rs.getString("question"));
				questionBean.setQuestionId(rs.getString("qid"));
				questionBean.setGroupId(rs.getString("gid"));
				questions.add(questionBean);
			}
		}
		catch (Exception ex) {
			logger.error("Exception in method getAllSurveyQuestions() "
					+ ex.getMessage());
		} finally {
			closeConnection(conn, pst, null);
		}
		
		return questions;
		
	}
	
	public ArrayList<ResponseForAQuestionBean> getResponses(String qid,String gid,ArrayList<String> users,boolean isnotATextQuestion)
	{
		ArrayList<ResponseForAQuestionBean> responses = new ArrayList<ResponseForAQuestionBean>();
		ResponseForAQuestionBean questionBean;
		ArrayList<SubQuestionsBean> newList; 
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		StringBuffer sqlQuery = new StringBuffer("");
		if(isnotATextQuestion)
		{
			sqlQuery.append("select phpsv_survey_55675.token,phpsv_answers.answer, phpsv_survey_55675.55675X"+gid.trim()+"X"+qid.trim());
			sqlQuery.append(" from phpsv_survey_55675 left join phpsv_answers on phpsv_survey_55675.55675X"+gid.trim()+"X"+qid.trim());
			sqlQuery.append("=phpsv_answers.code where qid="+qid.trim()+" and token in (");
		}
		else
		{
			sqlQuery.append("select token,55675X"+gid.trim()+"X"+qid.trim());
			sqlQuery.append(" from phpsv_survey_55675 where token in (");
		}
		String comma;
		for(int i=0;i<users.size();i++)
		{
			comma=",";
			if(i==0)
				comma="";
			sqlQuery.append(comma+"'"+users.get(i)+"'");
		}
		sqlQuery.append(") and lastpage=11 order by token");
		String userid="";
		try {
			conn = DBPool.getConnection();
			pst = conn
					.prepareStatement(sqlQuery.toString());
			rs = pst.executeQuery();
			while(rs.next())
			{
				questionBean = new ResponseForAQuestionBean();
				userid = rs.getString("token");
				newList = new ArrayList<SubQuestionsBean>();
				if(isnotATextQuestion)
					newList.add(new SubQuestionsBean("","",rs.getString("phpsv_answers.answer"),userid));
				else
				newList.add(new SubQuestionsBean("","",rs.getString("55675X"+gid.trim()+"X"+qid.trim()),userid));
				questionBean.setQuestionId(qid);
				questionBean.setUserid(userid);
				questionBean.setSubQuestions(newList);
				responses.add(questionBean);
			}
		}
			catch (Exception ex) {
				logger.error("Exception in method getAllSubQuestionsOfAQuestion() "
						+ ex.getMessage());
			} finally {
				closeConnection(conn, pst, null);
			}
			
			return responses;
		
	}
	
	public ArrayList<ResponseForAQuestionBean> getResponsesForAQuestion(String qid,String gid,ArrayList<String> users)
	{
		ArrayList<ResponseForAQuestionBean> responses = new ArrayList<ResponseForAQuestionBean>();
		SubQuestionsBean subQs;
		if(qid.trim().equals("42")|| qid.trim().equals("43") || qid.trim().equals("40") || qid.trim().equals("41"))
		{
			
			if(qid.trim().equals("42")|| qid.trim().equals("43"))
				responses = getResponses(qid,gid,users,true);
			else
				responses = getResponses(qid,gid,users,false);
			
			return responses;
		}
		ArrayList<SubQuestionsBean> subQuestions = getAllSubQuestionsOfAQuestion(qid);
		ArrayList<SubQuestionsBean> newList; 
		ResponseForAQuestionBean questionBean;
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		StringBuffer sqlQuery = new StringBuffer("");
		sqlQuery.append("select token,");
		String comma;
		for(int i=0;i<subQuestions.size();i++)
		{
			comma=",";
			subQs = subQuestions.get(i);
			if(i==0)
				comma="";
			sqlQuery.append(comma+" 55675X"+gid.trim()+"X"+qid.trim()+subQs.getQuestionId());
		}
		sqlQuery.append(" from phpsv_survey_55675 where token in (");
		for(int i=0;i<users.size();i++)
		{
			comma=",";
			if(i==0)
				comma="";
			sqlQuery.append(comma+"'"+users.get(i)+"'");
		}
		sqlQuery.append(") and lastpage=11 order by token");
		String userid="";
		try {
			conn = DBPool.getConnection();
			pst = conn
					.prepareStatement(sqlQuery.toString());
			rs = pst.executeQuery();
			while(rs.next())
			{
				questionBean = new ResponseForAQuestionBean();	
				userid = rs.getString("token");
				newList = new ArrayList<SubQuestionsBean>();
				
				for(int i=0;i<subQuestions.size();i++)
				{
					//subQs = subQuestions.get(i);
					subQs = new SubQuestionsBean(subQuestions.get(i).getQuestionId(),subQuestions.get(i).getQuestion(),subQuestions.get(i).getResponse(),subQuestions.get(i).getUserid());
					subQs.setResponse(rs.getString("55675X"+gid.trim()+"X"+qid.trim()+subQuestions.get(i).getQuestionId()));
					subQs.setUserid(userid);
					
					newList.add(subQs);
					/*subQs = subQuestions.get(i);
					subQuestions.get(i).setResponse(rs.getString("55675X"+gid.trim()+"X"+qid.trim()+subQs.getQuestionId()));
					subQuestions.get(i).setUserid(userid);*/
				}
				questionBean.setQuestionId(qid);
				questionBean.setUserid(userid);
				questionBean.setSubQuestions(newList);
				responses.add(questionBean);
			}
		}
		catch (Exception ex) {
			logger.error("Exception in method getAllSubQuestionsOfAQuestion() "
					+ ex.getMessage());
		} finally {
			closeConnection(conn, pst, null);
		}
		
		return responses;
	}
	

		
	public ArrayList<String> getusersOfAGroup(int groupId)
	{
		 ArrayList<String> userids = new ArrayList<String>();
		 Connection conn = null;
			PreparedStatement pst = null;
			ResultSet rs = null;
			try {
				conn = DBPool.getConnection();
				pst = conn
						.prepareStatement("select cmapTitle from group_cmaps where groupid=?");
				pst.setInt(1, groupId);
				rs = pst.executeQuery();
				while(rs.next())
				{
					userids.add(rs.getString("cmapTitle"));
				}
			}
			catch (Exception ex) {
				logger.error("Exception in method getAllSubQuestionsOfAQuestion() "
						+ ex.getMessage());
			} finally {
				closeConnection(conn, pst, null);
			}
			
			return userids;
	}
	
	public ArrayList<SubQuestionsBean> getAllSubQuestionsOfAQuestion(String qid)
	{
		ArrayList<SubQuestionsBean> subQuestions = new ArrayList<SubQuestionsBean>();
		SubQuestionsBean subQuestionBean;
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			conn = DBPool.getConnection();
			pst = conn
					.prepareStatement("SELECT code,answer FROM phpsv_answers p where qid="+qid.trim());
			rs = pst.executeQuery();
			while(rs.next())
			{
				subQuestionBean = new SubQuestionsBean();
			subQuestionBean.setQuestion(rs.getString("answer"));
			subQuestionBean.setQuestionId(rs.getString("code"));
			subQuestions.add(subQuestionBean);
			}
		}
		catch (Exception ex) {
			logger.error("Exception in method getAllSubQuestionsOfAQuestion() "
					+ ex.getMessage());
		} finally {
			closeConnection(conn, pst, null);
		}
		
		return subQuestions;

	}

}
