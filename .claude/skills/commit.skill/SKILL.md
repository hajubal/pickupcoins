---
name: commit
description: 변경사항을 Git 커밋합니다. 변경 내용을 분석하고 커밋 메시지를 자동으로 작성합니다. 사용자가 "/commit" 명령을 입력할 때 실행됩니다.
---

# Git Commit Automation

## Purpose
Git 변경사항을 분석하고 적절한 커밋 메시지와 함께 커밋합니다.

## When to Use
- 사용자가 "/commit" 또는 "커밋"이라고 명령할 때
- 코드 수정 후 변경사항을 저장소에 커밋하고 싶을 때

## Workflow

### 1. 변경사항 확인
다음 명령어를 병렬로 실행하여 현재 상태를 파악합니다:
```bash
# 변경된 파일 확인 (never use -uall flag)
git status

# staged/unstaged 변경사항 확인
git diff
git diff --cached

# 최근 커밋 메시지 스타일 확인
git log --oneline -10
```

### 2. 변경 내용 분석
변경 내용을 분석하여 커밋 타입을 결정합니다:
- **feat**: 새로운 기능 추가
- **fix**: 버그 수정
- **docs**: 문서 변경
- **style**: 코드 스타일 변경 (포맷팅, 세미콜론 등)
- **refactor**: 리팩토링 (기능 변경 없음)
- **test**: 테스트 추가/수정
- **chore**: 빌드 작업, 패키지 관리

### 3. 커밋 메시지 작성 규칙
- **언어**: 한글로 작성
- **이모지**: 사용 금지
- **형식**: `{type}: {설명}`
- **설명**: 변경의 "무엇을" 보다 "왜"에 초점
- **길이**: 간결하게 1-2문장

예시:
```
feat: 사용자 인증 API 추가
fix: 라이센스 만료 검증 로직 수정
docs: README.md API 문서 업데이트
style: import 정렬 및 코드 포맷팅 정리
refactor: LicenseService 중복 코드 제거
test: AdminController 단위 테스트 추가
chore: npm 의존성 업데이트
```

### 4. 파일 스테이징
- 특정 파일 단위로 스테이징 (git add -A 대신 개별 파일명 사용)
- 민감한 파일(.env, credentials 등)은 제외
- 대용량 바이너리 파일 제외

### 5. 커밋 실행
HEREDOC 형식으로 커밋 메시지 전달:
```bash
git commit -m "$(cat <<'EOF'
{type}: {설명}

Co-Authored-By: Claude Opus 4.5 <noreply@anthropic.com>
EOF
)"
```

### 6. 결과 확인
```bash
git status
git log -1
```

## Safety Rules
- NEVER update git config
- NEVER run destructive commands (push --force, reset --hard, etc.)
- NEVER skip hooks (--no-verify)
- NEVER amend previous commits unless explicitly requested
- NEVER commit .env, credentials, or sensitive files
- NEVER push to remote unless explicitly requested

## Pre-commit Hook Failure
훅 실패 시:
1. 문제 수정
2. 파일 다시 스테이징
3. **새로운 커밋 생성** (--amend 사용 금지)

## Example Interactions

**User**: "/commit"
**Claude**:
1. git status/diff 실행
2. 변경 내용 분석
3. 커밋 타입 및 메시지 결정
4. 파일 스테이징
5. 커밋 실행
6. 결과 확인

**User**: "/commit fix: 로그인 버그 수정"
**Claude**:
1. 지정된 메시지로 커밋 진행
2. 나머지 동일

## Checklist
- [ ] git status로 변경사항 확인
- [ ] git diff로 변경 내용 분석
- [ ] 커밋 타입 결정 (feat/fix/docs/style/refactor/test/chore)
- [ ] 한글 커밋 메시지 작성 (이모지 없음)
- [ ] 민감한 파일 제외 확인
- [ ] 개별 파일 스테이징
- [ ] 커밋 실행
- [ ] git status로 성공 확인

## Project Context
- 프로젝트: PickupCoins NestJS Application (Node.js/TypeScript)
- 커밋 메시지: 한글, 이모지 없음
- Co-Author: Claude Opus 4.5 <noreply@anthropic.com>
- 주요 파일 확장자: `.ts`, `.tsx`, `.json`
