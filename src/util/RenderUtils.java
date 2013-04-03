/**
 * 
 */
package util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;

import org.jsoup.Jsoup;

import dao.ConnectionDAO;

import beans.FilterParameters;
import beans.ResponseForAQuestionBean;
import beans.SubQuestionsBean;
import beans.SurveyQuestionsBean;

/**
 * @author kevinagary
 *
 */
public final class RenderUtils {

	/**
	 * 
	 */
	private RenderUtils() {
	}

	public static String generateCSVContentsOfAllResponses(ArrayList<SurveyQuestionsBean> questions, FilterParameters filters)
	{
		final ConnectionDAO connection = new ConnectionDAO();
		StringBuffer csvContents = new StringBuffer("");
	
		csvContents.append(filters);
	
		ArrayList<ResponseForAQuestionBean> responses;
		String questionSelected="";

		if(questions == null)
			questions = getAllSurveyQuestions();
	
		ArrayList<String> users = connection.getUsersMatchingTheFilter(new FilterParameters("select","select","select",null));
		boolean allResponses = true;
		for(int i=0;i<questions.size();i++)
		{
			responses = getResponsesForAQuestion(questions.get(i).getQuestionId(),questions.get(i).getGroupId(),users);
			questionSelected=getQuestionSelectedFromQid(questions,questions.get(i).getQuestionId());
			questionSelected = questionSelected.replaceAll("\\<.*?>","");
			csvContents.append(generateCSVContents(questionSelected,responses,allResponses,filters));
	
		}
	
		return csvContents.toString();
	}

	public static ArrayList<SurveyQuestionsBean> getAllSurveyQuestions()
	{
		final ConnectionDAO connection = new ConnectionDAO();
		ArrayList<SurveyQuestionsBean> questions = connection.getAllSurveyQuestions();
		return questions;
	}

	public static String getFilters(FilterParameters filters)
	{
		StringBuffer csvContents = new StringBuffer("");
	
		if(filters != null)
		{
			int counter=0;
			csvContents.append("Filters Selected: ");
			if(!filters.getDegreeProgram().trim().equalsIgnoreCase("select"))
			{
				counter++;
				csvContents.append("Degree Program: "+filters.getDegreeProgram());
			}
			if(!filters.getGpa().trim().equalsIgnoreCase("select"))
			{
				counter++;
				csvContents.append("GPA: "+filters.getGpa()+" ");
			}
			if(!filters.getGradYear().trim().equalsIgnoreCase("select"))
			{
				counter++;
				csvContents.append("Year Graduated: "+filters.getGradYear()+" ");
			}
			if(filters.getEntCourses() != null)
			{
				String [] entCourses = filters.getEntCourses();
	
				for (int index = 0; index < entCourses.length; index++) {
					if (entCourses[index].equalsIgnoreCase("CST315"))
					{
						counter++;
						csvContents.append("CST 315: Yes ");
					}
	
					else if (entCourses[index].equalsIgnoreCase("CST316"))
					{
						counter++;
						csvContents.append("CST 316: Yes ");
					}
	
					else if (entCourses[index].equalsIgnoreCase("CST415"))
					{
						counter++;
						csvContents.append("CST 415: Yes ");
					}
	
					else if (entCourses[index].equalsIgnoreCase("CST416"))
					{
						counter++;
						csvContents.append("CST 416: Yes ");
					}
	
				}
			}
	
			if(counter == 0)
			{
				csvContents.append("None. Responses of all users will be shown.");
			}
			csvContents.append("\r\n");
	
		}
		return csvContents.toString();
	}

	public static ArrayList<ResponseForAQuestionBean> getResponsesForAQuestion(String qid,String gid,ArrayList<String> users)
	{
		final ConnectionDAO connection = new ConnectionDAO();
		ArrayList<ResponseForAQuestionBean> responses = connection.getResponsesForAQuestion(qid,gid,users);
		return responses;
	}

	public static String getQuestionSelectedFromQid(ArrayList<SurveyQuestionsBean> questions,String qid)
	{
		SurveyQuestionsBean qs;
		String questionSelected="";
		for(int i=0;i<questions.size();i++)
		{
			qs = questions.get(i);
			if(qs.getQuestionId().trim().equals(qid))
			{
				questionSelected = qs.getQuestion();
				break;
			}
		}
		return questionSelected;
	}

