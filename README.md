# 로그인 유지 구현

* 토큰관리는 인증서버! RefreshToken => Redis

* 클라이언트 브라우저의 쿠키에 HttpOnly JWT 토큰 삽입
* 프론트 서버의 로그인필터는 쿠키의 `jwttoken` 을 가져옵니다
* `jwttoken`이 없다면 /login 리다이렉트. 있다면, 실제 validation을 하지 않고 백엔드 api-gateway에 전송합니다

* Spring-cloud-gateway 는 브랜치 분기 이전에는 yaml 파일을 통해 간단히 프록시패스를 하였지만, 직접 구현으로 변경할 것입니다
> JWT 페이로드 내 저장된 id 를 읽고 `.../user/userId=?` 의 `?` 에 삽입해줘야하는데 yaml 를 통해 설정한다면 불가능하기 떄문이죠