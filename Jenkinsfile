pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                git 'https://github.com/mohamedashraf001/Devops-ecommerce-microservices.git'
            }
        }

        stage('Detect Changes') {
            steps {
                script {
                    // مثال: detect التغييرات في كل خدمة
                    def changed = sh(script: "git diff --name-only HEAD~1 HEAD | grep 'services/' | cut -d '/' -f2 | sort | uniq", returnStdout: true).trim()
                    echo "Changed services: ${changed}"
                    env.CHANGED_SERVICES = changed
                }
            }
        }

        stage('Build & Run Docker') {
            steps {
                script {
                    if (env.CHANGED_SERVICES) {
                        def services = env.CHANGED_SERVICES.split("\n")
                        for (s in services) {
                            echo "Building and running Docker for ${s}"
                            sh """
                                docker build -f services/${s}/Dockerfile.dev -t ${s}:dev ./services/${s}
                                docker run -d -p 8${s.hashCode().abs() % 1000}:8082 --name ${s} ${s}:dev
                            """
                        }
                    } else {
                        echo "No services changed"
                    }
                }
            }
        }
    }
}