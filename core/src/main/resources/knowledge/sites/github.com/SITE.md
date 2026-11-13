---
name: github.com
display: GitHub
description: Code hosting and collaboration site: browse repositories, code, issues and pull requests
domains: ["github.com"]
aliases: ["GitHub", "github", "جيت هب"]
apps: []
open: github.com
mode: desktop
---
## What it is
A code hosting and collaboration site; repositories have code, docs, Issues and Pull requests.

## Pages and URLs
- Repository: `https://github.com/<owner>/<repo>`.
- A repository's issues and pull requests are at `/issues` and `/pulls` after the repository URL.
- Search: `https://github.com/search?q=<encoded words>&type=repositories`; code, issues and other types can be searched too.

## Features
- **Repositories**: public repositories usually show files, README, commits, branches, issues and pull requests without sign-in; private ones need an account with access.
- **Star**: the Star button at the top right of a repository page bookmarks it and shows support; it needs sign-in. When the current account has starred it, the button shows Starred, so the button state tells you whether it is starred.
- **Account**: what you can see and do after signing in depends on the account's permissions; button text follows the UI language.

## Known behaviour
- When a repository doesn't exist, is private or the account has no access, the page may only show "page not found".
- Star counts may be abbreviated, such as `1.2k`.
