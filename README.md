# APVS

The APVS system, written with gwt interfaces a web portal to the APVS DAQ system.
It uses atmosphere to keep values read by the DAQ system up to date on a supervisor
portal and using mgwt on a number of worker iPad systems. The system contains a number
of simulators to mimic the DAQ behaviour and run the system without APVS DAQ.

## Preparation

### Copy a version of AWSS

   git clone git://github.com/CERN/apvs.git
   
### Setup Nexus

Download nexus from http://www.sonatype.org/nexus/ and install.

* On Windows install and start nexus by running the command using admin privileges, (Right mouse click on application->Run as Administrator):
	
    .../nexus-<version number>/bin > nexus install
    .../nexus-<version number>/bin > nexus start

* On MacOS install and start nexus by creating a file ~/Library/LaunchAgents/nexus.plist with the following content:

```xml
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
```

then reboot to start up nexus.

Nexus will be availble under: http://localhost:8081/nexus, log in with admin/admin123, go to Users, right click admin, and set a password.

### Setup Maven

* Create file settings.xml for maven in ~/.m2/settings.xml with the following content:

```xml
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
```

Note: Windows users should make sure the .m2 directory is on a local disk (and not DFS) by changing the system settings.xml to point to aa local repo directory

	<localRepository>/path/to/local/repo/</localRepository>

Further info in: http://maven.apache.org/guides/mini/guide-configuring-maven.html

More info on maven: http://maven.apache.org/guides/getting-started/index.html 

## Building

Change directory to the project and run:

	 <apvs project directory> > mvn clean install
	
the project should be built in a couple of minutes, after downloading all the dependencies and plugins. 


## Running

There are two ways to run APVS: demo-mode where a plugin is needed in the browser and which is typically used 
to have a quick edit-compile-debug cycle, and normal-mode, which runs in jetty, is faster but takes more time
to compile. 

### Run in demo-mode

Run:

	<apvs project directory>/apvs > mvn clean gwt:run
	
Using FireFox open 

	http://localhost:8888/apvs/index.html?gwt.codesvr=localhost:9997
	install the proper gwt plugin in firefox

Mac: http://acleung.com/ff14-mac.xpi
Windows: http://acleung.com/ff14-win.xpi

These plugins change all the time, just use any higher version number when needed. 

### Run the Jetty version

Run:
	<apvs project directory>/apvs-jetty > java -jar target/apvs-jetty.war

Using any browser open

	http://localhost:8095/apvs/index.html


## Release a version

Set JAVA_HOME to the proper java installation otherwise `mvn release:perform` will fail on calling `javadoc`
