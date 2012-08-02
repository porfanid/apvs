Installation and Build Notes
============================

Author: Mark Donszelmann

Run the Jetty version
---------------------

   In the apvs-jetty directory run `java -jar target/apvs-jetty.war`

Release a version
-----------------

   Set JAVA_HOME to the proper java installation otherwise `mvn release:perform` will fail on calling `javadoc`


