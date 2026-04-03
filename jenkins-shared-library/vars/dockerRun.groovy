def call(String containerName, String imageName, String port) {
    sh """
    echo "Running container: ${containerName}"

    docker stop ${containerName} || true
    docker rm ${containerName} || true

    docker run -d --name ${containerName} \
    -p ${port}:${port} \
    ${imageName}:latest
    """
}