#!/bin/bash

REPOSITORY_URL=${1}
TAG=${2}

echo "0. ECR Login";
# ECR 로그인
aws ecr get-login-password --region ap-northeast-2 | docker login --username AWS --password-stdin ${REPOSITORY_URL}

echo "1. Pull and re-tag, re-name with $TAG";
# $NEW_VERSION으로 끝나는 모든 이미지 태그를 ECR에서 가져옵니다.
images_to_pull=$(aws ecr list-images --repository-name chat --filter "tagStatus=TAGGED" --query "imageIds[?contains(imageTag, '${TAG}')].imageTag" --output text)
echo "images_to_pull list: $images_to_pull"

# 이미지 목록을 반복하며 각 이미지를 가져온 다음 새로운 태그를 설정합니다.
for image in $images_to_pull; do
  docker pull $REPOSITORY_URL:$image

  # "_"를 기준으로 태그를 분리합니다.
  image_name=$(echo $image | cut -d'_' -f1)
  new_tag=$(echo $image | cut -d'_' -f2)

  # 존재하는 태그가 있는지 확인합니다.
  if docker inspect $image_name:latest > /dev/null 2>&1; then
    # 중복된 태그가 있으면 "old" 로 태그합니다. 여러 버전을 저장할 순 있지만, 용량문제로 현재는 최신버전과 그 이전버전만 저장합니다.
    docker tag $image_name:latest $image_name:old
  fi

  # 새로운 이미지 이름과 태그로 이미지를 다시 태그합니다.
  docker tag $REPOSITORY_URL:$image $image_name:latest

  # 원본 태그의 이미지를 삭제합니다.
  docker rmi $REPOSITORY_URL:$image
done

echo "2. Remove Dangling Docker Images";

sh remove_dangling_image.sh

echo "3. Run Server and DB Container";

docker compose -f docker-compose-prod.yaml up -d