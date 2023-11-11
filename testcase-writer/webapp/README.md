## Test Case Writer webapp

# Structure
 * [api](api): the application interface, which generates the java/ts models for the backend and frontend
 * [backend](backend): the application backend, which relies on the tcwriter core and javatc modules
 * [frontend](frontend): a basic react frontend
 * [war](war): the full application
 * [test](test): the webapp's end 2 end tests 
 
 
# The Backend
The backend is a SpringBoot web application that can be started using ch.scaille.tcwriter.server.Server

The class ch.scaille.tcwriter.server.bootstrap.ExampleBootstrap
