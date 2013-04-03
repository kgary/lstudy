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

/**
 * @author Srikesh Mandala(smandal2@exchange.asu.edu)
 *
 * The QuestionsBean is a bean class consisting of 
 * six members question1,question2,ownQuestion,answer1,
 * answer2 & ownAnswer
 */
public class QuestionsBean {

	private String question1;
	private String question2;
	private String ownQuestion;
	private String answer1;
	private String answer2;
	private String ownAnswer;

	public String getQuestion1() {
		return question1;
	}

	public void setQuestion1(String question1) {
		this.question1 = question1;
	}

	public String getQuestion2() {
		return question2;
	}

	public void setQuestion2(String question2) {
		this.question2 = question2;
	}

	public String getOwnQuestion() {
		return ownQuestion;
	}

	public void setOwnQuestion(String ownQuestion) {
		this.ownQuestion = ownQuestion;
	}

	public String getAnswer1() {
		return answer1;
	}

	public void setAnswer1(String answer1) {
		this.answer1 = answer1;
	}

	public String getAnswer2() {
		return answer2;
	}

	public void setAnswer2(String answer2) {
		this.answer2 = answer2;
	}

	public String getOwnAnswer() {
		return ownAnswer;
	}

	public void setOwnAnswer(String ownAnswer) {
		this.ownAnswer = ownAnswer;
	}

}
