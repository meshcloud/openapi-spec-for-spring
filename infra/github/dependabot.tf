resource "github_repository_file" "dependabot_yml" {
  repository = github_repository.this.name
  branch     = github_branch_default.this.branch
  file       = ".github/dependabot.yml"
  content = templatefile("${path.module}/dependabot.yml.tftpl", {
    default_branch = local.default_branch
    branches       = { for name, branch in local.maintained_branches : name => branch if branch.dependabot }
  })
  commit_message      = "ci: update the Dependabot config"
  overwrite_on_create = true
}
