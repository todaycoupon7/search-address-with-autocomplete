# **다음(Kakao) 로컬API를 이용한 주소 검색 (feat. 자동완성)**

다음(kakao) [로컬 API](https://developers.kakao.com/docs/restapi/local)를 이용하여 주소 검색 및 검색 키워드 입력시 자동완성 키워드 목록을 표시합니다.


로컬 API 중에 [주소검색](https://developers.kakao.com/docs/restapi/local#%EC%A3%BC%EC%86%8C-%EA%B2%80%EC%83%89) 부분을 참고하시면 됩니다.

자동완성의 경우, 해당 기능을 지원해주는 서비스가 없어 [우편번호 서비스](http://postcode.map.daum.net/guide)에서 자동완성 기능의 요청 URL을 이용하여 구현하였습니다. 자세한 내용은 소스 내용을 참고하세요.


소스코드를 테스트시 미리 [KakaoDevelopers_](https://developers.kakao.com/apps)에 어플리케이션 등록 및 앱키 발급을 받으셔서 strings.xml을 수정하셔서 사용하시면 됩니다.


자세한 내용은 [https://todaycoupon7.github.io/2018/02/01/address_search.html] 링크 참조


![image_of_address_search](https://todaycoupon7.github.io/assets/img/pexels/address-search.png)
