#!bin/bash

echo "1. Build";

./gradlew build --build-cache --parallel

echo "2. remove_dangling_image";

sh remove_dangling_image.sh

echo "3. Run";

docker compose -f docker-compose-dev.yaml up --build