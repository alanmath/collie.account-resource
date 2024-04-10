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
                build job: 'collie.account', wait: true
            }
        }
        stage ('Build') {
            steps {
                sh 'mvn clean package'
            }
        }
    }
}