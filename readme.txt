The following steps are to be followed for deploying the application Longitudinal study:

0) PRODUCTION DEPLOYMENT ONLY: If you are deploying to production, you should
remove the ROOT Tomcat web app and replace it with this one. To do this:
a) Start Tomcat
b) Select the Manager application
c) Use the manager tool to "Undeploy" the "/" application
d) Stop Tomcat
e) edit build.xml and change appname to "ROOT"
f) ant deploy
g) Start Tomcat
Voila! The longitudinal study application will now respond to requests on /
Make sure any static content does not redirect to /LongitudinalStudy

The next step is to answer requests on port 80. To do this on our linux
servers, make sure port 80 and port 80xx are open in the iptables config,
where 80xx is the non-privileged port (e.g. 8080) where Tomcat is running.
Then, as root on the box, issue this command:
iptables -t nat -A PREROUTING -i eth0 -p tcp --dport 80 -j REDIRECT --to-port 80xx

This redirects traffic from port 80 to port 80xx, yet you do not have to 
run the Tomcat instance as root (which you should never do).

1) Checkout the project from directory "svn co svn://lead2.poly.asu.edu/nsfccli/LongitudinalStudy" from SVN on lead2.

2) Edit the following properties in the properties file (longitudinalStudy.properties) in the directory \WebContent
    a) title - title of the application which is shown in the header of every page
    b) dBUserName - username for accessing the database
    c) dBPassword - password for accessing the database
    d) dBUrl - connection string of database (ex: jdbc:mysql://lead2.poly.asu.edu:3306/assessment_master_db)
    e) logoutMessage - message which is shown to the user on logout
    f) adminLogoutMessage - message which is shown to admin on logout
    g) hostUrl - url of the host including protocol and application name. This is used for generating url while triggering emails (ex: http://localhost:8080/LongitudinalStudy/Login?email=<emailid>)
    h) nsfccliAppAbsoluteURL - absolute url of nsfccli app
    g) Also edit the property "longitudinalStudyRedirectURl" in the directory \WebContent\WEB-INF directory of NSFCCLI app
3) Build it (ant war). Make sure it has the following jars before building:
   a) log4j.jar
   b) mail.jar
   c) mysql-connector-java-5.1.12-bin.jar
   d) servlet-api.jar - if you do not have this locally in the webapp then you
	nee to modify build.xml to point to a versio under tomcat

4) Append the following contents to context.xml of tomcat

<Resource
	auth="Container"
	name="jdbc/LongitudinalStudyDB"
	type="javax.sql.DataSource"
	driverClassName="com.mysql.jdbc.Driver"
	maxIdle="30"
	maxWait="1000"
	maxActive="4"
	validationQuery="select 1 from dual"
	url=<connection string ex:"jdbc:mysql://lead2.poly.asu.edu:3306/assessment_master_db">
	username=<username of database>
	password=<password>
/>

and make the file $TOMCAT_HOME/webapps/ROOT/index.html be:
<html>
  <head>
    <meta http-equiv="refresh" content="0;URL=/LongitudinalStudy">
  </head>
  <body>
  </body>
</html>

5) Make sure the production database backup file is executed.

6) Deploy the app (ant deploy). Make sure the tomcat.home property is set
   properly in build.xml first. 

7) Hit the login page: http://hostname:port/applicationName/Login
8) Hit the Registration page: http://hostname:port/applicationName/Registration
9) Hit the admin page: http://hostname:port/applicationName/Admin . username/password: admin/asupoly
10) Make sure survey is deployed on software enterprise server,tokens are added and survey is acccessible.
11) after the survey details are added to LS database through admin console make sure the following SQL script is executed
(this is the de-briefing message shown to the user after he has completed the survey.)
     
     update surveyinfo set briefingpage='<CENTER><h2>Software Enterprise Longitudinal Study Debrief</h2></CENTER><br><br>
 Thank you for taking the Software Enterprise Longitudinal Study Survey!<br><br>

A longitudinal study is one where participants are tracked over time in order to observe what events
 or changes occur in the population over that period. From these results, trending analysis are performed
 to determine if correlations can be shown between the environment and the effects. This longitudinal study
  will be used to determine, in part, if Software Enterprise pedagogy has an impact on the knowledge, skills,
   and career preparedness of students who participate.<br><br>

You will be asked to repeat this survey in one year\'s time in order to assess how your career attitudes and
 preparation have changed. You have the right to opt out of the study at any time. Your information, through the
 use of the secret id (sid) will remain anonymous; we have no way of associating an individual\'s responses to the ids.
 <br><br>

If you have any questions or concerns about the study, please contact Dr. Kevin Gary, kgary@asu.edu or (480)727-1373.<br><br>';

