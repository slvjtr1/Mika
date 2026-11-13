---
name: content-transcribe
display: Turn content into text
description: Turn videos, podcasts and articles into text: summaries, chapters, quotes or a rewritten article
aliases: ["summarize a video", "podcast notes", "article summary", "get subtitles"]
apps: ["bilibili", "xiaohongshu"]
sites: ["youtube.com", "xiaoyuzhoufm.com"]
terms: ["summary", "summarize", "transcript", "subtitles", "captions", "podcast", "chapters", "key points", "quotes"]
---
## Entry

First ask which result is wanted: a full transcript, a summary, chapters, quotes, or a rewritten article. If the user didn't say, write a summary of five to ten sentences.

Take the text from where the content is: for an article, read the page body; for a video, take the subtitles or a transcript in the description; for a podcast, see whether the episode page has a transcript or show notes. There is no speech-to-text here; when there is only audio, ask the user for a transcript or subtitle file.

## Steps

1. **Get the full text**: read the page body with `web_read_page`; subtitle or transcript files from the user are in `/workspace/inbox/`, read them with `read_file`.
2. **Read in parts**: for long content, read it in several parts with a little overlap, remove duplicates afterwards, and make sure no part is missing from start to end.
3. **Write in the requested form**: how to write each form, with examples, is in `references/transcript-output-formats.md`; read it before writing. A summary's outline: one or two sentences of overview; three to eight topics in order of appearance, each with its approximate start time and two or three sentences; finally the notable views, numbers and books or works mentioned.
4. **Write in English**, unless the user asks for another language.
5. **Re-read**: after writing, check against the original: is the order right, is any topic missing, can every time and quote be found in the original.

## Pitfalls

- Subtitles are only text; charts, demos and on-screen text in the video can't be read, so never write as if you saw the picture.
- Auto-generated subtitles often have typos and wrong sentence breaks; check proper nouns, names and numbers against the context or description.
- Times are estimated from subtitle positions, so write "around minute 23", not to the second.
- Mark passages that are inaudible or missing from the subtitles; don't fill them in.

## Done when (evidence to verify)

The result covers every part of the original content; every quote and time can be found in the subtitles or text that were read.

## When to stop and hand over

When the content needs sign-in or payment to view, or there is only audio with no transcript at all, tell the user what is missing and ask them to provide it.
