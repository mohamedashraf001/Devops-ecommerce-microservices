def call(Map config = [:]) {
    pipeline {
        agent any
        
        environment {
            // اسم الخدمة واسم الصورة
            SERVICE_NAME = "${config.serviceName}"
            IMAGE_TAG = "mohamedashraf001/${config.serviceName}:latest"
            DOCKER_FILE = "services/${config.serviceName}/Dockerfile"
        }

        stages {
            stage('Clean Up') {
                steps {
                    echo "Cleaning old containers for ${SERVICE_NAME}..."
                    // حذف أي كونتينر قديم بنفس الاسم لتجنب التعارض
                    sh "docker rm -f ${SERVICE_NAME} || true"
                }
            }

            stage('Build Docker Image') {
                steps {
                    echo "Building Image: ${IMAGE_TAG}..."
                    // بناء الصورة باستخدام الـ Dockerfile الخاص بالخدمة
                    sh "docker build -t ${IMAGE_TAG} -f ${DOCKER_FILE} ."
                }
            }

            stage('Run Container') {
                steps {
                    echo "Starting Container for ${SERVICE_NAME}..."
                    // تشغيل الكونتينر (بسيط حالياً بدون Docker Compose لكل خدمة)
                    sh "docker run -d --name ${SERVICE_NAME} ${IMAGE_TAG}"
                }
            }
            
            stage('Health Check') {
                steps {
                    script {
                        echo "Checking if ${SERVICE_NAME} is running..."
                        sh "docker ps | grep ${SERVICE_NAME}"
                    }
                }
            }
        }
    }
}