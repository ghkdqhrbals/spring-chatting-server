#!/bin/bash

REPOSITORY_URL=${1}
TAG=${2}
PREFIX="spring-chatting_"

# ECR 로그인
$(aws ecr get-login --no-include-email --region ap-northeast-2)

# 해당 ECR 리포지토리의 이미지 목록 가져오기
IMAGES=$(aws ecr list-images --repository-name chat --query 'imageIds[?starts_with(imageTag, `'$PREFIX'`)].imageTag' --output text)

# 각 이미지에 대해 pull 명령어 실행
for IMAGE in $IMAGES; do
    docker pull ${REPOSITORY_URL}/${IMAGE}:${TAG}
done

echo "2. Remove Dangling Docker Images";

sh remove_dangling_image.sh

echo "3. Run Server and DB Container";

docker compose -f docker-compose-prod.yaml up -d