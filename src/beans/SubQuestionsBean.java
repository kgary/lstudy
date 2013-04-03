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

package beans;

import java.io.Serializable;

public class SubQuestionsBean implements Serializable{
	
	public SubQuestionsBean()
	{
		
	}
	
	public SubQuestionsBean(String questionId,String question,String response,String userid)
	{
		this.question = question;
		this.questionId = questionId;
		this.response = response;
		this.userid = userid;
	}

	private String questionId;
	private String question;
	private String response;
	private String userid;
	
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getQuestionId() {
		return questionId;
	}
	public void setQuestionId(String questionId) {
		this.questionId = questionId;
	}
	public String getQuestion() {
		return question;
	}
	public void setQuestion(String question) {
		this.question = question;
	}
	public String getResponse() {
		return response;
	}
	public void setResponse(String response) {
		this.response = response;
	}
	
}
