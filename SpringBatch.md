# Spring Batch

## 용어 정리

- `Job`: 배치 처리의 가장 큰 단위, 하나의 완전한 배치 처리
- `Step`: `Job`을 구성하는 실행 단위, 하나의 `Job`은 하나 이상의 `Step`으로 구성
- `JobLauncher`: `Job`을 실행하고 실행에 필요한 파라미터를 전달하는 객체. 배치 작업 실행의 시작점
- `JobRepository`: 배치 처리의 모든 메타데이터를 저장하고 관리하는 핵심 저장소
    - `Job`과 `Step`의 실행 정보(시작/종료 시간, 상태, 결과 등)를 기록
    - 저장된 정보들은 배치 작업의 모니터링이나 문제 발생 시 재실행에 활용
- `ExecutionContext`: `Job`과 `Step` 실행 중의 상태 정보를 `key-value` 형태로 저장하는 객체
    - `Job`과 `Step` 간의 데이터 공유나 `Job` 재시작 시 상태 복원에 사용
- 데이터 처리 컴포넌트 구현체: 다양한 형태의 데이터를 `읽기-처리-쓰기` 방식으로 처리하는 실제 구현체
    - `ItemReader`, `ItemProcessor`, `ItemWriter`

## Program Arguments

### Job Name

- Job 이름으로 원하는 작업만 실행하는 방법

```batch
--spring.batch.job.name=basicBatchJob
```

> `Job` 타입 빈이 한 개일 때는 생략 가능
