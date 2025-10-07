pipeline {
    agent any
    stages {
        stage('Test Git') {
            steps {
                git branch: 'develop',
                    url: 'https://github.com/bhagat279/infosys.git',
                    credentialsId: 'github-cred'
            }
        }
    }
}
