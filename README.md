# APVS / EDUSAFE


The APVS system, written with gwt interfaces a web portal to the APVS DAQ system.
It uses atmosphere to keep values read by the DAQ system up to date on a supervisor
portal and using mgwt on a number of worker iPad systems. The system contains a number
of simulators to mimic the DAQ behaviour and run the system without APVS DAQ.

## Preparation

### Copy a version of AWSS

   git clone git://github.com/CERN/apvs.git

### Install Maven

More info on maven: http://maven.apache.org/guides/getting-started/index.html 

### Install ojdbc driver (Oracle only)

Download ojdb6.jar from www.oracle.com and install in apvs-ojdbc/ojdbc6.jar

## Building the GUI

Change directory to the project and run:

	 <apvs project directory> > mvn clean install -Pgui
	
the project should be built in a couple of minutes, after downloading all the dependencies and plugins. 


## Running (inside Jetty)

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


## Database

The control app needs access to the database (Oracle):

TBL_DEVICES, TBL_USERS, TBL_INSPECTIONS (select, insert, update)
TBL_MEASUREMENTS, TBL_EVENTS, TBL_SENSORS (select)


