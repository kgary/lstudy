/**
 * 
 */
package servlets;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import properties.PropertiesClass;
import util.LoggerUtil;
import util.RenderUtils;

import beans.FilterParameters;
import beans.ResponseForAQuestionBean;
import beans.SubQuestionsBean;
import beans.SurveyQuestionsBean;

/**
 * @author kevinagary
 *
 */
public class ReportPerQuestion extends HttpServlet {

	private static Logger logger = LoggerUtil.getClassLogger();
	
	private String generateHTMLForIndividualQues(ArrayList<ResponseForAQuestionBean> responses,HttpServletRequest request)
	{
		HttpSession session = request.getSession(true);
		
		StringBuilder sb = new StringBuilder("");
		SurveyQuestionsBean qs;
		ResponseForAQuestionBean response;
		ArrayList<SubQuestionsBean> subQuestions ;
		SubQuestionsBean subQs;
		String applicationContext = "";
		String questionSelected="";
		ArrayList<SurveyQuestionsBean> questions = (ArrayList<SurveyQuestionsBean>)session.getAttribute("allSurveyQuestions");
		if(questions == null)
			questions = RenderUtils.getAllSurveyQuestions();
		String qid = request.getParameter("qid"); 
	
		for(int i=0;i<questions.size();i++)
		{
			qs = questions.get(i);
			if(qs.getQuestionId().trim().equals(qid))
			{
				questionSelected = qs.getQuestion();
				break;
			}
		}
	
		sb.append("<title>Report Per Question</title>");
		sb.append("<h1 class='title' align='center'>");
		sb.append(PropertiesClass.title);
		sb.append("</h1>");
	
		sb.append("<h3 id='pagetitle'>Reports</h3>");
	
		sb.append("<table align='right'><tr><td>");
		sb.append("<a href='"+applicationContext+"AdminLogout'>Logout</a>");
		sb.append("</td></tr></table>");
		sb.append("<a href='"+applicationContext+"AdminHome'>Home</a><br/><br/>");
	
	
		if(responses==null || responses.size()==0)
		{
			sb.append("There are no responses to show. Please select a different group or change the filters<br/><br/><br/>");
			return sb.toString();
		}
		FilterParameters filters = null;
		String fromNsfccli = (String)session.getAttribute("fromNsfccli");
		if(fromNsfccli==null)
			filters = (FilterParameters)session.getAttribute("filters");
	
		if(filters != null)
		{
			int counter=0;
			sb.append("<table>");
			sb.append("<tr><td>Filters Selected:</td></tr>");
			if(!filters.getDegreeProgram().trim().equalsIgnoreCase("select"))
			{
				counter++;
				sb.append("<tr><td>Degree Program: "+filters.getDegreeProgram()+
				"</td></tr>");
			}
			if(!filters.getGpa().trim().equalsIgnoreCase("select"))
			{
				counter++;
				sb.append("<tr><td>GPA: "+filters.getGpa()+
				"</td></tr>");
			}
			if(!filters.getGradYear().trim().equalsIgnoreCase("select"))
			{
				counter++;
				sb.append("<tr><td>Year Graduated: "+filters.getGradYear()+
				"</td></tr>");
			}
			if(filters.getEntCourses() != null)
			{
				String [] entCourses = filters.getEntCourses();
	
				for (int index = 0; index < entCourses.length; index++) {
					if (entCourses[index].equalsIgnoreCase("CST315"))
					{
						counter++;
						sb.append("<tr><td>CST 315: Yes</td></tr>");
					}
	
					else if (entCourses[index].equalsIgnoreCase("CST316"))
					{
						counter++;
						sb.append("<tr><td>CST 316: Yes</td></tr>");
					}
	
					else if (entCourses[index].equalsIgnoreCase("CST415"))
					{
						counter++;
						sb.append("<tr><td>CST 415: Yes</td></tr>");
					}
	
					else if (entCourses[index].equalsIgnoreCase("CST416"))
					{
						counter++;
						sb.append("<tr><td>CST 416: Yes</td></tr>");
					}
	
				}
			}
	
			if(counter == 0)
			{
				sb.append("<tr><td>none. Responses of all users will be shown.</td></tr>");
			}
			sb.append("</table><br><br>");
	
		}
	
		if(questionSelected != "")
		{
			sb.append("<table><tr><td>Question Selected:</td></tr>");
			sb.append("<tr><td>"+questionSelected+"</td></tr></table><br><br>");
		}
	
		sb.append("<table align='left'><tr><td><a href='"+applicationContext
				+"Reports'>Back</a></td></tr></table><br>");
		sb.append("<table border='1'><tr>");
	
		sb.append("<th>UserId</th>");
		response = responses.get(0);
		subQuestions = response.getSubQuestions();
	
		for(int k=0;k<subQuestions.size();k++)
		{
			subQs = subQuestions.get(k);
			sb.append("<th>"+subQs.getQuestion()+"</th>");
		}
		sb.append("</tr><tr>");
	
		Hashtable<Integer,String> responsesForMean = new Hashtable<Integer,String>();
		Hashtable<Integer,ArrayList<Integer>> responsesForSD = new Hashtable<Integer,ArrayList<Integer>>();
		DecimalFormat df = new DecimalFormat("#.##");
		ArrayList<Integer> data;
		
		//System.out.println("WWW processing question with qid = " + qid);
		if(qid != null && !(qid.trim().equals("42")|| qid.trim().equals("43") || qid.trim().equals("40") || qid.trim().equals("41")))
		{
			RenderUtils.constructResponsesForStats(responses, responsesForMean, responsesForSD);
			if(responsesForMean != null && responsesForMean.size() >0)
			{
				//System.out.println("YYY We have responses for Man");
				sb.append("<tr><td></td>");
				double mean;
				double sd;
				double median;
				for(int k=0;k<responsesForMean.size();k++)
				{
					data = (ArrayList<Integer>)responsesForSD.get(k);
					if(data==null)
					{
						data = new ArrayList<Integer>();
						data.add(0);
					}
					//System.out.println("ZZZ k= "+(String)responsesForMan.get(k));
					if((String)responsesForMean.get(k)==null)
						responsesForMean.put(k, "0");
					
					mean = RenderUtils.calculateMean(responsesForMean.get(k), responses.size());
					sd = RenderUtils.calculateSD(data, mean);
					median = RenderUtils.getMedian(data);
					sb.append("<td><font color='red'>Mean = "+df.format(mean)+
							"<br><br>Median = "+df.format(median)+
							"<br><br>Standard Deviation = "+df.format(sd)+"</font></td>");
				}
				sb.append("</tr>");
			}
		}
	
		if(responses != null)
		{
			for(int i=0;i<responses.size();i++)
			{
				response = responses.get(i);
				sb.append("<tr>");
				sb.append("<td>"+response.getUserid()+"</td>");
				subQuestions = response.getSubQuestions();
				for(int j=0;j<subQuestions.size();j++)
				{
					subQs = subQuestions.get(j);
					if(subQs.getResponse().equals(""))
						sb.append("<td>NA</td>");
					else
						sb.append("<td>"+subQs.getResponse()+"</td>");					
				}
				sb.append("</tr>");
			}
		}
	
		sb.append("</table><br>");
		sb.append("<table><tr><td><a href='"+applicationContext
				+"ReportPerQuestion?action=csvExport'>Export As CSV</a></td></tr></table><br><br>");
		sb.append("<table><tr><td>NA means not answered</td></tr></table><br><br>");
	
		return sb.toString();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		response.setContentType("text/html");

		response.setHeader("Expires", "0");
		response.setHeader("Cache-Control", "no-cache");
		response.setHeader("Pragma", "no-cache");

		HttpSession session = request.getSession(true);
		String userId = (String) session.getAttribute("admin");

		if (userId == null) 
		{
			response.sendRedirect(response.encodeRedirectURL("Admin"));
		}
			
		String action = request.getParameter("action");
			
		if (action == null) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		if(action.equals("csvExport"))
		{
			String qid = (String)session.getAttribute("qid");

			ArrayList<ResponseForAQuestionBean> responses = (ArrayList<ResponseForAQuestionBean>)session.getAttribute("allresponses");

			ArrayList<SurveyQuestionsBean> questions = (ArrayList<SurveyQuestionsBean>)session.getAttribute("allSurveyQuestions");
			if(questions == null)
				questions = RenderUtils.getAllSurveyQuestions();
			String questionSelected= RenderUtils.getQuestionSelectedFromQid(questions,qid);

			if(responses == null)
			{
				String gid = (String)session.getAttribute("gid");
				ArrayList<String> users = (ArrayList<String>)session.getAttribute("usersMatchingTheFilters");
				responses = RenderUtils.getResponsesForAQuestion(qid,gid,users);
			}
			
			// YYY Why do we need filters here? KG
			FilterParameters filters = (FilterParameters)session.getAttribute("filters");	
			String csvFileContents = RenderUtils.generateCSVContents(questionSelected,responses,false,filters);

			if (csvFileContents == null) {
				out.println("No results were returned for CSV file export.<br>\n");
			}
			else
			{
				byte requestBytes[] = csvFileContents.getBytes();
				ByteArrayInputStream bis = new ByteArrayInputStream(requestBytes);
				Date dateToday = new Date();
				SimpleDateFormat df = new SimpleDateFormat("yyyyMMddhhmmss");

				try
				{
					response.reset();
					response.setContentType("application/text");
					response.setHeader("Content-disposition","attachment; filename=" + "LS_" + df.format(dateToday) + ".csv" );
					byte[] buf = new byte[1024];
					int len;
					while ((len = bis.read(buf)) > 0){
						response.getOutputStream().write(buf, 0, len);
					}
					bis.close();
					response.getOutputStream().flush(); 
				}
				catch(Exception ex)
				{
					logger.error("exception caught while generating csv contents: "+ex.getMessage());
					ex.printStackTrace();
				}
				return;
			}
		}
		else if(action.equals("viewResponse"))
		{
			String qid = request.getParameter("qid");
			String gid = request.getParameter("gid");
			// What the heck is this? What are 44 and 38? KG
			//this case will not arise. but just to be on safer side
			if (qid == null || gid == null) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST);
				return;
			}
			/* Took this out 6/9/11 in favor of above KG
			if(qid == null)
				qid = "44";
			if(gid == null)
				gid = "38";
			// YYY you don't need these in the session they are on the URL KG
			session.setAttribute("qid", qid);
			session.setAttribute("gid", gid);
			*/
			ArrayList<String> users = (ArrayList<String>)session.getAttribute("usersMatchingTheFilters");
			ArrayList<ResponseForAQuestionBean> responses = RenderUtils.getResponsesForAQuestion(qid,gid,users);
			session.setAttribute("allresponses", responses);
			RequestDispatcher dispatcher = request.getRequestDispatcher("/static/ASU_Header.html");
			dispatcher.include(request, response);
			out.println(generateHTMLForIndividualQues(responses,request));
			dispatcher = request.getRequestDispatcher("/static/ASU_Footer.html");
			dispatcher.include(request, response);
		}
	}

}
