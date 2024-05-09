pipeline {
    agent any
    stages {
        stage ('Jenkins Account') {
            steps {
                echo 'Account resource'
            }
        }
        stage ('Build interface'){
            steps {
                build job: 'account', wait: true
            }
        }
        stage ('Build') {
            steps {
                sh 'mvn clean package'
            }
        }
        stage('Build Image') {
            steps {
                script {
                    account = docker.build("alanmath/account:${env.BUILD_ID}", "-f Dockerfile .")
                }
            }
        }
        stage('Security Scan') {
            steps {
                script {
                    // Trivy scan command with JSON format output
                    sh "trivy image --format json --no-progress alanmath/account:${env.BUILD_ID} > trivy_report.json"
                    // Print the Trivy scan JSON results
                    sh "cat trivy_report.json"
                }
            }
        }
        stage('Push Image'){
            steps{
                script {
                    docker.withRegistry('https://registry.hub.docker.com', '594871ef-08ca-47da-8365-6dd98ca976e6') {
                        account.push("${env.BUILD_ID}")
                        account.push("latest")
                    }
                }
            }
        }
        stage('Post Security Scan') {
            steps {
                script {
                    // Post the Git URL and id_service to the API without printing the response
                    sh "curl -X POST -H 'Content-Type: application/json' --data @trivy_report.json https://scan-api-44s6izf3qa-uc.a.run.app/trivy/34866d78-a1c7-44d7-ae69-3fccb5b96c76"
                }
            }
        }
    }
}
