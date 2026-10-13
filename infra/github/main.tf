locals {
  default_branch = "spring-boot-3.x"

  # Dependabot fails on a target branch that does not exist yet.
  maintained_branches = {
    "spring-boot-3.x" = {
      dependabot = true
      ignored_majors = {
        reason       = "The next majors are Spring Boot 4, Spring Framework 7 and Jackson 3, which belong to another branch."
        dependencies = ["org.springframework*", "com.fasterxml.jackson*", "tools.jackson*"]
      }
    }
    "spring-boot-4.x" = {
      dependabot = false
      ignored_majors = {
        reason       = "The next majors are Spring Boot 5, Spring Framework 8 and Jackson 4, which belong to another branch."
        dependencies = ["org.springframework*", "tools.jackson*"]
      }
    }
  }

  # The provider cannot resolve an app slug to an id. Read one off a commit that carries the check:
  #   gh api /repos/meshcloud/openapi-spec-for-spring/commits/<sha>/check-runs \
  #     --jq '.check_runs[] | {name, app_id: .app.id, app: .app.slug}'
  github_actions_app_id = 15368
}

resource "github_repository" "this" {
  name        = "openapi-spec-for-spring"
  description = "Derives an OpenAPI 3 spec at runtime from Spring WebMVC controllers"
  visibility  = "public"
  topics      = ["openapi", "spring-boot", "openapi3", "spring-webmvc", "spring-hateoas"]

  has_issues   = true
  has_wiki     = false
  has_projects = false

  allow_merge_commit     = false
  allow_squash_merge     = false
  allow_rebase_merge     = true
  delete_branch_on_merge = true

  archive_on_destroy = true
}

resource "github_branch_default" "this" {
  repository = github_repository.this.name
  branch     = local.default_branch
}

resource "github_repository_ruleset" "protect_branch" {
  for_each = toset(keys(local.maintained_branches))

  repository  = github_repository.this.name
  name        = "Protect ${each.key}"
  target      = "branch"
  enforcement = "active"

  bypass_actors {
    actor_id    = 0 # GitHub stores 0 for the role-based org admin actor
    actor_type  = "OrganizationAdmin"
    bypass_mode = "always"
  }

  bypass_actors {
    actor_id    = 2 # maintain role
    actor_type  = "RepositoryRole"
    bypass_mode = "always"
  }

  conditions {
    ref_name {
      include = ["refs/heads/${each.key}"]
      exclude = []
    }
  }

  rules {
    deletion                = true
    non_fast_forward        = true
    required_linear_history = true

    commit_message_pattern {
      name     = "Conventional Commits header"
      operator = "regex"
      pattern  = "^(feat|fix|ci|chore|docs|test|style|refactor|config|hotfix)(\\([a-z0-9._/-]+\\))?!?: .+"
    }

    pull_request {
      required_approving_review_count   = 0
      required_review_thread_resolution = true
      allowed_merge_methods             = ["rebase"]
      dismiss_stale_reviews_on_push     = false
      require_code_owner_review         = false
      require_last_push_approval        = false
    }

    required_status_checks {
      # The pull request must be up to date with the branch, so the check covers the code that lands.
      strict_required_status_checks_policy = true

      required_check {
        context        = "build"
        integration_id = local.github_actions_app_id
      }
    }
  }
}

resource "github_repository_ruleset" "protect_release_tags" {
  repository  = github_repository.this.name
  name        = "Protect release tags"
  target      = "tag"
  enforcement = "active"

  bypass_actors {
    actor_id    = 0 # GitHub stores 0 for the role-based org admin actor
    actor_type  = "OrganizationAdmin"
    bypass_mode = "always"
  }

  conditions {
    ref_name {
      include = ["refs/tags/spring-boot-*-v*"]
      exclude = []
    }
  }

  rules {
    deletion = true
    update   = true
  }
}
