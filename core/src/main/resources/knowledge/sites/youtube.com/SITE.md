---
name: youtube.com
display: YouTube
domains: ["youtube.com", "youtu.be"]
aliases: ["YouTube", "油管", "يوتيوب"]
apps: []
open: youtube.com
mode: desktop
description: Video platform: search videos and channels, read descriptions, comments and subtitle text
---
## What it is
A video platform.

## Pages and URLs
- Search: `https://www.youtube.com/results?search_query=<encoded words>`.
- Video: `/watch?v=<id>`; channels use the channel link.

## Features
- **Videos**: search results show title, channel, length, view count and upload time. On the video page you can expand the description and scroll down to load comments. Public videos need no sign-in.
- **Channels**: name, subscriber count, About and latest videos.
- **Subtitles**: for videos with subtitles, the subtitle text can be fetched; long videos have very long subtitles.

## Known behaviour
- View and subscriber counts are often abbreviated, such as "120K" or "1.2M"; the exact number is on the detail view.
- Private, members-only, age-restricted or region-blocked videos can't be watched; that is different from "no search results".
- Failing to get subtitles can mean the video has none, it is restricted, or a network problem.
