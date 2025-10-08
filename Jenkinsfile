pipeline {
    agent any
    tools {
        maven 'maventoo'
    }

    environment {
        AWS_ACCOUNT_ID = "339712886979"
        AWS_REGION = "ap-south-1"
        IMAGE_REPO = "${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/springboot-app"
        KUBECONFIG = "/root/.kube/config" // helm/kubectl ke liye
    }

    triggers {
        cron('0 8 * * *') // Daily 8 AM deploy
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
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
                    aws ecr get-login-password --region $AWS_REGION | docker login --username AWS --password-stdin $IMAGE_REPO
                    docker build -t springboot-app .
                    docker tag springboot-app:latest $IMAGE_REPO:latest
                    docker push $IMAGE_REPO:latest
                    '''
                }
            }
        }

        stage('Deploy to Staging') {
            steps {
                sh '''
                helm upgrade --install staging-app ./helm \
                  --set image.repository=$IMAGE_REPO \
                  --set image.tag=latest
                '''
            }
        }

        stage('Approval for Prod') {
            when { branch 'master' }
            steps {
                input "Deploy to Production?"
            }
        }

        stage('Deploy to Prod') {
            when { branch 'master' }
            steps {
                sh '''
                helm upgrade --install prod-app ./helm \
                  --set image.repository=$IMAGE_REPO \
                  --set image.tag=latest
                '''
            }
        }
    }
}
