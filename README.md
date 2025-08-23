# MinigamePlugin

마인크래프트 서버용 미니게임 플러그인으로, 3라운드로 구성된 팀 기반 게임을 제공합니다.

## 📋 목차

- [서버 환경 및 요구사항](#서버-환경-및-요구사항)
- [게임 구조](#게임-구조)
- [설정 파일](#설정-파일)
- [코드 구조](#코드-구조)
- [설치 및 사용법](#설치-및-사용법)
- [주요 기능](#주요-기능)
- [문제 해결](#문제-해결)
- [라이센스](#라이센스)
- [기여하기](#기여하기)
- [문의](#문의)

## 🖥️ 서버 환경 및 요구사항

### 시스템 요구사항
- **Java**: Java 21 이상
- **마인크래프트 서버**: Paper 1.21.1 이상
- **메모리**: 최소 2GB RAM 권장
- **플레이어**: 최대 50명 동시 접속 지원

### Dependencies
```xml
<dependency>
    <groupId>io.papermc.paper</groupId>
    <artifactId>paper-api</artifactId>
    <version>1.21.1-R0.1-SNAPSHOT</version>
    <scope>provided</scope>
</dependency>
```

### 권한 설정
- **관리자 권한**: `minigame.admin` - 모든 명령어 사용 가능
- **플레이어 권한**: 기본적으로 제한됨, 팀 설정 시에만 참여 가능

## 🎮 게임 구조

### 라운드 흐름

#### 1라운드: 그림맞추기
- **목표**: 그림팀이 그림을 그리고, 방해팀이 방해하는 팀워크 게임
- **시간**: 제한 없음
- **특징**: 
  - 그림팀: 그림판에 그림을 그려 정답을 맞춤
  - 방해팀: 실명권, 초기화권, 깽판권 등으로 방해

#### 2라운드: 고깔축구
- **목표**: 호박을 쓴 상태에서 축구공을 골대에 넣는 게임
- **시간**: 15분
- **특징**:
  - 모든 플레이어가 호박을 착용
  - 2분마다 10초간 호박 해제 후 재착용
  - 팀별 점수 시스템

#### 3라운드: 끈끈한 점프맵
- **목표**: 점프맵을 통과하는 게임
- **상태**: 개발 중 (기본 구조만 구현)

### 주요 명령어

| 명령어 | 설명 | 권한 |
|--------|------|------|
| `/팀` | 팀 설정 모드 진입 | `minigame.admin` |
| `/그림` | A팀 또는 B팀을 그림팀으로 지정 | `minigame.admin` |
| `/방해` | A팀 또는 B팀을 방해팀으로 지정 | `minigame.admin` |
| `/골키퍼` | 골키퍼 포지션 설정 | `minigame.admin` |
| `/수비수` | 수비수 포지션 설정 | `minigame.admin` |
| `/공격수` | 공격수 포지션 설정 | `minigame.admin` |
| `/축구공` | 축구공 소환/해제 | `minigame.admin` |
| `/라운드2시작` | 2라운드 호박 로직 시작 | `minigame.admin` |
| `/라운드2종료` | 2라운드 호박 로직 종료 | `minigame.admin` |
| `/spawn` | 모든 플레이어 스폰으로 이동 | `minigame.admin` |
| `/아이템` | 공격팀 방해 아이템 지급 | `minigame.admin` |

### 시스템 구성

#### 팀 시스템
- **A팀/B팀**: 두 개의 팀으로 구성
- **그림팀/방해팀**: 역할 기반 팀 분류
- **포지션 시스템**: 골키퍼, 수비수, 공격수 역할 분담

#### 아이템 시스템
- **실명권**: 그림팀을 실명 상태로 만듦
- **초기화권**: 그림판을 초기화
- **깽판권**: 그림판에 무작위 블록 배치
- **점프부스트**: 점프력 100배 증가
- **좀비소환권**: 좀비 10마리 소환
- **감옥권**: 랜덤 감옥에 투입

#### 축구공 시스템
- **물리 엔진**: 실제 축구공과 유사한 물리 효과
- **충돌 감지**: 벽과의 충돌 시 반사
- **마찰 효과**: 자연스러운 감속

## ⚙️ 설정 파일

### config.yml 주요 설정

#### 그림판 좌표
```yaml
canvas:
  corner1:
    world: world
    x: 45
    y: -54
    z: -107
  corner2:
    world: world
    x: 61
    y: -58
    z: -99
```

#### 좀비 소환 위치
```yaml
zombie_spawn:
  world: world
  x: 53
  y: -58
  z: -103
```

#### 감옥 시스템
```yaml
jail:
  world: world
  x: 53
  y: -59
  z: -58
release:
  world: world
  x: 53
  y: -58
  z: -92
```

#### 무제한 상자
```yaml
unlimited-chests:
  - world: world
    x: 41
    y: -59
    z: -103
    slots:
      0:
        material: WHITE_CONCRETE
        amount: 64
      # ... 추가 블록들
```

## 🏗️ 코드 구조

### 패키지 구조
```
src/main/java/yd/kingdom/main/
├── Main.java                    # 메인 플러그인 클래스
├── commands/                    # 명령어 처리 클래스들
│   ├── AttackCommand.java      # 방해팀 설정
│   ├── AttackerCommand.java    # 공격수 포지션
│   ├── DefendCommand.java      # 그림팀 설정
│   ├── DefenderCommand.java    # 수비수 포지션
│   ├── GoalkeeperCommand.java  # 골키퍼 포지션
│   ├── ItemCommand.java        # 아이템 지급
│   ├── RoundTwoEndCommand.java # 2라운드 종료
│   ├── RoundTwoStartCommand.java # 2라운드 시작
│   ├── SoccerBallCommand.java  # 축구공 제어
│   ├── SpawnCommand.java       # 스폰 이동
│   └── TeamSetupCommand.java   # 팀 설정
├── game/                        # 게임 로직 클래스들
│   ├── GameManager.java        # 게임 전체 관리
│   ├── ItemManager.java        # 아이템 관리
│   ├── PositionManager.java    # 포지션 관리
│   ├── RopeManager.java        # 로프 시스템
│   ├── SoccerBallManager.java  # 축구공 물리
│   ├── TeamColorManager.java   # 팀 색상 관리
│   ├── TeamManager.java        # 팀 관리
│   └── UnlimitedChestManager.java # 무제한 상자
├── listener/                    # 이벤트 리스너들
│   ├── BallKickListener.java   # 공 차기 이벤트
│   ├── BlockBreakDropListener.java # 블록 파괴 이벤트
│   ├── HungerListener.java     # 배고픔 이벤트
│   ├── ItemListener.java       # 아이템 사용 이벤트
│   ├── PositionListener.java   # 포지션 이벤트
│   ├── TeamChatListener.java   # 팀 채팅 이벤트
│   └── TeamDefendListener.java # 팀 수비 이벤트
├── manager/                     # 라운드별 관리자
│   ├── round1/
│   │   └── RoundOneManager.java # 1라운드 관리
│   ├── round2/
│   │   └── RoundTwoManager.java # 2라운드 관리
│   └── round3/
│       └── RoundThreeManager.java # 3라운드 관리
├── position/                    # 포지션 관련
│   └── PositionType.java       # 포지션 타입 정의
└── util/                        # 유틸리티 클래스들
    ├── LocationUtil.java       # 위치 관련 유틸
    └── MessageUtil.java        # 메시지 관련 유틸
```

### 주요 클래스 설명

#### Main.java
- 플러그인의 진입점
- 모든 매니저 클래스 초기화
- 명령어 및 이벤트 리스너 등록

#### GameManager.java
- 전체 게임 진행 관리
- 라운드별 전환 처리
- 게임 상태 관리

#### TeamManager.java
- 팀 구성 및 관리
- 팀별 플레이어 할당
- 팀 설정 모드 처리

#### RoundTwoManager.java
- 2라운드 고깔축구 로직
- 호박 착용/해제 사이클
- 보스바 카운트다운
- 팀별 점수 시스템

#### SoccerBallManager.java
- 축구공 물리 엔진
- 충돌 감지 및 반사
- 마찰 효과 적용

## 📥 설치 및 사용법

### 1. 플러그인 설치
1. **서버 준비**: Paper 1.21.1 이상 서버 준비
2. **Java 설치**: Java 21 이상 설치
3. **플러그인 다운로드**: `MinigamePlugin.jar` 파일을 `plugins` 폴더에 복사
4. **서버 재시작**: 서버를 재시작하여 플러그인 활성화

### 2. 초기 설정
1. **설정 파일 확인**: `plugins/MinigamePlugin/config.yml` 파일 확인
2. **좌표 설정**: 서버 월드에 맞게 좌표 수정
3. **권한 설정**: `plugins/PermissionsEx/config.yml`에서 권한 설정

### 3. 게임 시작
1. **팀 설정**: `/팀` 명령어로 팀 구성
2. **역할 지정**: `/그림`, `/방해` 명령어로 팀 역할 설정
3. **포지션 설정**: `/골키퍼`, `/수비수`, `/공격수`로 포지션 할당
4. **게임 시작**: 자동으로 1라운드 시작

### 4. 라운드 진행
- **1라운드**: 그림맞추기 자동 시작
- **2라운드**: `/라운드2시작` 명령어로 수동 시작
- **3라운드**: 자동 시작 (개발 중)

## 🚀 주요 기능

### 팀 관리 시스템
- **동적 팀 구성**: 게임 중에도 팀 변경 가능
- **팀별 채팅**: 팀 내부 통신 시스템
- **팀 색상**: 팀별 구분을 위한 색상 시스템

### 아이템 시스템
- **방해 아이템**: 그림팀을 방해하는 다양한 아이템
- **효과 아이템**: 점프부스트, 좀비소환 등 특수 효과
- **제한 아이템**: 게임 밸런스를 위한 제한된 아이템

### 포지션 시스템
- **골키퍼**: 골대 수비 담당
- **수비수**: 중간 지역 수비
- **공격수**: 공격 및 득점 담당

### 감옥 시스템
- **자동 감옥**: 특정 조건 시 자동 감옥
- **감옥 해제**: 관리자 명령어로 해제
- **감옥 위치**: 설정 파일로 위치 조정 가능

### 무제한 상자
- **자동 보충**: 아이템 사용 시 자동으로 보충
- **다양한 블록**: 콘크리트, 테라코타 등 다양한 블록 제공
- **설정 가능**: `config.yml`에서 블록 종류 및 수량 조정

## 🔧 문제 해결

### 일반적인 문제들

#### 플러그인이 로드되지 않는 경우
1. **Java 버전 확인**: Java 21 이상 설치 확인
2. **서버 버전 확인**: Paper 1.21.1 이상 사용 확인
3. **로그 확인**: `logs/latest.log`에서 오류 메시지 확인

#### 명령어가 작동하지 않는 경우
1. **권한 확인**: `minigame.admin` 권한 보유 확인
2. **플러그인 활성화**: `/plugins` 명령어로 플러그인 상태 확인
3. **명령어 등록**: 서버 재시작 후 명령어 재등록

#### 게임이 시작되지 않는 경우
1. **설정 파일 확인**: `config.yml` 파일의 좌표 값 확인
2. **월드 확인**: 설정된 월드가 존재하는지 확인
3. **권한 확인**: 관리자 권한 보유 확인

#### 성능 문제가 발생하는 경우
1. **메모리 확인**: 서버 메모리 사용량 확인
2. **플레이어 수 제한**: 동시 접속 플레이어 수 제한
3. **설정 최적화**: 불필요한 기능 비활성화

### 로그 분석
- **오류 로그**: `logs/latest.log`에서 `ERROR` 레벨 메시지 확인
- **경고 로그**: `WARN` 레벨 메시지로 잠재적 문제 파악
- **디버그 로그**: `DEBUG` 레벨로 상세 정보 확인

## 📄 라이센스

이 프로젝트는 MIT 라이센스 하에 배포됩니다. 자세한 내용은 `LICENSE` 파일을 참조하세요.

## 🤝 기여하기

프로젝트에 기여하고 싶으시다면 다음 단계를 따라주세요:

1. **Fork**: 이 저장소를 포크합니다
2. **브랜치 생성**: 새로운 기능을 위한 브랜치를 생성합니다
3. **변경사항 커밋**: 의미있는 커밋 메시지와 함께 변경사항을 커밋합니다
4. **Pull Request**: 변경사항을 검토받기 위해 Pull Request를 생성합니다

### 기여 가이드라인
- **코드 스타일**: Java 코딩 컨벤션을 따릅니다
- **테스트**: 새로운 기능에 대한 테스트를 작성합니다
- **문서화**: 코드 변경사항에 대한 문서를 업데이트합니다
- **이슈 보고**: 버그나 개선사항을 이슈로 보고합니다

## 📞 문의

프로젝트에 대한 질문이나 제안사항이 있으시면:

- **GitHub Issues**: [이슈 페이지](https://github.com/yourusername/MinigamePlugin/issues)에서 새로운 이슈를 생성해주세요
- **Pull Request**: 개선사항이나 버그 수정을 Pull Request로 제출해주세요
- **문서 개선**: README나 문서 개선 제안도 환영합니다

### 개발자 정보
- **개발자**: ydking0911
- **프로젝트**: MinigamePlugin
- **버전**: 1.0-SNAPSHOT
- **지원 버전**: Minecraft 1.21.1+

---

**참고**: 이 플러그인은 교육 및 개인 사용 목적으로 제작되었습니다. 상업적 사용 시 별도 문의가 필요합니다.
