pipeline {
    agent {
        docker {
            image 'golang:1.22'
            args '-v /var/run/docker.sock:/var/run/docker.sock'
        }
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

        stage('Build Changed Services') {
            steps {
                script {
                    def services = env.CHANGED_SERVICES.split(",")

                    for (svc in services) {
                        if (svc?.trim()) {
                            buildService(svc)
                        }
                    }
                }
            }
        }
    }
}

def buildService(serviceName) {
    echo "Building ${serviceName}"

    sh """
        cd services/${serviceName}
        go mod tidy
        go build -o app
    """
}