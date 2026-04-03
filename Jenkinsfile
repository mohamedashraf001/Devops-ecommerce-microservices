pipeline {
    agent {
        docker {
            image 'golang:1.25.3'
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
                def changedServices = []

                for (changeLog in currentBuild.changeSets) {
                    for (entry in changeLog.items) {
                        for (file in entry.affectedFiles) {
                            def filePath = file.path

                            if (filePath.startsWith("services/ApiGateway")) {
                                changedServices.add("ApiGateway")
                            }
                            if (filePath.startsWith("services/CartService")) {
                                changedServices.add("CartService")
                            }
                            if (filePath.startsWith("services/OrderService")) {
                                changedServices.add("OrderService")
                            }
                            if (filePath.startsWith("services/ProductService")) {
                                changedServices.add("ProductService")
                            }
                            if (filePath.startsWith("services/UserService")) {
                                changedServices.add("UserService")
                            }
                        }
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