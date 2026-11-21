# SOPKATHON Android

SOPT 39th Sopkathon Android app. Kotlin, Jetpack Compose, single activity, type-safe Navigation Compose, Retrofit + kotlinx.serialization, Coil. No DI library.
One module `app`, package `org.sopt.sopkathon_android2`. Versions in `gradle/libs.versions.toml`.
Commands: build `./gradlew assembleDebug`, compile only `./gradlew :app:compileDebugKotlin`.

## Before editing

Find the task in [docs/index.md](docs/index.md) and read that page first. For a new screen, copy `presentation/feed`.

## Rules

- Don't add libraries, DI frameworks, or modules unless asked.
- Keep changes inside the task. Don't refactor, rename, or delete other code (including `Dummy*.kt` placeholders).
- Build with `./gradlew assembleDebug` before saying the work is done.

## Comments

Korean, only where the reason is not obvious. No comments that repeat the code.
End with a short noun form, not `~다` (`// 피그마 프레임 크기에 맞춤`, `// 연타 시 중복 요청 방지`).

## Security (public repository)

Never commit `local.properties` or the server URL. Do not read or print `local.properties`.
