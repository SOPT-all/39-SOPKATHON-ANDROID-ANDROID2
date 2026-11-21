# 🚀 서비스 이름

**👔 부끄부끄**

> 동료의 코디를 함께 고르며 직급과 세대를 넘어 소통하는 사내 옷 입히기 서비스

<!-- 서비스 대표 이미지나 로고 -->

<br>

## 📱 서비스 소개

동료의 코디를 함께 고르고 공유하며 직급과 세대를 넘어 가볍게 소통할 수 있도록 돕는 사내 옷 입히기 서비스예요.

<br>

## 👥 팀원 소개

| 박찬미 | 공승준 | 신한서 | 정소연 |
| :---: | :---: | :---: | :---: |
| <img src="" width="160" /> | <img src="" width="160" /> | <img src="" width="160" /> | <img src="" width="160" /> |
| [@chanmi1125](https://github.com/chanmi1125) | [@github-id]() | [@github-id]() | [@github-id]() |
| 피드<br>동료 선택<br>프로필 검색<br>아이콘 삽입 | 옷 입히기<br>게시글 업로드<br>옷 드래그 + 줌인/줌아웃 | 프로필 생성<br>게시글 상세 + 바텀시트<br>이미지 누끼 | 내 프로필<br>동료 프로필<br>디자인 시스템 |

<br>

## ✨ 주요 기능

| 기능 | 설명 |
| --- | --- |
| 프로필 만들기 | 이름과 직급을 입력하고 사진을 등록해 프로필 생성 |
| 동료 선택하기 | 직급에 관계없이 코디를 만들어줄 동료 선택 |
| 옷 입히기 | 동료의 얼굴은 고정하고, 옷장에서 상의와 하의를 업로드 후 골라 캐릭터에 입히기 |
| 코디 게시하기 | 완성한 코디에 소개 글을 작성해 팀 피드에 공유 |
| 피드와 프로필 둘러보기 | 동료의 게시물을 둘러보고, 프로필에서 코디를 모아보며 좋아요와 댓글로 반응 |

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
|<img width="370" height="250" alt="4D5BA886-88D3-4CEC-A461-5B37EC69E5F4" src="https://github.com/user-attachments/assets/f6c62fb8-e8b9-4b28-8cf3-ee69c6e9552d" />| <img src="" width="400" /> |
