#!/bin/bash

ECR_URL="$1"
NEW_VERSION="$2"

# 현재 빌드된 Docker 이미지의 목록을 가져옵니다.
images=$(docker images --format "{{.Repository}}" | grep "^spring-chatting-server_" | grep -v "$ECR_URL")

# 각 이미지를 ECR에 태깅 및 푸시합니다.
for image in $images; do
  ecr_image="$ECR_URL/$image:$NEW_VERSION"

  docker tag "$image" "$ecr_image"
  docker push "$ecr_image"
done
