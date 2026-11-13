---
name: mail.google.com
display: Gmail
description: Search, read, compose and label mail in Gmail on the web
domains: ["mail.google.com"]
aliases: ["Gmail", "Google Mail", "谷歌邮箱", "جيميل"]
apps: ["gmail"]
open: mail.google.com
mode: desktop
---
## What it is
Google's web mail; needs a signed-in Google account.

## Pages and URLs
Entry `https://mail.google.com/`. Layout: the search box at the top; on the left Compose plus Inbox, Sent, Drafts, Spam, Bin and custom labels; the mail list in the middle. The UI language follows the account setting, so buttons are not necessarily in English. Appending `#search/<encoded query>` to the current account URL searches directly; the account number in the URL (such as `u/0`) is the signed-in account. Search syntax is in `references/gmail-search-syntax.md`.

## Features
- **List and search**: each row has sender, subject, date and unread state, but no body. The list scrolls virtually: rows above are removed as you scroll down, so the number of rows scrolled past is not the mailbox's total.
- **Email body**: opening an email shows sender, subject, body, time and attachments. Opening it may change its unread state.
- **Compose, reply, forward**: Compose writes a new email; to reply or forward, open that email first. Replying fills in the recipient and subject. A sent email appears in Sent; the compose window closing does not prove it was sent.

## Known behaviour
Signing in to a Google account and "Verify it's you" are two different checks.
