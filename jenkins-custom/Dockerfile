FROM jenkins/jenkins:lts-jdk17

# Switch to root to install dependencies
USER root

# Install Docker CLI, AWS CLI, unzip, curl, and Helm
RUN apt-get update && \
    apt-get install -y docker.io unzip curl && \
    curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip" && \
    unzip awscliv2.zip && ./aws/install && \
    curl https://raw.githubusercontent.com/helm/helm/main/scripts/get-helm-3 | bash && \
    rm -rf awscliv2.zip aws

# Add Jenkins user to docker group for Docker socket access
RUN usermod -aG docker jenkins

# Switch back to Jenkins user
USER jenkins
