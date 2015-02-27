Spring Batch Sample
===================
>**spring-batch-db는 spring guides(https://spring.io/guides/gs/batch-processing/) 소스를 100% 참고했습니다.**

>본 소스는 Creating a Batch Service에서는 메모리db로 배치 결과가 저장되는 것을 mariadb로 결과를 저장하는 것으로만 변경했습니다.

---------------------------
**spring-batch-db에 적용된 기술은 다음과 같습니다.**

사용된 오픈소스|버전
------------|---
java|1.7
spring|4.1.4
gradle|2.2.1

----------------------------
**기능설명**

spring-batch-db에서는 book.csv파일을 읽어들입니다. book.csv파일은 [서적명, 작가, 가격] 등이 순서대로 100건 등록되어 있습니다.
그래서 이 파일을 읽어들여서 가격을 일괄적으로 10% 올린 데이터로 book테이블에 인서트하는 배치프로그램입니다. 


설치방법은 eclipse에서 Import하면 됩니다. (eclipse에 gradle 플러그인이 설치되어 있어야 합니다)
>**import > Gradle > Gradle Project**

생성테이블은 아래의 경로에 존재합니다.
>**src/main/resources/Book.sql** 