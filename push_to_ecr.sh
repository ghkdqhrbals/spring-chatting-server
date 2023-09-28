#!/bin/bash

ECR_URL="$1"
NEW_VERSION="$2"
PREFIX="spring-chatting-server_"


echo "ECR_URL: $ECR_URL"
echo "NEW_VERSION: $NEW_VERSION"
echo "Push to AWS-ECR start"
echo "Get docker images with spring-chatting-server prefix"

# get list of images with spring-chatting-server prefix
images=$(docker images --format "{{.Repository}}" | grep "^${PREFIX}")

echo "${images}"

# tagging and push to ECR
for image in $images; do
  # Use only image name without prefix
  IMAGE_NAME=$(echo "$image" | cut -d'_' -f2)_
  echo "tagging ${image} to ${IMAGE_NAME}"

  ecr_image="$ECR_URL:$IMAGE_NAME$NEW_VERSION"
  docker tag "$image" "$ecr_image"
  echo "push image to ECR with ${ecr_image}"
  docker push "$ecr_image"
done
