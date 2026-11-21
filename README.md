# 🚀 서비스 이름

> 한 줄 소개를 적어주세요

<!-- 서비스 대표 이미지나 로고 -->

<br>

## 📱 서비스 소개

서비스를 간단히 소개해주세요.

<br>

## 👥 팀원 소개

| 박찬미 | 공승준 | 신한서 | 정소연 |
| :---: | :---: | :---: | :---: |
| <img src="" width="160" /> | <img src="" width="160" /> | <img src="" width="160" /> | <img src="" width="160" /> |
| [@chanmi1125](https://github.com/chanmi1125) | [@github-id]() | [@github-id]() | [@github-id]() |
| 역할 | 역할 | 역할 | 역할 |

<br>

## ✨ 주요 기능

| 기능 | 설명 |
| --- | --- |
| 기능 1 | 설명 |
| 기능 2 | 설명 |
| 기능 3 | 설명 |

<br>

## 🛠️ 기술 스택

| 분류 | 기술 |
| --- | --- |
| Language | Kotlin |
| UI | Jetpack Compose, Material3 |
| Architecture | Single Activity, Route/Screen + ViewModel(StateFlow) |
| Navigation | Navigation Compose (Type-safe) |
| Network | Retrofit, OkHttp, Kotlinx Serialization |
| Image | Coil |
| Min / Target SDK | 24 / 36 |

<br>

## 📂 폴더 구조

```
org.sopt.sopkathon_android2
├── core
│   ├── common        # extensions, util
│   ├── designsystem  # component, theme
│   ├── navigation
│   └── network
├── data
│   ├── di            # NetworkModule, ServiceModule
│   ├── dto           # request, response
│   └── service
└── presentation
    ├── main          # MainActivity, MainScreen, navigation
    └── home          # component, model, Contract, Screen, ViewModel
```

<br>

## 🌿 브랜치 전략

Git Flow를 사용해요.

| 브랜치 | 설명 |
| --- | --- |
| `main` | 배포 브랜치 |
| `develop` | 개발 브랜치, 기능 브랜치가 머지되는 곳 |
| `feature` | 기능 개발 브랜치 |
| `hotfix` | 긴급 수정 브랜치 |

- 브랜치명: `유형/#이슈번호-작업내용` (예: `feat/#4-readme`)
- 항상 최신 `develop`에서 브랜치를 만들어요.

<br>

## 📝 컨벤션

### Issue
- 이슈명: `[Feat] 버튼 컴포넌트`

### Commit
- 형식: `[유형/#이슈번호] 작업 내용` (예: `[feat/#4] README 작성`)
- 머지 커밋: `[Merge] #이슈번호 -> develop`

| 유형 | 설명 |
| --- | --- |
| `init` | 프로젝트 초기 세팅 |
| `feat` | 새로운 기능 구현 |
| `refactor` | 코드 리팩토링 |
| `add` | 리소스, 라이브러리 등 추가 |
| `chore` | 빌드, 설정 등 기타 작업 |
| `style` | 코드 포맷, UI 스타일 변경 |

### Code
| 대상 | 규칙 |
| --- | --- |
| 함수, 변수 | lowerCamelCase |
| 클래스, 인터페이스 | UpperCamelCase |
| 상수 (`const val`) | UPPER_SNAKE_CASE |
| 리소스 (drawable 등) | snake_case |

<br>

## 📸 안드로이드 팀 사진

| Before | After |
| :---: | :---: |
| <img src="" width="400" /> | <img src="" width="400" /> |