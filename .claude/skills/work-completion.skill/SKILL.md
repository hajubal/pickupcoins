---
name: work-completion
description: 코드 수정 작업 완료 후 검증, 테스트, 문서화를 자동으로 수행합니다. 사용자가 "작업 완료" 명령을 입력할 때 실행됩니다.
---

# Work Completion Automation

## Purpose
코드 수정 작업 완료 후 검증, 테스트, 문서화를 자동으로 수행합니다.

## When to Use
- 사용자가 "작업 완료"라고 명령할 때
- 코드 수정이 끝나고 최종 검증이 필요할 때
- 작업 로그를 자동으로 기록하고 싶을 때

## Workflow

### 1. 코드 검토
최근 수정된 파일들을 확인하고 다음을 검토합니다:
- 코드 품질 및 잠재적 버그
- 베스트 프랙티스 준수 여부
- `docs/design/` 폴더의 설계 문서와의 일관성

### 2. 기능 확인
- 수정된 코드가 UI/화면 관련 기능인지 판단
- 해당하는 경우 브라우저를 실행하여 기능 테스트
- 주요 사용자 시나리오 검증

### 3. 테스트 코드 작성
- 수정된 기능에 대한 단위 테스트 작성 (`*.spec.ts`)
- 통합 테스트가 필요한 경우 작성 (`*.e2e-spec.ts`)
- 테스트 실행 및 결과 확인

```bash
# 단위 테스트 실행
npm test

# 특정 파일 테스트
npm test -- --testPathPattern=파일명

# E2E 테스트
npm run test:e2e

# 커버리지 확인
npm run test:cov
```

### 4. 작업 로그 기록
`docs/work-logs/` 폴더에 작업 내역을 기록합니다.

파일명 형식: `YYYY-MM-DD-feature-name.md`

### 5. 버전 업데이트
`package.json` 파일의 버전을 확인하고 필요시 업데이트합니다.
```bash
# 현재 버전 확인
npm pkg get version

# 버전 업데이트 (필요시)
npm version patch --no-git-tag-version
```

### 6. GitHub Issue 작업인 경우

#### Issue 번호 확인
1. **Git 커밋에서 자동 추출**: 최근 커밋 메시지에서 `#123` 패턴 검색
   ```bash
   git log --oneline -10 | grep -oE '#[0-9]+'
   ```
2. **자동 추출 실패 시**: 사용자에게 issue 작업 여부와 번호를 질문

#### Issue 상태 확인
```bash
gh issue view {ISSUE_NUMBER}
```

#### 댓글 작성
```bash
gh issue comment {ISSUE_NUMBER} --body "$(cat <<'EOF'
## 작업 완료

### 수정 파일
- `file1.ts`
- `file2.ts`

### 주요 변경사항
1. 변경 내용 1
2. 변경 내용 2

### 관련 커밋
- `abc1234`: feat: 기능 추가
- `def5678`: fix: 버그 수정

### 테스트 결과
- 단위 테스트 통과
- 통합 테스트 통과

---
🤖 *Automated by Claude Code*
EOF
)"
```

#### Issue 종료
댓글 작성 후 자동으로 issue를 close:
```bash
gh issue close {ISSUE_NUMBER}
```

#### 체크리스트
- [ ] Git log에서 issue 번호 확인 (또는 사용자에게 질문)
- [ ] Issue 상태 확인 (이미 closed인지)
- [ ] 작업 내용 요약 댓글 작성
- [ ] Issue close

## Work Log Template
```markdown
# [기능명] 작업 완료

## 작업 일시
YYYY-MM-DD HH:mm

## 작업 요청
[원본 요청 내용]

## 수정 파일
- file1.ts
- file2.ts

## 주요 변경사항
1. ...
2. ...

## 테스트 결과
- [ ] 단위 테스트 통과
- [ ] 통합 테스트 통과
- [ ] 화면 기능 확인

## 특이사항
...

## 후속 작업
...
```

## Example Interactions

**User**: "작업 완료"
**Claude**: 
1. Git status로 변경된 파일 확인
2. 코드 리뷰 수행
3. 화면 관련 기능이면 브라우저 테스트
4. 테스트 코드 작성
5. docs/work-logs/에 작업 로그 생성

## Project Context
- 프로젝트: PickupCoins NestJS Application (Node.js/TypeScript)
- 설계 문서 위치: `docs/design/`
- 작업 로그 위치: `docs/work-logs/`
- 테스트 파일: `src/**/*.spec.ts`, `test/**/*.e2e-spec.ts`
- 테스트 프레임워크: Jest
- 주요 명령어:
  - `npm test`: 단위 테스트
  - `npm run test:e2e`: E2E 테스트
  - `npm run lint`: ESLint 검사
  - `npm run build`: 빌드
