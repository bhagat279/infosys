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

    options {
        // Always start with a clean workspace
        wipeWorkspace()
        buildDiscarder(logRotator(numToKeepStr: '10'))
        timestamps()
        ansiColor('xterm')
    }

    triggers {
        cron('0 8 * * *') // Daily 8 AM deploy
    }

    stages {

        stage('Checkout') {
            steps {
                script {
                    try {
                        checkout([
                            $class: 'GitSCM',
                            branches: [[name: "*/${env.BRANCH_NAME ?: 'master'}"]],
                            doGenerateSubmoduleConfigurations: false,
                            userRemoteConfigs: [[
                                url: 'https://github.com/bhagat279/infosys.git',
                                credentialsId: 'github-cred'
                            ]]
                        ])
                    } catch (err) {
                        error "Git checkout failed: ${err}"
                    }
                }
            }
        }

        stage('Build & Unit Test') {
            steps {
                script {
                    try {
                        sh 'mvn clean install -DskipTests'
                    } catch (err) {
                        error "Maven build failed: ${err}"
                    }
                }
            }
        }

        stage('Docker Build & Push') {
            steps {
                script {
                    try {
                        withCredentials([[$class: 'AmazonWebServicesCredentialsBinding', credentialsId: 'aws-cred']]) {
                            sh '''
                            #!/bin/bash
                            set -e

                            echo "Logging into AWS ECR..."
                            for i in {1..3}; do
                                if aws ecr get-login-password --region $AWS_REGION | docker login --username AWS --password-stdin $IMAGE_REPO; then
                                    echo "AWS ECR login successful"
                                    break
                                else
                                    echo "AWS ECR login failed, retrying... ($i)"
                                    sleep 5
                                fi
                            done

                            echo "Building Docker image..."
                            docker build -t springboot-app .

                            echo "Tagging Docker image..."
                            docker tag springboot-app:latest $IMAGE_REPO:latest

                            echo "Pushing Docker image to ECR..."
                            docker push $IMAGE_REPO:latest
                            '''
                        }
                    } catch (err) {
                        error "Docker build/push failed: ${err}"
                    }
                }
            }
        }

        stage('Deploy to Staging') {
            steps {
                script {
                    try {
                        sh '''
                        echo "Deploying to Staging..."
                        helm upgrade --install staging-app ./helm \
                            --set image.repository=$IMAGE_REPO \
                            --set image.tag=latest
                        '''
                    } catch (err) {
                        error "Staging deployment failed: ${err}"
                    }
                }
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
                script {
                    try {
                        sh '''
                        echo "Deploying to Production..."
                        helm upgrade --install prod-app ./helm \
                            --set image.repository=$IMAGE_REPO \
                            --set image.tag=latest
                        '''
                    } catch (err) {
                        error "Production deployment failed: ${err}"
                    }
                }
            }
        }
    }

    post {
        always {
            echo "Cleaning workspace..."
            cleanWs()
        }
        success {
            echo "Pipeline completed successfully!"
        }
        failure {
            echo "Pipeline failed! Check logs for errors."
        }
    }
}
