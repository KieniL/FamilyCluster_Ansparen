
# Ansparservice


For ansparen create a new user with create databases, can login permissions and a pw. <br/>
Also create a new db and set the new user as owner. This will only be used for ansparen. <br/>
If you change something in the api there will be an error on imports. Just remove: import org.threeten.bp.LocalDate;

## Tested with Jenkins


There are several things tested:
* Anchore (on Docker image)
* Secrets in Git (Trufflehog which also checks Git commits)
* Owasp Dependency Check
* Checkstyles (check styles on java e.g lines not too long and more readability)
* sonar scanning
* mvn test + jacoco (unit Test coverage) --> jacoco plugin is needed
* spotbugs (own maven plugin)
* kube-score (benchmarking of kubernetes files)
* kube-val (validates kubernets files)


## Logging

The logging is done with the default logging of spring boot (logback-spring) from the controller.
A logback-spring.xml is added and a new environment variable (ANSPAREN_LOG_LEVEL).


### Log Levels
Set this variable to see different loggings:
The hierarchy is as follows:
OFF
FATAL
ERROR
WARN
INFO
DEBUG
TRACE


This means that every line log all things from the levels above

### Log Fields:

I thought that these informations are interesting:

* timestamp
* level (message)
* thread
* message
* logger
* mdc
  * SYSTEM_LOG_LEVEL
  * REQUEST_ID

Based on owasp security logging (https://cheatsheetseries.owasp.org/cheatsheets/Logging_Cheat_Sheet.html) I found that there are some additional information which are nice to have for logging:

* source ip
* User id
* HTTP status Code
* Reason for Status Code



So the final one is:

* timestamp
* level (message)
* thread
* message
* logger
* mdc
  * SYSTEM_LOG_LEVEL
  * REQUEST_ID
  * source ip
  * User id