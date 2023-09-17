#!bin/bash

inputValue="$1"

# 기본값 설정
defaultValue="0.0.1"

# if inputValue is empty, set the default value
if [ -z "$inputValue" ]; then
    inputValue="$defaultValue"
fi

echo "1. Build";

./gradlew build --build-cache --parallel -Pversion=${inputValue} || { echo "Gradle build failed"; exit 1; }

echo "2. remove_dangling_image";

sh remove_dangling_image.sh

echo "3. Run";

docker compose -f docker-compose-only-chat.yaml up --build -d