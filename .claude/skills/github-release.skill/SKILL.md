---
name: github-release
description: GitHub 릴리즈를 생성합니다. 버전 업데이트, 태그 생성, 릴리즈 노트 자동 작성을 수행합니다. 사용자가 "릴리즈" 또는 "/release" 명령을 입력할 때 실행됩니다.
---

# GitHub Release Automation

## Purpose
GitHub 릴리즈 프로세스를 자동화합니다:
- 버전 검증 및 업데이트
- Git 태그 생성
- 커밋 기반 릴리즈 노트 자동 생성
- GitHub Release 생성

## When to Use
- 사용자가 "릴리즈" 또는 "/release"라고 명령할 때
- 새 버전을 배포하고 싶을 때
- GitHub Release를 자동으로 생성하고 싶을 때

## Prerequisites
1. Git 저장소 (원격: GitHub)
2. `gh` CLI 설치 및 인증 (`gh auth login`)
3. GitHub push 권한

## Workflow

### 1. 사전 확인
다음을 확인합니다:
```bash
# Git 상태 확인 (클린 상태여야 함)
git status

# 현재 버전 확인
git describe --tags --abbrev=0

# gh 인증 상태 확인
gh auth status
```

**중요**: 커밋되지 않은 변경사항이 있으면 먼저 커밋하거나 stash해야 합니다.

### 2. 버전 결정
프로젝트의 버전 파일을 확인합니다:
- `package.json`: `"version": "X.Y.Z"`

**Semantic Versioning 규칙**:
- **MAJOR (X)**: 호환성 깨지는 변경
- **MINOR (Y)**: 새로운 기능 (하위 호환성 유지)
- **PATCH (Z)**: 버그 수정

사용자에게 새 버전을 확인받습니다.

### 3. 릴리즈 노트 자동 생성
최신 릴리즈 태그부터 현재까지의 커밋을 수집하고 분류합니다:

```bash
# 최근 태그 찾기
git describe --tags --abbrev=0

# 커밋 로그 수집
git log <previous-tag>..HEAD --oneline
```

커밋 메시지를 다음 카테고리로 분류합니다:
- **feat**: 새로운 기능
- **fix**: 버그 수정
- **docs**: 문서 변경
- **refactor**: 리팩토링
- **chore**: 기타 작업

**릴리즈 노트 템플릿**:
```markdown
## v{VERSION}

### 새로운 기능
- feat 커밋들

### 버그 수정
- fix 커밋들

### 문서
- docs 커밋들

### 기타
- chore/refactor 커밋들
```

### 4. 버전 파일 업데이트
`package.json` 파일의 버전을 업데이트합니다:
```json
"version": "{NEW_VERSION}"
```

또는 npm 명령어를 사용할 수 있습니다:
```bash
npm version {NEW_VERSION} --no-git-tag-version
```

### 5. Git 작업
```bash
# 변경 사항 커밋
git add package.json package-lock.json
git commit -m "chore: Release v{VERSION}"

# 버전 태그 생성
git tag v{VERSION}

# 원격에 푸시
git push origin main --tags
```

### 6. GitHub Release 생성
`gh` CLI를 사용하여 Release를 생성합니다:
```bash
gh release create v{VERSION} \
  --title "v{VERSION}" \
  --notes "{RELEASE_NOTES}"
```

### 7. 검증
```bash
# 릴리즈 확인
gh release view v{VERSION}

# 또는 브라우저에서 확인
gh release view v{VERSION} --web
```

## Checklist

- [ ] Git 상태 클린 확인
- [ ] 새 버전 결정 (Semantic Versioning)
- [ ] 변경사항 확인
- [ ] 릴리즈 노트 작성
- [ ] package.json 버전 업데이트
- [ ] Git 커밋 및 태그 생성
- [ ] GitHub Release 생성
- [ ] 릴리즈 페이지 검증

## Example Interactions

**User**: "릴리즈"
**Claude**:
1. Git 상태 확인 (클린 여부)
2. 현재 버전 확인 및 새 버전 제안
3. 커밋 로그 분석하여 릴리즈 노트 생성
4. 사용자 승인 요청
5. package.json 버전 업데이트
6. Git 커밋, 태그 생성, 푸시
7. gh release create 실행
8. 결과 확인 및 링크 제공

**User**: "v1.2.0으로 릴리즈해줘"
**Claude**:
1. 지정된 버전으로 바로 진행
2. 나머지 동일

## Error Handling

### gh 인증 실패
```bash
gh auth login
```

### 이미 존재하는 태그
- 기존 태그 삭제 후 재생성
- 또는 다른 버전 선택

### push 권한 없음
- GitHub 권한 확인
- SSH 키 또는 토큰 확인

## Project Context
- 프로젝트: PickupCoins NestJS Application (Node.js)
- 버전 파일: `package.json`
- 현재 버전: `2.0.0`
- 커밋 컨벤션: 한글, 이모지 없음 (feat/fix/docs/chore/refactor)
- 패키지 매니저: npm
