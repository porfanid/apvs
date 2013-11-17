#!/bin/sh
java -Xmx1024M  -Dlogback.configurationFile=logback.xml -jar apvs-jetty/target/apvs-jetty.war
