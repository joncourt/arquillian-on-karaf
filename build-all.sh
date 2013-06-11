#!/bin/bash

set -o errexit

mvn clean install -f arquillian-container-osgi/pom.xml
mvn clean install -f arquillian.karaf.blueprint/pom.xml
mvn clean install -f arquillian.karaf.blueprint.acceptance.test/pom.xml -Dmaven.test.skip=true