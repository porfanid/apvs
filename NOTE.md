Installation and Build Notes
============================

Author: Mark Donszelmann

______________________________________________________________
--------------------------- STEP 1 ---------------------------
Copy a version of AWSS
----------------------
   git clone .....
   
______________________________________________________________
--------------------------- STEP 2 ---------------------------	
Setting up Nexus
----------------

1. Download nexus from http://www.sonatype.org/nexus/ and install.

On Windows:
To install nexus run the command "nexus install" under the folder ".../nexus-<version number>/bin"

Remark: 
Windows users should be running command prompt with administrator priviledges(Right mouse click on application->Run as Administrator)

On MacOS:
in ~/Library/LaunchAgents create a file nexus.plist with the following content:

	<?xml version="1.0" encoding="UTF-8"?>
	<!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
	<plist version="1.0">
	<dict>
		<key>KeepAlive</key>
		<false/>
		<key>Label</key>
		<string>nexus</string>
		<key>ProgramArguments</key>
		<array>
			<string>/...absolute directory to nexus installation.../nexus/bin/nexus</string>
			<string>start</string>
		</array>
		<key>RunAtLoad</key>
		<true/>
	</dict>
	</plist>

2. Start up the nexus server (run the command "nexus start").

3.Create file settings.xml for maven in ~/.m2/settings.xml. PLEASE READ REMARK AT THE END OF THIS STEP.

	<settings>
	
	  <mirrors>
	    <mirror>
	      <!--This sends everything else to /public -->
	      <id>nexus</id>
	      <mirrorOf>*</mirrorOf>
	      <url>http://localhost:8081/nexus/content/groups/public</url>
	    </mirror>
	  </mirrors>
	
	  <profiles>
	    <profile>
	      <id>nexus</id>
	      <!--Enable snapshots for the built in central repo to direct -->
	      <!--all requests to nexus via the mirror -->
	      <repositories>
	        <repository>
	          <id>central</id>
	          <url>http://central</url>
	          <releases><enabled>true</enabled></releases>
	          <snapshots><enabled>true</enabled></snapshots>
	        </repository>
	      </repositories>
	     <pluginRepositories>
	        <pluginRepository>
	          <id>central</id>
	          <url>http://central</url>
	          <releases><enabled>true</enabled></releases>
	          <snapshots><enabled>true</enabled></snapshots>
	        </pluginRepository>
	      </pluginRepositories>
	    </profile>
	  </profiles>
	    
	  <activeProfiles>
	    <!--make the profile active all the time -->
	    <activeProfile>nexus</activeProfile>
	  </activeProfiles>
	</settings>

Remark: 
WINDOWS USERS USING CERN DFS AS HOME DIRECTORY MIGHT EXPERIENCE SOME ISSUES DURING BUILD PROCESS. THEREFORE, THEY SHOULD CHANGE THE LOCAL REPOSITORY TO A LOCAL DIRECTORY BY ADDING A SPECIFIED LOCATION ON "setting.xml" FILE 

	<localRepository>/path/to/local/repo/</localRepository>

Further info in: http://maven.apache.org/guides/mini/guide-configuring-maven.html
If using a network drive (e.g. CERN DFS) as a home directory and still want to be able to compile when the network is not available, you must make sure these files are available offline. To enable this feature for CERN DFS please follow the following tutorial: http://www.sevenforums.com/tutorials/48852-offline-files-make-files-folders-available-offline.html

4. Url http://localhost:8081/nexus admin, admin123 go to Users, right click admin, set password

______________________________________________________________
--------------------------- STEP 3 ---------------------------
Build AWSS
----------

1. Readers without any experience with maven are strongly recommended to follow the start guide before continue to the next step: http://maven.apache.org/guides/getting-started/index.html 

2. Run `mvn clean install`
______________________________________________________________
--------------------------- STEP 4 ---------------------------
Run in demo-mode ???
--------------------

1. In <apvs project directory>/apvs  run `mvn clean gwt:run`

install the proper gwt plugin in firefox
http://acleung.com/ff14-mac.xpi -> Mac Version
http://acleung.com/ff14-win.xpi -> Windows Version
______________________________________________________________
--------------------------- STEP 5 ---------------------------
Run the Jetty version
---------------------

1. In the <apvs project directory>/apvs-jetty  run `java -jar target/apvs-jetty.war`

______________________________________________________________
--------------------------- STEP 6 ---------------------------
Release a version
-----------------

1. Set JAVA_HOME to the proper java installation otherwise `mvn release:perform` will fail on calling `javadoc`

Remarks:
For Windows users: 
Go to control panel->System->Advanced System Settings->(Tab Advanced) Environment Variables->User Variable
Name: JAVA_HOME
Value: <path to java installation>
