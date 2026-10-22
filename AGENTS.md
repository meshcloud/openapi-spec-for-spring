# Agent guidelines

> **This repository is public.** Write everything here so that a contributor without meshcloud access can follow it.
> References to the private sibling repository `../meshfed-release` are meshcloud-internal and optional.

## Code and comments

- Write self-explanatory code. A comment stays only when it says what the code cannot: the reason for a decision a
  reader would otherwise reverse, a rejected alternative, an external constraint, or a link to code this must stay in
  step with. Prefer one short line. (meshcloud-internal: `../meshfed-release/PRINCIPLES.md`, "Self-Explanatory
  Code", and the skills `self-explanatory-code` and `refine-code-comments` in `../meshfed-release/.claude/skills/`.)
- Javadoc on a `@ConfigurationProperties` field stays: the configuration processor turns it into the property
  description that IDEs show.
- Write comments, commit messages and PR text in plain English: common words, exact technical terms, no idioms.
  (meshcloud-internal: `../meshfed-release/.claude/output-styles/simple-language.md`.)

## License

This repo is a hard fork of `qaware/openapi-generator-for-spring` under Apache 2.0. Apache 2.0 §4(c) requires
keeping QAware's copyright line, so add meshcloud's line below it and never remove QAware's:

```
 * Copyright (C) 2020-2023 QAware GmbH
 * Copyright (C) 2026 meshcloud GmbH
```

A new file without QAware code carries only the meshcloud line. Keep `LICENSE.txt` and `NOTICE`, which names QAware
as the original author.

## Commits

- The ruleset enforces `^(feat|fix|perf|ci|chore|docs|test|style|refactor|config|hotfix)(\([a-z0-9._/-]+\))?!?: .+`, so
  a scope and the breaking marker are allowed (`fix(webmvc)!: …`). The body says why. (meshcloud-internal: the
  `conventional-commits` skill in `../meshfed-release/.claude/skills/`.)
- Only rebase merges are allowed, so every commit lands on the branch: one concern per commit, and fix an earlier
  commit with `fixup!` and `git rebase --autosquash` before you push.
- Mechanical commits (formatting, package moves) stand alone and go into `.git-blame-ignore-revs`.

## Build

```sh
nix develop path:. --command ./gradlew build
```
