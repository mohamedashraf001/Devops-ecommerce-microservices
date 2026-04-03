pipeline {
    agent any

    environment {
        DOCKERHUB_CRED = 'dockerhub-cred' // معرف الـ credentials في Jenkins
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

        stage('Build & Push Docker Images') {
            steps {
                script {
                    def services = env.CHANGED_SERVICES.split(',')
                    def parallelBuilds = [:]

                    for (svc in services) {
                        def serviceName = svc
                        parallelBuilds[serviceName] = {
                            dir("services/${serviceName}") {
                                docker.withRegistry('https://index.docker.io/v1/', DOCKERHUB_CRED) {
                                    // Convert to lowercase
                                    def imageName = "mohamedashraf001/${serviceName.toLowerCase()}:latest"
                                    def image = docker.build(imageName)
                                    image.push()
                                }
                            }
                        }
                    }

                    parallel parallelBuilds
                }
            }
        }

        stage('Notify Success') {
            steps {
                echo "✅ Docker images built and pushed for: ${env.CHANGED_SERVICES}"
            }
        }
    }

    post {
        failure {
            echo "❌ Pipeline failed!"
        }
        success {
            echo "🚀 Pipeline succeeded!"
        }
    }
}