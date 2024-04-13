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
                    // Trivy scan command
                    sh "trivy image --format table --no-progress alanmath/account:${env.BUILD_ID} > trivy_report.txt"
                    // Print the Trivy scan results
                    sh "cat trivy_report.txt"
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
    }
}