
node {
    def app
    def mvnTool = tool 'localMaven'
    def sonar = tool 'sonar'
    def jdk = tool 'jdk'
    def containerBuild = "luke19/familyanspareservice:${BUILD_NUMBER}"

  

    checkout scm
  
    stage ('Compile Stage') {
        try {
            sh "${mvnTool}/bin/mvn clean compile --file ansparen/pom.xml"
        }
        catch (exc) {
            error('Clean compile failed' + exc.message)
        }
    }	
	
    stage ('Check Secrets Stage') {
        sh "rm trufflehog.txt || true"
        try {
            sh 'docker run --rm --name trufflehog dxa4481/trufflehog --regex https://github.com/KieniL/Family-Cluster.git > trufflehog.txt' 	  
        }catch (exc) {
        }   
    
    publishHTML (target: [
        allowMissing: false,
        alwaysLinkToLastBuild: false,
        keepAll: true,
        reportDir: './',
        reportFiles: 'trufflehog.txt',
        reportName: "Trufflehog Report"
      ])
  }	


    stage ('Source Composition Analysis Stage') {
        try {
            sh 'rm owasp* || true'
            sh 'bash ./owasp-dependency-check.sh'
            
            publishHTML (target: [
                allowMissing: false,
                alwaysLinkToLastBuild: false,
                keepAll: true,
                reportDir: 'odc-reports',
                reportFiles: 'dependency-check-report.html',
                reportName: "OWASP Dependency Report"
            ])
        }
        catch (exc) {
            error('Source Composition Analysis failed' + exc.message)
        }
  }
 


  
  
    stage ('SAST') {
        sh "${mvnTool}/bin/mvn sonar:sonar --file ansparen/pom.xml -Dsonar.login=$SONAR_TOKEN"
        
        publishHTML (target: [
            allowMissing: false,
            alwaysLinkToLastBuild: false,
            keepAll: true,
            reportDir: 'target/sonar',
            reportFiles: 'report-task.txt',
            reportName: "Sonarscan Report"
        ])
  }
  
  
  


    stage ('Testing Stage') {
        try {
            sh "rm test.txt || true"
            
            sh "${mvnTool}/bin/mvn test --file ansparen/pom.xml > test.txt"
            
            publishHTML (target: [
                allowMissing: false,
                alwaysLinkToLastBuild: false,
                keepAll: true,
                reportDir: './',
                reportFiles: 'test.txt',
                reportName: "Maven Test Report"
            ])
            }
        catch (exc) {
            error('Testing failed' + exc.message)
        }
  }
  
  stage ('Packaging Stage') {

    sh "${mvnTool}/bin/mvn clean package --file ansparen/pom.xml"
    try {
	  docker.withDockerRegistry(credentialsId: 'docker', url: 'https://index.docker.io/v1/') {
	    app = docker.build(containerBuild)
        app.push()
	  }
  	}
    catch (exc) {
      error('Packaging failed' + exc.message)
    }
  }
  
  stage ('Analyzing Stage') {    
    try {
	  writeFile file: 'anchore_images', text: containerBuild
  	  anchore name: 'anchore_images'
  	}
    catch (exc) {
      error('Packaging failed. ' + exc.message)
    }
  }
  
  
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