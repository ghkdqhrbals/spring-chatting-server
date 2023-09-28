#!bin/bash

echo "Dangling Image Removal Process Start";

echo "1. Dangling docker image check";
dockerDanglingArray=$(docker image list --filter "dangling=true" -q)

echo "${dockerDanglingArray}"

echo "2. Remove empty image string"

# Temporary Image List
NEW=()

# If empty string has been found, skip that index
for i in ${dockerDanglingArray}; do
   # Skip null items
   if [ -z "$i" ]; then
     continue
   fi
   # Add the rest of the elements to an array
   NEW+=("${i}")
done

# 빈 공백이 제거된 이미지 리스트로 초기화
dockerDanglingArray=${NEW[@]}

# Dangling image length
len=${#dockerDanglingArray[@]}

if [ $len -eq 0 ]; then
  echo "No Dangling Images";
else
  echo "3. Remove dangling docker image";
  docker rmi ${dockerDanglingArray[@]} # repository=<none> image removal start
fi

