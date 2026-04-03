pipeline {
    agent any

    environment {
        SERVICES = "ApiGateway CartService OrderService ProductService UserService"
        DOCKER_REGISTRY = "local" // ممكن تغيره بعدين لـ DockerHub أو registry داخلي
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Detect Changes') {
            steps {
                script {
                    def changedFiles = sh(
                        script: "git diff --name-only HEAD~1 HEAD",
                        returnStdout: true
                    ).trim().split("\n")

                    def changedServices = []

                    for (file in changedFiles) {
                        if (file.startsWith("services/ApiGateway")) {
                            changedServices.add("ApiGateway")
                        }
                        if (file.startsWith("services/CartService")) {
                            changedServices.add("CartService")
                        }
                        if (file.startsWith("services/OrderService")) {
                            changedServices.add("OrderService")
                        }
                        if (file.startsWith("services/ProductService")) {
                            changedServices.add("ProductService")
                        }
                        if (file.startsWith("services/UserService")) {
                            changedServices.add("UserService")
                        }
                    }

                    env.CHANGED_SERVICES = changedServices.unique().join(",")
                    echo "Changed services: ${env.CHANGED_SERVICES}"
                }
            }
        }

        stage('Build & Run Docker for Changed Services') {
            steps {
                script {
                    def services = env.CHANGED_SERVICES.split(",")

                    for (svc in services) {
                        if (svc?.trim()) {
                            dockerBuildAndRun(svc)
                        }
                    }
                }
            }
        }
    }
}

// Function to build and run Docker container
def dockerBuildAndRun(serviceName) {
    echo "Building Docker for ${serviceName}"

    def servicePath = "services/${serviceName}/docker"
    def imageName = "${serviceName.toLowerCase()}:latest"

    sh """
        cd ${servicePath}
        docker build -t ${DOCKER_REGISTRY}/${imageName} .
        docker rm -f ${serviceName}-test || true
        docker run -d --name ${serviceName}-test ${DOCKER_REGISTRY}/${imageName}
    """
    
    echo "Service ${serviceName} is running in container ${serviceName}-test"
}