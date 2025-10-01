pipeline {
    agent any
    stages {
        stage('Build') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Docker Build & Push') {
            steps {
                script {
                    def branch = env.GIT_BRANCH.replaceAll("origin/", "")
                    def tag = (branch == "main") ? "prod" : "staging"

                    sh """
                    aws ecr get-login-password --region <REGION> | docker login --username AWS --password-stdin <AWS_ACCOUNT_ID>.dkr.ecr.<REGION>.amazonaws.com
                    docker build -t <AWS_ACCOUNT_ID>.dkr.ecr.<REGION>.amazonaws.com/myapp:${tag} .
                    docker push <AWS_ACCOUNT_ID>.dkr.ecr.<REGION>.amazonaws.com/myapp:${tag}
                    """
                }
            }
        }

        stage('Deploy to EKS') {
            steps {
                script {
                    def branch = env.GIT_BRANCH.replaceAll("origin/", "")
                    def namespace = (branch == "main") ? "production" : "staging"
                    def tag = (branch == "main") ? "prod" : "staging"

                    sh """
                    aws eks update-kubeconfig --region <REGION> --name <EKS_CLUSTER_NAME>

                    helm upgrade --install myapp ./helm \
                    --namespace ${namespace} --create-namespace \
                    --set image.repository=<AWS_ACCOUNT_ID>.dkr.ecr.<REGION>.amazonaws.com/myapp \
                    --set image.tag=${tag}
                    """
                }
            }
        }
    }
}
