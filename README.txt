Kevin Tivert
Application Title: SchedulerApp

PURPOSE:
This application was created in the aim to provide the user with a scheduling app. This app is capable
of connecting to the cloud ("wgudb.ucertify.com") and manage customers and appointments across multiple
locations and timezones. This app offers an appointment screen where user will be able to fill information
he/she desire to add or update, a report feature for customers and appointments information.
And lastly for security purposes this app also provide a log screen to protect user information privacy.

IDE:
- IntelliJ IDEA Community Edition 2020.3
- JavaFX SDK 11.0.2
- Java SE SDK 11.0.10
- mysql connector java 8.01

How to Run Application :
Main Method ->src/Main.java
Build and Run application in IDE of your choice.

*** UserName = Admin_Test
*** Password = PassWord

Additional Report: The additional report shows all the appointments that were created by the current user.
The addition of this report features makes it so that our user has a direct and easy access to his/her scheduled
appointment info.

NOTE: Lambda expressions are anonymous methods and therefore can not have dedicated javadoc comments, so their
justification is mentioned across all the classes they appear in. All the classes in the controller file
(/src/Controller) uses Lambda functions. Lambda functions are used to manage buttons behavior in this project.

# Description
                    
This application was created in the aim to provide the user with a scheduling app. This app is capable
of connecting to the cloud ("wgudb.ucertify.com") and manage user appointments across multiple
locations and timezones and reminding the user of any upcomming rendez-vous within the upcomming 30min. 
This app also offers an appointment screen where a user will be able to fill information
he/she desire to add or update, a report feature for customers and appointments information.
And lastly for security purposes this app also provide a log screen to protect user information privacy.
                    
# Goal
                    
- Create an application that enable a user to easily create, modify, or delete meetings.
- The application data needs to be stored on a cloud based sql server.
                    
# Challenges
 
- Build an application within the java ecosystem with cloud sql server capabilities.
- The app should also have more than 1 language enable.

# Solution Implemented

Since the app was design to be hosted on a computer and within the java ecosystem, the app is built with javafx 
for the user interface and using the java sql library to connect to a cloud sql server.
