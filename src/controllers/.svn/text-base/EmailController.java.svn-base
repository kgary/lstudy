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

package controllers;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;

import dao.ConnectionDAO;

import util.LoggerUtil;

/**
 * @author Srikesh Mandala(smandal2@exchange.asu.edu)
 *
 * The EmailController class consists of a method 
 * sendEmail() which sends emails using ASU SMTP server.
 */
public class EmailController {
	private static Logger logger = LoggerUtil.getClassLogger();
	private ConnectionDAO connection = new ConnectionDAO();

	/**
     * The method sendEmail sends emails using 
     * ASU SMTP server
     * @param  toAddress - destination email address,
     *         fromAddress - email address from which
     *                        emails are sent
     *         subject - subject of the email
     *         body - content of the email
     * @return none
     */
	public void sendEmail(String toAddress, String fromAddress, String subject,
			String body) {
		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.asu.edu");
		Session session = Session.getDefaultInstance(props, null);
		Message msg = new MimeMessage(session);
		try {
			InternetAddress iaAddressFrom = new InternetAddress(fromAddress);
			msg.setFrom(iaAddressFrom);
			InternetAddress iaAddressTo = new InternetAddress(toAddress);
			msg.setRecipient(Message.RecipientType.TO, iaAddressTo);
			msg.setSubject(subject);
			msg.setContent(body, "text/plain");
			Transport.send(msg);
			logger.info("email to: " + toAddress + " from: " + fromAddress
					+ " sent successfully with body: " + body);
			updateEmailSent(toAddress);
		} catch (Exception e) {
			logger.error("Exception while sending email to: "+toAddress+" " + e.getMessage());
		}
	}
	
	private void updateEmailSent(String emailid)
	{
		connection.updateEmailSent(emailid);
	}
}
