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

## Listener

### 주요 활용 예시

- 단계별 모니터링과 추적: 각 `Job`/`Step`의 실행 전후 로깅
- 실행 결과에 따른 후속 처리: `Job`과 `Step`의 실행 상태를 리스너에서 직접 확인하고 그에 따른 후속 조치
    - `JobExecutionListener`의 `afterJob()` 메서드에서 `Job`의 종료 상태를 확인하고 종료 상태에 따른 후속 조치 가능
- 데이터 가공과 전달: 실제 처리 로직 전후에 데이터를 추가로 정제하거나 변환
    - `StepExecutionListener`, `ChunkListener`를 사용해 `ExecutionContext`의 데이터를 수정하거나 필요한 정보를 추가
    - `Step` 간의 데이터 전달, 다음 처리에 필요한 정보 사전 준비 가능
- 부가 기능 분리: 주요 처리 로직과 부가 로직을 깔끔하게 분리 가능
    - `ChunkListener`에서 오류가 발생한 경우 `afterChunkError()` 메서드에서 관리자에게 알림 메일 등

### `Listener`의 종류

- `JobExecutionListener`: 전체 `Job`의 시작과 종료 감지
- `StepExecutionListener`: 각 `Step` 단계의 실행 감지
- `ChunkListener`: 시스템을 청크 단위로 처리 시, 반복의 시작과 종료 시점 감지
- `ItemReadListener`/`ItemProcessListener`/`ItemWriteListener`: 개별 아이템 식별 감지

### 주의 사항

- 예외 처리: Spring Batch는 `JobExecutionListener`의 `beforeJob()` 또는 `StepExecutionListener`의 `beforeStep()`에서 예외가 발생 시 `Job`/`Step`이 실패한 것으로 판단함
    - `Job`/`Step`을 중단시켜야 할 만큼 치명적인 에러가 아닌 경우는 `try`/`catch`로 직접 예외를 잡아서 무시하고 진행 필요
- 단일 책임 원칙 준수: `Listener`는 감시 및 통제만을 담당하고, 비즈니스 로직과 분리
- 성능 최적화
    - `JobExecutionListener`/`StepExecutionListener`: `Job`/`Step` 실행당 한 번씩만 실행되므로 비교적 가벼움
    - `ItemReadListener`/`ItemProcessListener`: 아이템마다 실행되어 비교적 무거음
    - 리소스 사용 최소화: 데이터베이스 연결, 파일 I/O, 외부 API 호출 등 `Listener` 내 로직 최소화. `Item` 단위 `Listener`에서 특히 중요

## Job ExecutionContext

Spring Batch는 배치 작업의 재현 가능성(Repeatability)과 일관성(Consistency)을 보장하는 것을 기본 철학으로 하기 때문에, `JobParameters`는 불변(immutable)하게 설계됨

- 재현 가능성: 동일한 `JobParameters`로 실행한 `Job`은 항상 동일한 결과를 생성해야 함
    - 실행 중간에 `JobParameters`가 변경되면 이를 보장할 수 없음
- 추적 가능성: 배치 작업의 실행 기록(`JobInstance`, `JobExecution`)과 `JobParameters`는 메타데이터 저장소에 저장됨
    - `JobParameters`가 변경 가능하다면 기록과 실제 작업의 불일치가 발생할 수 있음

`ExecutionContext`를 활용하면 커스텀 컬렉션의 마지막 처리 인덱스나 집계 중간 결과물 같은 데이터를 저장할 수 있고, Batch 재시작 시 `ExecutionContext`의 데이터를 자동으로 복원하므로, 중단된 지점부터 처리를 이어갈 수 있음

> 외부에서 값을 받는 것이 훨씬 더 안전하고 유연하기 때문에 가능한 `JobParameters` 사용 권장  
> JobExecutionListener`/`ExecutionContext`는 외부에서 값을 받을 수 없는 경우에만 사용

`Step` 간의 데이터 공유가 필요한 경우 `ExecutionContextPromotionListener`를 사용하면 boilerplate를 줄일 수 있음

> 다만 `Step`은 가능한 한 독립적으로 설계하여 재사용성과 유지보수성을 높이는 것이 좋음  
> 불가피한 경우가 아니라면 `Step` 간 데이터 의존성은 최소화  
