Installation and Build Notes
============================

Author: Mark Donszelmann

Copy a version of AWSS
----------------------

   git clone .....

Setting up Nexus
----------------

Download nexus from ... and install. Start up the nexus server, and configure it.
Create file settings.xml for maven
Url http://localhost:8081/nexus admin, admin123
go to Users, right click admin, set password

repositories (extra)

Build AWSS
----------

Run `mvn clean install`

Run in demo-mode ???
--------------------

in apvs run `mvn clean gwt:run`

install the proper gwt plugin in firefox



Run the Jetty version
---------------------

   In the apvs-jetty directory run `java -jar target/apvs-jetty.war`

Release a version
-----------------

   Set JAVA_HOME to the proper java installation otherwise `mvn release:perform` will fail on calling `javadoc`


