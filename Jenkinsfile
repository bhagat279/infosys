pipeline {
    agent any

    tools {
        maven 'maventoo'
    }

    environment {
        AWS_ACCOUNT_ID = "339712886979"
        AWS_REGION = "ap-south-1"
        IMAGE_REPO = "${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/springboot-app"
    }

    triggers {
        cron('0 8 * * *') // Daily 8 AM deploy
    }

    options {
        // Clean workspace before build to avoid .git issues
        wipeWorkspace()
        buildDiscarder(logRotator(numToKeepStr: '10'))
    }

    stages {

        stage('Checkout') {
            steps {
                // Explicit Git clone using credentials
                checkout([
                    $class: 'GitSCM',
                    branches: [[name: '*/develop']],
                    doGenerateSubmoduleConfigurations: false,
                    userRemoteConfigs: [[
                        url: 'https://github.com/bhagat279/infosys.git',
                        credentialsId: 'github-cred'
                    ]]
                ])
            }
        }

        stage('Build & Unit Test') {
            steps {
                sh 'mvn clean install -DskipTests'
            }
        }

        stage('Docker Build & Push') {
            steps {
                withCredentials([[$class: 'AmazonWebServicesCredentialsBinding', credentialsId: 'aws-cred']]) {
                    sh '''
                    echo "Logging into AWS ECR..."
                    aws ecr get-login-password --region $AWS_REGION | docker login --username AWS --password-stdin $IMAGE_REPO

                    echo "Building Docker image..."
                    docker build -t springboot-app .

                    echo "Tagging Docker image..."
                    docker tag springboot-app:latest $IMAGE_REPO:latest

                    echo "Pushing Docker image to ECR..."
                    docker push $IMAGE_REPO:latest
                    '''
                }
            }
        }

        stage('Deploy to Staging') {
            steps {
                sh '''
                echo "Deploying to Staging..."
                helm upgrade --install staging-app ./helm \
                    --set image.repository=$IMAGE_REPO \
                    --set image.tag=latest
                '''
            }
        }

        stage('Approval for Production') {
            when { branch 'master' }
            steps {
                input message: "Deploy to Production?", ok: "Deploy"
            }
        }

        stage('Deploy to Production') {
            when { branch 'master' }
            steps {
                sh '''
                echo "Deploying to Production..."
                helm upgrade --install prod-app ./helm \
                    --set image.repository=$IMAGE_REPO \
                    --set image.tag=latest
                '''
            }
        }
    }

    post {
        always {
            echo "Cleaning workspace after build..."
            cleanWs()
        }
        success {
            echo "Pipeline completed successfully!"
        }
        failure {
            echo "Pipeline failed!"
        }
    }
}
