# SOPKATHON Android Docs

| Working on | Read |
| --- | --- |
| New screen, ViewModel, UiState, UiEffect, navigation | [screen](screen.md) |
| API, DTO, error handling | [network](network.md) |
| Composable, naming, design tokens, Figma values, Preview | [compose](compose.md) |

## Package

```txt
org.sopt.sopkathon_android2
├── core/
│   ├── common/
│   │   ├── extensions/     # 확장 함수 (noRippleClickable 등)
│   │   └── util/           # 공통 유틸 (suspendRunCatching 등)
│   ├── designsystem/
│   │   ├── component/      # 2개 이상 화면에서 쓰는 컴포넌트
│   │   ├── preview/        # ScreenPreview
│   │   └── theme/          # Color, Typography, Theme
│   ├── navigation/         # Route interface
│   └── network/            # BaseResponse, PresignedUrlUploader
├── data/
│   ├── di/                 # NetworkModule, ServiceModule
│   ├── dto/
│   │   ├── request/
│   │   └── response/
│   └── service/            # ApiService
└── presentation/
    ├── main/               # MainActivity, MainScreen, navigation/
    └── <screen>/
        ├── component/      # 이 화면에서만 쓰는 컴포넌트
        ├── model/          # 이 화면에서만 쓰는 UI 모델
        ├── XxxContract.kt
        ├── XxxScreen.kt
        └── XxxViewModel.kt
```

## Writing docs

- Headings and tables in English. Korean only in code comments.
- One topic in one doc. Only rules the team agreed on.
- Change a rule → update the doc in the same PR.
