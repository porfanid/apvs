#!/bin/sh
export VER=1.1.0
export SHA=5b9299
cp ~/cern/git/atmosphere/modules/cpr/target/atmosphere-runtime-${VER}-SNAPSHOT.jar atmosphere-runtime-${VER}-${SHA}.jar
cp ~/cern/git/atmosphere-extensions/gwt/modules/atmosphere-gwt-client/target/atmosphere-gwt-client-${VER}-SNAPSHOT.jar atmosphere-gwt-client-${VER}-${SHA}.jar
cp ~/cern/git/atmosphere-extensions/gwt/modules/atmosphere-gwt-common/target/atmosphere-gwt-common-${VER}-SNAPSHOT.jar atmosphere-gwt-common-${VER}-${SHA}.jar
cp ~/cern/git/atmosphere-extensions/gwt/modules/atmosphere-gwt-server/target/atmosphere-gwt-server-${VER}-SNAPSHOT.jar atmosphere-gwt-server-${VER}-${SHA}.jar
cp ~/cern/git/atmosphere/modules/compat-jbossweb/target/atmosphere-compat-jbossweb-${VER}-SNAPSHOT.jar atmosphere-compat-jbossweb-${VER}-${SHA}.jar
cp ~/cern/git/atmosphere/modules/compat-tomcat/target/atmosphere-compat-tomcat-${VER}-SNAPSHOT.jar atmosphere-compat-tomcat-${VER}-${SHA}.jar
cp ~/cern/git/atmosphere/modules/compat-tomcat7/target/atmosphere-compat-tomcat7-${VER}-SNAPSHOT.jar atmosphere-compat-tomcat7-${VER}-${SHA}.jar
