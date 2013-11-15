#!/bin/sh
java -Xmx1024M  -Dlogback.configurationFile=logging.xml -jar apvs-jetty/target/apvs-jetty.war
