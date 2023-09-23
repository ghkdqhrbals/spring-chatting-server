#!bin/bash

inputValue="$1"

# deafult value
defaultValue="0.0.1"

# if inputValue is empty, set the default value
if [ -z "$inputValue" ]; then
    inputValue="$defaultValue"
fi

echo "0. Remove old build files older than 30 days";
# find every build directories which are older than 30 days and remove them
find . -type d -name build -exec find {} -type f -mtime +30 -exec rm -f {} \; \;
# remove empty build directories
find . -type d -name build -exec find {} -type d -empty -delete \;

echo "1. Build";

./gradlew build --build-cache --parallel -Pversion=${inputValue} || { echo "Gradle build failed"; exit 1; }

echo "2. Remove Dangling Docker Images";

sh remove_dangling_image.sh

echo "3. Run Server and DB Container";

docker compose -f docker-compose-prod.yaml up -d --build