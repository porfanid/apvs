# APVS

The APVS system, written with gwt interfaces a web portal to the APVS DAQ system.
It uses atmosphere to keep values read by the DAQ system up to date on a supervisor
portal and using mgwt on a number of worker iPad systems. The system contains a number
of simulators to mimic the DAQ behaviour and run the system without APVS DAQ.

## Preparation

### Copy a version of AWSS

   git clone git://github.com/CERN/apvs.git

### Install Maven

More info on maven: http://maven.apache.org/guides/getting-started/index.html 

### Install ojdbc driver

We have this one somewhere on DropBox

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

	http://localhost:8888/index.html?gwt.codesvr=localhost:9997
	install the proper gwt plugin in firefox

### Run the Jetty version

Configure:
	copy EgroupCheckConfExample.properties to EgroupCheckConf.properties and edit username and pwd to access the supervisors e-group

Run:
	<apvs project directory>/run-prod.sh

Using any browser open

	http://localhost:8095/index.html


## Release a version

Set JAVA_HOME to the proper java installation otherwise `mvn release:perform` will fail on calling `javadoc`

Make sure to run with -Pprod


## Run behind apache

Configure:
        shibolleth
	copy httpd-proxy-example.conf to httpd-proxy.conf and edit to point to correct video and apvs server
	add pointer from httpd.conf to httpd-procy.conf


