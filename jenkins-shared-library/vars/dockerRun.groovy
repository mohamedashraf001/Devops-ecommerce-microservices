def call(String containerName, String imageName, String port) {
    sh """
    echo "Stopping old container if exists..."

    docker stop ${containerName} || true
    docker rm ${containerName} || true

    echo "Running new container..."

    docker run -d \
    --name ${containerName} \
    -p ${port}:${port} \
    ${imageName}:latest
    """
}