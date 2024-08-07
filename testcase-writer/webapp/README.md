## Test Case Writer webapp

# Structure
 * [backend-core](backend-core): the application services, which relies on the tcwriter core and javatc modules
 * [api-v0](api-v0): the application's openapi specifications, used to generate the java/js apis and the backend's skeleton.
 * [back-end](backend): the backend's war, which includes the web configuration. It relies on the backend-core and api-v0-back-end
 * [front-end](frontend): a basic react front-end, which relies on the js generated by api-v0-webapi
 * [war](war): the full application, which includes the back-end and front-end
 * [tests](tests): the webapp's end 2 end tests and test configuration bootstrap
 
# Back-end
The back-end is a SpringBoot web application that can be started using ch.scaille.tcwriter.server.Server

# Front-end
The front-end is a React application. It connects to the backend using REST apis.

# Tests 
The tests are made of selenium tests, which rely on the lib's selenium pilot.

The test module contains a Spring Configuration that can bootstrap a test setup. The test server can be started using mvn spring-boot:run .

# Versioned apis
To create a new version of the apis:
 * copy the folder api-v0, i.e. into api-v1
 * rename the controller packages 
 * in the api-v1 folder, use "sed -i 's/v0/v1/' \`find . -type f\`"
 * edit the pom.xml to change the property apiVersion to 1
 * include the artifact testcase-writer-webapp-api-v0-backend in the backend module
 * edit the package.json file to use the new api version



