pipeline {
    agent any

    environment {
        SERVICES = "ApiGateway CartService OrderService ProductService UserService"
        DOCKER_REGISTRY = "local"
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
                    def allServices = [
                        "ApiGateway",
                        "CartService",
                        "OrderService",
                        "ProductService",
                        "UserService"
                    ]

                    def changedServices = []

                    // Detect changes using Git
                    for (changeLog in currentBuild.changeSets) {
                        for (entry in changeLog.items) {
                            for (file in entry.affectedFiles) {
                                def filePath = file.path
                                for (svc in allServices) {
                                    if (filePath.startsWith("services/${svc}")) {
                                        changedServices.add(svc)
                                    }
                                }
                            }
                        }
                    }

                    // Remove duplicates
                    changedServices = changedServices.unique()

                    // Fallback: if no changes detected, build all services
                    if (changedServices.isEmpty()) {
                        echo "⚠️ No changes detected → building ALL services"
                        changedServices = allServices
                    }

                    env.CHANGED_SERVICES = changedServices.join(",")
                    echo "Services to build: ${env.CHANGED_SERVICES}"
                }
            }
        }

        stage('Build & Run Docker') {
            steps {
                script {
                    def services = env.CHANGED_SERVICES.split(",")
                    def buildStages = [:]

                    // Create parallel stages for each service
                    for (svc in services) {
                        def serviceName = svc.trim()
                        if (serviceName) {
                            buildStages[serviceName] = {
                                dockerBuildAndRun(serviceName)
                            }
                        }
                    }

                    parallel buildStages
                }
            }
        }
    }
}

// Function to build and run Docker container
def dockerBuildAndRun(serviceName) {
    echo "🔹 Building Docker for ${serviceName}"

    def servicePath = "services/${serviceName}"                  // Docker context
    def dockerfilePath = "${servicePath}/docker/Dockerfile"     // Dockerfile path
    def imageName = "${serviceName.toLowerCase()}:latest"

    sh """
        docker build -t ${DOCKER_REGISTRY}/${imageName} -f ${dockerfilePath} ${servicePath}
        docker rm -f ${serviceName}-test || true
        docker run -d --name ${serviceName}-test ${DOCKER_REGISTRY}/${imageName}
    """

    echo "✅ ${serviceName} container is running as ${serviceName}-test"
}