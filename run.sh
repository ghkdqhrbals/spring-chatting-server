#!/bin/bash

REPOSITORY_URL=${1}
TAG=${2}

echo "0. ECR Login";
# ECR Login
aws ecr get-login-password --region ap-northeast-2 | docker login --username AWS --password-stdin ${REPOSITORY_URL}

echo "1. Pull and re-tag, re-name with $TAG";
# Get image lists that postfix with $NEW_VERSION from AWS-ECR
images_to_pull=$(aws ecr list-images --repository-name chat --filter "tagStatus=TAGGED" --query "imageIds[?contains(imageTag, '${TAG}')].imageTag" --output text)
echo "images_to_pull list: $images_to_pull"

# Naming and Tagging process
for image in $images_to_pull; do
  docker pull $REPOSITORY_URL:$image

  # Split tag with delimiter "_" and get image name and tag, and re-add "main-service_" prefix
  image_name=main-service_$(echo $image | cut -d'_' -f1)

  # Check exist image with same name and tag
  if docker inspect $image_name:latest > /dev/null 2>&1; then
    # If duplicated name and tag exist, change tag to "old"
    docker tag $image_name:latest $image_name:old
  fi

  # Change name and tag of newly pulled image to $image_name:latest
  docker tag $REPOSITORY_URL:$image $image_name:latest

  # Echo newly tagged image name and tag
  echo "Newly tagged image: $image_name:latest"

  # Removing outdated image
  docker rmi $REPOSITORY_URL:$image
done

echo "2. Remove Dangling Docker Images";

sh remove_dangling_image.sh

echo "3. Run Server and DB Container";

docker compose -f docker-compose-prod.yaml up -d