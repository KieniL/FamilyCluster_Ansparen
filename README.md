
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
