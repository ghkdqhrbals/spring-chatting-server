#!/bin/bash

ECR_URL="$1"
NEW_VERSION="$2"

# get list of images with spring-chatting-server prefix
images=$(docker images --format "{{.Repository}}" | grep "^spring-chatting-server_" | grep -v "$ECR_URL")

# tagging and push to ECR
for image in $images; do
  IMAGE_NAME=$(echo "$image" | cut -d'_' -f2)_
  ecr_image="$ECR_URL:$IMAGE_NAME$NEW_VERSION"
  docker tag "$image" "$ecr_image"
  docker push "$ecr_image"
done
