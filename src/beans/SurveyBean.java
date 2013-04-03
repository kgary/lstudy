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

/**
 * @author Srikesh Mandala(smandal2@exchange.asu.edu)
 *
 * The SurveyBean is a bean class consisting of 
 * five members surveyName,surveyURL,dateCompleted,
 * surveyDescription & dateCreated
 */
public class SurveyBean implements Serializable {

	private String surveyName;
	private String surveyURL;
	private String dateCompleted;
	private String surveyDescription;
	private String dateCreated;

	public String getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(String dateCreated) {
		this.dateCreated = dateCreated;
	}

	public String getSurveyDescription() {
		return surveyDescription;
	}

	public void setSurveyDescription(String surveyDescription) {
		this.surveyDescription = surveyDescription;
	}

	public String getDateCompleted() {
		return dateCompleted;
	}

	public void setDateCompleted(String dateCompleted) {
		this.dateCompleted = dateCompleted;
	}

	public String getSurveyName() {
		return surveyName;
	}

	public void setSurveyName(String surveyName) {
		this.surveyName = surveyName;
	}

	public String getSurveyURL() {
		return surveyURL;
	}

	public void setSurveyURL(String surveyURL) {
		this.surveyURL = surveyURL;
	}

}
