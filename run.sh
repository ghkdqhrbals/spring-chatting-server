#!/bin/bash

REPOSITORY_URL=${1}
TAG=${2}

# ECR 로그인
$(aws ecr get-login --no-include-email --region ap-northeast-2)

# 해당 ECR 리포지토리의 이미지 목록 가져오기
images_to_pull=$(aws ecr list-images --repository-name chat --filter "tagStatus=TAGGED" --query "imageIds[?ends_with(imageTag, '=${TAG}')].imageTag" --output text)
for image in $images_to_pull; do
  docker pull $ECR_URL/chat:$image
done

echo "2. Remove Dangling Docker Images";

sh remove_dangling_image.sh

echo "3. Run Server and DB Container";

docker compose -f docker-compose-prod.yaml up -d