Installation and Build Notes
============================

Author: Mark Donszelmann

Copy a version of AWSS
----------------------

   git clone .....

Setting up Nexus
----------------

Download nexus from ... and install. Start up the nexus server, and configure it.
Create file settings.xml for maven in ~/.m2/settings.xml

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

Url http://localhost:8081/nexus admin, admin123
go to Users, right click admin, set password

Build AWSS
----------

Run `mvn clean install`

Run in demo-mode ???
--------------------

in apvs run `mvn clean gwt:run`

install the proper gwt plugin in firefox
http://acleung.com/ff14-mac.xpi


Run the Jetty version
---------------------

   In the apvs-jetty directory run `java -jar target/apvs-jetty.war`

Release a version
-----------------

   Set JAVA_HOME to the proper java installation otherwise `mvn release:perform` will fail on calling `javadoc`


