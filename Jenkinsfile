
def containerBuild = "luke19/familyansparservice:${BUILD_NUMBER}"

pipeline {
  agent any

  environment {
    SONAR_TOKEN = credentials('sonarqube')
  }

  /*tools {
    maven 'localMaven' 
    sonar 'sonar'
    jdk 'jdk'
  }*/

  stages {

    stage('Checkout') {
      steps {
          checkout scm
      }
    }

    stage ('Compile Stage') {
      steps {
        script {
          try {
            sh "mvn clean compile"
          }catch (exc) {
            error('Clean compile failed' + exc.message)
          }
        }
      }
      
      
    }

    stage ('Test stage') {

      parallel {

        stage ('Check Secrets Stage') {
          steps {
            script{
              try {
                sh "rm trufflehog.txt || true"
                sh 'docker run --rm --name trufflehog dxa4481/trufflehog --regex https://github.com/KieniL/FamilyCluster_Ansparen.git > trufflehog.txt'
          
                publishHTML (target: [
                  allowMissing: false,
                  alwaysLinkToLastBuild: false,
                  keepAll: true,
                  reportDir: './',
                  reportFiles: 'trufflehog.txt',
                  reportName: "Trufflehog Report"
                ])
              }catch (exc) {
                error('Check secret failed' + exc.message)
              }   
            }
          }

        }

        stage ('Source Composition Analysis Stage') {
          steps {
            script{
              try{
                sh 'rm owasp* || true'
                sh 'wget "https://raw.githubusercontent.com/KieniL/FamilyCluster_Config/master/owasp-dependency-check.sh" '
                sh 'chmod +x owasp-dependency-check.sh'
                sh 'bash owasp-dependency-check.sh'
                
                publishHTML (target: [
                    allowMissing: false,
                    alwaysLinkToLastBuild: false,
                    keepAll: true,
                    reportDir: 'odc-reports',
                    reportFiles: 'dependency-check-report.html',
                    reportName: "OWASP Dependency Report"
                ])
              }catch (exc) {
                error('Source composition analysis failed' + exc.message)
              }
            }
          }
        }


        stage ('SAST') {
          steps {
            script{
              try{
                sh "mvn sonar:sonar -Dsonar.login=$SONAR_TOKEN"
                
                publishHTML (target: [
                    allowMissing: false,
                    alwaysLinkToLastBuild: false,
                    keepAll: true,
                    reportDir: 'target/sonar',
                    reportFiles: 'report-task.txt',
                    reportName: "Sonarscan Report"
                ])
              }catch (exc) {
                error('SAST failed' + exc.message)
              }
            }
          }
          
        }

        stage ('Maven Testing Stage') {
          steps {
            script{
              try{
                sh "rm test.txt || true"
                
                sh "mvn test  > test.txt"
                
                publishHTML (target: [
                    allowMissing: false,
                    alwaysLinkToLastBuild: false,
                    keepAll: true,
                    reportDir: './',
                    reportFiles: 'test.txt',
                    reportName: "Maven Test Report"
                ])
              }catch (exc) {
                error('Maven test failed' + exc.message)
              }
            }
          }
        }
      }
    }

    stage ('Packaging Stage') {
      steps {
        script{
          try{
            withDockerRegistry(credentialsId: 'docker', url: 'https://index.docker.io/v1/') {
                app = docker.build(containerBuild)
                app.push()
            }
          }catch (exc) {
            error('packaging failed' + exc.message)
          }
        }
      }
    }

    stage ('Analyzing Stage') {    
      steps {
        writeFile file: 'anchore_images', text: containerBuild
          anchore name: 'anchore_images'
      }
    }

  }
}

/*
  stage ('Deploying Stage') {
    try {
	  sh "docker run -d -p 8081:8081 --name ansparen ${containerBuild}"
  	}
    catch (exc) {
      error('Deploying failed' + exc.message)
    }
  }
 
  stage ('DAST') {
    try {
    
      sh "rm zap.txt || true"
      
	  sh "docker run --rm -t owasp/zap2docker-stable zap-full-scan.py -t http://localhost:8080/ > zap.txt"
  	}
    catch (exc) {
    }
    
    publishHTML (target: [
          allowMissing: false,
          alwaysLinkToLastBuild: false,
          keepAll: true,
          reportDir: './',
          reportFiles: 'zap.txt',
          reportName: "OWASP ZAP Report"
      ])
      
      sh "docker stop ansparen && docker rm ansparen"
  }

}
*/