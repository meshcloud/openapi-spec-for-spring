# GitHub repository settings

This OpenTofu module manages the GitHub repository `meshcloud/openapi-spec-for-spring`:

- the repository itself: description, topics, visibility, features and merge methods
- the default branch `spring-boot-3.x`
- a ruleset for each maintained branch (`spring-boot-3.x`, `spring-boot-4.x`): no deletion, no force push,
  linear history, a pull request merged by rebase, a passing `build` check, and Conventional Commits
  headers (scope and `!` allowed)
- a ruleset that lets only org and repository admins create a branch `spring-boot-*`
- a ruleset that makes the release tags `spring-boot-*-v*` immutable
- `.github/dependabot.yml` on the default branch, rendered from `dependabot.yml.tftpl` with one block per
  maintained branch that has `dependabot = true`

Change `.github/dependabot.yml` only through the template and the branch list in `main.tf`, then apply. The
apply commits the file to the default branch. A plan shows a diff when someone edits the file directly.

A ruleset for a branch that does not exist yet applies as soon as someone creates the branch. For a new maintained
branch, add it to `maintained_branches`, apply, and then an org or repository admin creates the branch.

## Apply

A meshcloud org admin or a repository admin applies the module by hand. Both bypass every ruleset, so the apply can
commit `.github/dependabot.yml` without a pull request. The token needs the `repo` scope; a fine-grained token
needs the permissions "Administration" and "Contents" (read and write) on the repository, and creating the
repository needs an org member allowed to create public repositories.

```sh
gcloud auth application-default login  # access to the state bucket
export GITHUB_TOKEN=...
tofu -chdir=infra/github init
tofu -chdir=infra/github apply
```

## First apply

The first apply creates the repository. Run it in this order:

1. Apply only the repository.
2. Push `spring-boot-3.x`.
3. Apply everything, which creates the rulesets and sets the default branch.

The default branch must exist before it can be set and receive `.github/dependabot.yml`. The rulesets
must come after the push, because the existing history has merge commits and messages that are not
Conventional Commits.

```sh
tofu -chdir=infra/github apply -target=github_repository.this
git push git@github.com:meshcloud/openapi-spec-for-spring.git spring-boot-3.x
tofu -chdir=infra/github apply
```

GitHub detects the Apache-2.0 license from `LICENSE.txt` after the push.
