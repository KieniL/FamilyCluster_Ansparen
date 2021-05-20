
# Ansparservice

## Tested with Jenkins


There are several things tested:
* Anchore (on Docker image)
* Secrets in Git (Trufflehog which also checks Git commits)
* Owasp Dependency Check
* Checkstyles (check styles on java e.g lines not too long and more readability)
* sonar scanning
* mvn test + jacoco (unit Test coverage)
* 
