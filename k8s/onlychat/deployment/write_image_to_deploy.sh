#!/bin/bash

# 입력 파라미터 체크
if [ "$#" -ne 3 ]; then
    echo "Usage: $0 REPOSITORY_NAME REGION VERSION"
    exit 1
fi

REPOSITORY_NAME=$1
REGION=$2
VERSION=$3

# ECR에서 모든 이미지 태그 가져오기
ALL_TAGS=$(aws ecr list-images --repository-name chat --filter "tagStatus=TAGGED" --query "imageIds[?contains(imageTag, '${VERSION}')].imageTag" --output text)
echo "Get all tags from ECR : $ALL_TAGS"
for file in *-deployment.yaml; do
  # 현재 서비스 이름 추출
  SERVICE_NAME=$(grep "image: main-service_" "$file" | awk -F':' '{print $2}' | awk -F'_' '{print $2}' | awk '{print $1}')

  # SERVICE_NAME이 없다면 다음 파일로 넘어간다
  # 있다면, SERVICE_NAME 이 "chatting-server" 로 됩니다
  echo "Check service name : $SERVICE_NAME"
  if [ -z "$SERVICE_NAME" ]; then
    continue
  fi

  # ALL_TAGS에서 해당 서비스 이름에 맞고, VERSION으로 끝나는 최신 이미지 태그 찾기
  DESIRED_TAG=$(echo "$ALL_TAGS" | tr '\t' '\n' | grep "${SERVICE_NAME}_${VERSION}")
  echo "Match file found with tag : $DESIRED_TAG"

  # 만약 DESIRED_TAG가 비어있다면 (즉, 해당 버전의 태그를 찾지 못했다면) 다음 파일로 넘어간다
  if [ -z "$DESIRED_TAG" ]; then
    continue
  fi

  # deploy.yaml 파일 내의 이미지 태그 업데이트
  sed -i '' "s#image: main-service_$SERVICE_NAME.*#image: $REPOSITORY_NAME:$DESIRED_TAG#g" "$file"
  echo "Updated $file"
done