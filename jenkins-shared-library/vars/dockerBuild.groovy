def call(String imageName, String dockerfilePath, String contextDir = ".") {
    sh """
    echo "Building Docker Image: ${imageName}"

    docker build -t ${imageName}:latest \
    -f ${dockerfilePath} ${contextDir}
    """
}