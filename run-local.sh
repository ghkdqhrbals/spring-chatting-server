#!bin/bash

logPrint="$1"

echo "1. Build";

./gradlew build --build-cache --parallel || { echo "Gradle build failed"; exit 1; }

echo "2. remove_dangling_image";

sh remove_dangling_image.sh

echo "3. Run";


if [ -z "$inputValue" ]; then
  docker compose -f docker-compose-prod.yaml up --build -d
else
  docker compose -f docker-compose-prod.yaml up --build
fi

