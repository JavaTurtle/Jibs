                                R E A D M E

            The j(ava I(nternet B(ackgammon S(server Version 0.5.1

INTRODUCTION

     jIBS is a Java based Internet Backgammon Server, it allows Internet 
     users to play backgammon in real-time against real people (and even 
     some bots). It is a clone of the already well-known backgammon server 
     FIBS.

     Most jIBS players today use a graphical interface to allow the easy 
     'point and click' approach. jIBS players can choose from a number of 
     excellent graphical interfaces, making jIBS backgammon a more natural and 
     enjoyable experience.
     
     Therefore you can still use your favourite (FIBS)-client application to 
     use with jIBS (JavaFIBS, 3dFiBs, RealFIBS, BBGT, MacFIBS,...). I highly
     recommend to use a graphical client as a plain telnet session is no fun, 
     though it might be the only way to register with jIBS.

     On the jIBS server there are not only human players, but also a number
     of computer players, often referred to as bots. With current technology,
     most bots play at a very high level, close to or maybe even surpassing
     the best players in the world. 
     
     It is my hope that with jIBS you can start developing a bot to 
     participate on jIBS or FIBS!
     
Startup
       o You can start jIBS by invoking the script 
            $JIBSHOME/bin/runjIBS.bat (Windows)
            $JIBSHOME/bin/runjIBS.sh  (*nix)       	
       
                o Most parameters of jIBS can be changed by altering it's main 
         configuration file. This file "jibs.properties" can be found in 
         the conf subdirectory and is well documented.

Shutdown
	There is a menu point to exit the server. Keep in mind that this will
	disconnects all online player without warning, which probably they will 
	find "surprising" at least. So I recommend to use the shutdown command
	instead. Only user with admin privileges can do so and you must grant users
	these rights in the database as there is no command in FIBS defined to do 
	that. If you want to be able to use the shutdown commanmd from within jIBS 
	and probably restart JIBS automatically afterwards, I recommend to 
    start jIBS with the jIBSLauncher.jar file instead. Simply do this with 
    the command:
		java -jar JibsLauncher.jar
                
LOG
     jIBS uses the log4j framework to log into the log files defined in the 
     $JIBS_HOME/log4j.properties file. I recommend to look into it whenever 
     you think that something wrong has happened. Therefore if  you can't start 
     jIBS you should definitely have a look into it!
         
DISTRIBUTION CONTENTS

     This distribution contains:
       o LICENSE.txt           - license agreement 
       
       o README.txt            - this readme file
       
       o lib                   - sub-directory with the required JARs 
                                 to run jIBS
         o jibs.jar            - main JAR 
         o hsqldb.jar          - HSQLDB-database JAR
         o ibatis-common-2.jar - iBATIS Data Mapper framework
           ibatis-sqlmap-2.jar - iBATIS Data Mapper framework
         o llog4j-1.2.12.jar   - logging framework
         o forms-1.0.6.jar     - jgoodies Forms layout Manager
         o formsrt.jar         - abeille GUI runtime library
         
       o images                - sub-directory with required images
         
       o conf
         o jibs.properties     - main configuration file
         o jibs_msg.properties - message file used by jIBS
         o login.txt           - logo screen 
         o logout.txt          - logout screen
         o motd.txt            - message-of-the-day
         o splash.jpg          - splash screen
         
       o database              
         o hsqldb			   - the embedded HSQLDB database
         	o createPlayer.script - scripts to generate the database schema
         	o createSavedGames.script
         	o runManager.bat      - execute the HSQLDB Manager application
         	o runServer.bat       - execute the HSQLDB database in server mode 
         o Player.xml			  - iBATIS mapping file 
         o Saved_Games.xml		  - iBATIS mapping file
         o SqlMapConfig.xml       - iBATIS configuration file
         
       o src                   - the entire source files of jIBS
       
       o bin
         o runjIBS.bat         - script to start jIBS (Windows)       
         o runjIBS.sh          - script to start jIBS (Unix)

DATABASE
     jIBS uses a JDBC compliant database to store players, their rating and 
     interrupted game states. jIBS comes with a pre-defined HSQLDB database. If
     you ever want to setup your own database you can easily do so. jIBS uses
     iBATIS to map Java code to SQL statements. You have to tweak this 
     configuration if you want to change the database jIBS should use.
         
CREDITS
     Many people have helped me with jIBS:
     
     o Peter Nevalainen        - author of the great JavaFIBS client, who has
                                 helped developing the registration code. He
                                 also helped me analyzing an error which occured
                                 with his client. His client was not at fault,
                                 but so was my server :-(
                                 
     o Evan McLean             - for his execellent documentation on the 
                                 FIBS protocol.

     o Mike Quinn              - also for his good documentation of the man-
                                 pages for all commands.   							 
     
     o Karsten Lentzsch        - for his jgoodies framework which probably 
                                 is the best layout manager around.

     o Jeff Tassin             - for his abeille forms designer
                                 which completely supports the above mentioned 
                                 layout manager.

     o log4j                   - the entire team who has come up with this
                                 logging framework.
                                 
     o hsqldb                  - the entire team who has come up with this
                                 small but great Java based JDBC compliant
                                 database.

     o iBATIS                  - the team which makes it unnecessary to write
                                 JDBC code.
                                 
                                          
Copyright (c) 2006 Axel Leucht.                             All rights reserved.