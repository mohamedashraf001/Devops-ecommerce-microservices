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
                    // Array services
                    def allServices = [
                        "ApiGateway",
                        "CartService",
                        "OrderService",
                        "ProductService",
                        "UserService"
                    ]

                    def changedServices = []

                    // اكتشاف التغييرات من Git
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

                    // إزالة التكرار
                    changedServices = changedServices.unique()

                    // لو مفيش تغييرات، نبني كل الخدمات
                    if (changedServices.isEmpty()) {
                        echo "⚠️ No changes detected → building ALL services (fallback)"
                        changedServices = allServices
                    }

                    env.CHANGED_SERVICES = changedServices.join(",")
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

def dockerBuildAndRun(serviceName) {
    echo "Building Docker for ${serviceName}"

    def servicePath = "services/${serviceName}" // Docker context = service folder
    def dockerfilePath = "${servicePath}/docker/Dockerfile" // path to Dockerfile
    def imageName = "${serviceName.toLowerCase()}:latest"

    sh """
        docker build -t ${DOCKER_REGISTRY}/${imageName} -f ${dockerfilePath} ${servicePath}
        docker rm -f ${serviceName}-test || true
        docker run -d --name ${serviceName}-test ${DOCKER_REGISTRY}/${imageName}
    """
    
    echo "Service ${serviceName} is running in container ${serviceName}-test"
}