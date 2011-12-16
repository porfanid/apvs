#/bin/sh

find . -name *.xml | xargs grep -slZ 0.1 | xargs -0 sed -i -e 's/0\.1\.1\.1/0\.1\.1-SNAPSHOT/g'
find . -name pom.xml-e | xargs rm

