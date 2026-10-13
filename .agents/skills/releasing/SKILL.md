---
name: releasing
description: Release a version of the library from a spring-boot-*.x branch.
disable-model-invocation: true
---

A release is a tag `<branch>-v<semver>` on a commit of that branch, for example `spring-boot-3.x-v1.0.0`. The
`release` workflow (`.github/workflows/release.yml`) does the rest:

1. It fails unless the tagged commit is on the branch named in the tag.
2. It runs the build and publishes `<semver>` to GitHub Packages.
3. It creates a GitHub Release with generated notes.

The ruleset "Protect release tags" blocks deleting or moving these tags for everyone except org admins, and GitHub
Packages keeps the published version anyway, so treat a pushed tag as final. Check before you push it:

```sh
git fetch origin
git --no-pager log --oneline -1 origin/spring-boot-3.x
git tag spring-boot-3.x-v1.0.0 origin/spring-boot-3.x
git push origin spring-boot-3.x-v1.0.0
```

Pick the version by semver against the previous tag on the same branch. A change to an interface in
`openapi-spec-for-spring-api` that breaks implementations is a major version.