	public static String generateCSVContents(String questionSelected,ArrayList<ResponseForAQuestionBean> responses,
			boolean allResponses, FilterParameters filters)
	{
		StringBuffer csvContents = new StringBuffer("");
		String strTemp="";
	
		if(!allResponses)
		{
			csvContents.append(filters);
		}
	
		ResponseForAQuestionBean response;
		ArrayList<SubQuestionsBean> subQuestions ;
		SubQuestionsBean subQs;
		CharSequence charSequence = ",";
		CharSequence charSequence1 = " ";
	
		response = responses.get(0);
		subQuestions = response.getSubQuestions();
		questionSelected = questionSelected.replace(charSequence, charSequence1);
		questionSelected = html2text(questionSelected);
		csvContents.append("Question Selected: "+questionSelected+"\r\n");
	
		csvContents.append(" ,");
		csvContents.append(" ,");
		for(int k=0;k<subQuestions.size();k++)
		{
			subQs = subQuestions.get(k);
			strTemp = subQs.getQuestion();
			strTemp = strTemp.replace(charSequence, charSequence1);
			csvContents.append(strTemp+",");
			if(k==subQuestions.size()-1)
				csvContents.append("\r\n");
		}
	
		for(int i=0;i<responses.size();i++)
		{
			response = responses.get(i);
			csvContents.append("\"\",");
			csvContents.append("\"" +response.getUserid()+ "\",");
			subQuestions = response.getSubQuestions();
			for(int j=0;j<subQuestions.size();j++)
			{
				subQs = subQuestions.get(j);
				if(subQs.getResponse().equals(""))
					csvContents.append("\"NA\",");
				else
					csvContents.append("\"" +subQs.getResponse()+ "\",");
			}
			csvContents.append("\n");
		}
		return csvContents.toString();
	
	}

	public static String html2text(String html) {
		return Jsoup.parse(html).text();
	}

	public static double calculateMean(String r, int s) {
		return Integer.parseInt(r)*1.00/s;
	}
	
	public static double calculateSD(ArrayList<Integer> data,double mean)
	{
		double sd;
		double sum =0.0;
		double difference;
		for(int j=0;j<data.size();j++)
		{
			difference = data.get(j)*1.0-mean;
			sum+=Math.pow(difference, 2);
		}
		sd = Math.pow(sum*1.0/data.size(), 0.5);

		return sd;
	}

	public static double getMedian(ArrayList<Integer> data)
	{
		double median = 0.0;
		Collections.sort(data);

		if(data.size()<=2)
		{
			median = data.get(0);
		}
		else if(data.size()%2==1)
			median = data.get(data.size()/2);
		else
			median = 1.0*(data.get(data.size()/2)+data.get(data.size()/2+1))/2;

		return median;
	}

	public static ArrayList<String> getParseUserIds(ArrayList<String> userids)
	{
		ArrayList<String> ids = new ArrayList<String>();
		String userid="";
		String [] parsedValues;
		for(int i=0;i<userids.size();i++)
		{
			userid = userids.get(i);
			parsedValues = userid.split("-");
			ids.add(parsedValues[0]);
		}
		return ids;
	}

	public static ArrayList<String> getUsersOfAGroup(int gId) {
		final ConnectionDAO connection = new ConnectionDAO();
		ArrayList<String> userids = connection.getusersOfAGroup(gId);
		return (getParseUserIds(userids));
	}

	public static void constructResponsesForStats(ArrayList<ResponseForAQuestionBean> responses,
			Hashtable<Integer,String> responsesForMean,
			Hashtable<Integer,ArrayList<Integer>> responsesForSD) 			
	{
		ResponseForAQuestionBean response;
		ArrayList<SubQuestionsBean> subQuestions ;
		SubQuestionsBean subQs;
		ArrayList<Integer> data;
		String tmp="";
		int rs = 0;
		
		if(responses == null || responses.size() == 0) {
			return;
		}

		//System.out.println("PPP responses are not null");
		for(int i=0;i<responses.size();i++)
		{
			response = responses.get(i);
			subQuestions = response.getSubQuestions();
			//System.out.println("QQQ subQs size = " + subQuestions.size());
			for(int j=0;j<subQuestions.size();j++)
			{
				subQs = subQuestions.get(j);
				if(!subQs.getResponse().equals(""))
				{
					//System.out.println("XXX subQ response: " + subQs.getResponse());
					if(responsesForSD.containsKey(j))
					{
						data = (ArrayList<Integer>)responsesForSD.get(j);
						data.add(Integer.parseInt(subQs.getResponse()));
						responsesForSD.put(j, data);
					}
					else
					{
						data = new ArrayList<Integer>();
						data.add(Integer.parseInt(subQs.getResponse()));
						responsesForSD.put(j, data);
					}
					if(responsesForMean.containsKey(j))
					{
						tmp = (String)responsesForMean.get(j);
						try
						{
							rs = Integer.parseInt(tmp) + Integer.parseInt(subQs.getResponse());
						}
						catch(NumberFormatException nfe)
						{
							nfe.printStackTrace();
						}
						responsesForMean.put(j, Integer.toString(rs));
					}
					else
					{
						responsesForMean.put(j, subQs.getResponse());
					}

				}
			}
		}
	}
}
