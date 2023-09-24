#!/bin/bash

REPOSITORY_URL=${1}
TAG=${2}

echo "1. ECR Login";
# ECR 로그인
$(aws ecr get-login --no-include-email --region ap-northeast-2)

# $NEW_VERSION으로 끝나는 모든 이미지 태그를 ECR에서 가져옵니다.
images_to_pull=$(aws ecr list-images --repository-name chat --filter "tagStatus=TAGGED" --query "imageIds[?ends_with(imageTag, '=${TAG}')].imageTag" --output text)

echo "1. ECR image pull and re-tag, re-name";
# 이미지 목록을 반복하며 각 이미지를 가져온 다음 새로운 태그를 설정합니다.
for image in $images_to_pull; do
  docker pull $ECR_URL/chat:$image

  # "_"를 기준으로 태그를 분리합니다.
  image_name=$(echo $image | cut -d'_' -f1)
  new_tag=$(echo $image | cut -d'_' -f2)

  # 새로운 이미지 이름과 태그로 이미지를 다시 태그합니다.
  docker tag $ECR_URL/chat:$image $image_name:$new_tag
done

echo "2. Remove Dangling Docker Images";

sh remove_dangling_image.sh

echo "3. Run Server and DB Container";

docker compose -f docker-compose-prod.yaml up -d