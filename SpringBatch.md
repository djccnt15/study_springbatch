# Spring Batch

## 기초 용어 정리

- `Job`: 배치 처리의 가장 큰 단위, 하나의 완전한 배치 처리
- `Step`: `Job`을 구성하는 실행 단위, 하나의 `Job`은 하나 이상의 `Step`으로 구성
- `Tasklet`: `Step`을 통해 실행되는 최소 단위 로직
    - 하나의 `Step`은 하나의 `Tasklet`으로 구성됨
    - 하나의 `Tasklet`에 여러 단계의 서비스 로직을 작성할 수 있으나 지양할 것
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

## jobParameters 표기법

```batch
parameterName=parameterValue,parameterType,identificationFlag
```

- `parameterName`: 배치 `Job`에서 파라미터를 찾을 때 사용할 key 값
- `parameterValue`: 파라미터의 실제 값
- `parameterType`: 파라미터의 타입
    - `java.lang.String`과 같은 FQNC(Fully Qualified Name Class) 사용. 기본값은 `String`
- `identificationFlag`: 해당 파라미터가 `JobInstance` 식별에 사용될 파라미터인지 여부
    - `true`이면 식별에 사용(default == `true`)

## JobScope와 StepScope

### 지연된 빈 생성 (Lazy Bean Creation)

`@JobScope`, `@StepScope`가 적용된 빈은 애플리케이션 구동 시점에는 프록시 객체만 생성 후 실제 인스턴스는 `Job`/`Step`이 실행될 때 생성되어 `Job`/`Step이` 종료하면 소멸됨(생명주기). 지연 생성을 통해 애플리케이션 실행 중에 전달되는 `jobParameters`를 `Job`/`Step` 실행 시점에 생성되는 빈의 실제 인스턴스에 주입 가능  
API 등 외부 호출로 실행하는 애플리케이션의 경우 동적 파라미터를 `Step` 빈 인스턴스 생성 시에 주입 가능함. 동시에 여러 요청이 같은 `Job` 정의를 서로 다른 파라미터로 실행하는 경우 `Tasklet` 인스턴스의 동시성 이슈 발생 가능성이 있는데, 프록시 객체와 실제 `Tasklet` 인스턴스를 구분해 `jobExecution`마다 다른 `Tasklet` 인스턴스를 생성해 해결 가능

`@StepScope`는 `Step`의 실행 범위에서 빈을 관리함. 동시에 여러 `Step`이 실행되면서 빈 객체를 호출해도 각 `Step` 실행마다 독립적인 `Tasklet` 인스턴스 생성하고, 이를 통해 `Tasklet` 객체 재활용 시 발생할 수 있는 동시성 이슈 방지 가능

### 사용 시 주의사항

1. CGLIB를 사용해 클래스 기반의 프록시를 생성하기 때문에 프록시를 생성하려면 대상 클래스가 상속 가능해야 함
1. `Step` 빈에는 `@StepScope` 사용 금지(에러 발생), `@JobScope` 미사용 권장
    - `Step` 빈 생성과 스코프 활성화 시점이 불일치해 오류 발생
    - `Tasklet`에 `@JobScope`/`@StepScope`를 선언해 파라미터를 받아야 함
