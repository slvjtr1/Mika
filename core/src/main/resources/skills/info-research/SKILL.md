---
name: info-research
display: Research something
description: Research something properly online: find leads, read original sources, cross-check, and state sources and dates
aliases: ["research", "look into", "find out about"]
apps: ["xiaohongshu", "zhihu", "bilibili", "weibo"]
sites: ["zhihu.com", "tieba.baidu.com", "weibo.com", "douban.com"]
terms: ["research", "look up", "find out", "background", "compare", "reliable", "source", "latest", "review"]
---
## Entry

First pin down the subject: full name, region, time period, version or model. When names clash or the wording is ambiguous, ask the user once, and meanwhile research the parts the question doesn't affect. For conditions the user didn't give (such as a time range), choose a common set yourself and state it at the start of the conclusion.

Start by opening a search page with `web_navigate` (Google, Bing, DuckDuckGo), or use the search inside a relevant app.

## Steps

1. **Find leads**: do two or three broader searches first to see how the subject is described, which sites cover it and whether there is an authoritative publisher. For in-site search, use two to four keywords, not a whole question.
2. **Find the original source**: for the fact the user directly asked about, trace it to where it was first published: the publisher's own site, an official announcement, the original document or paper, or the product or service page itself. Treat news and reposts only as leads, and follow their links or names to the original.
3. **Add experience and the other side**: for real user experience, look at communities and video platforms (Reddit, YouTube, forums and review sites); for problems, look at complaint sites. Do one search with negative words ("problem", "complaint", "downside").
4. **Cross-check**: read each key conclusion as original text in at least two unrelated sources. When they disagree, compare publish dates and which side is closer to the event, and write down the disagreement itself.
5. **Write the conclusion**: after each point, give the source (site or publisher) and the date of that content; keep what the source says separate from your own inference; for things you searched but didn't find, say where you looked and what is still missing.

## Pitfalls

- Search result snippets are not the original text and are often cut off or outdated; open the page with `web_read_page` to read the body, and note which site you finally landed on.
- One press release is often republished by many sites; it looks like many sources but is only one.
- Promotional posts, reviews with purchase links and anonymous claims are less reliable; label them when you use them.
- The date on a page may be when it was reposted or updated, not when the event happened.
- Finding nothing, a page that won't open or a sign-in wall only means you couldn't read it this time; it doesn't mean the thing doesn't exist.

## Done when (evidence to verify)

Every point traces back to a page actually read this time, with its source and date; key points have two independent sources; weak or conflicting points explain why.

## When to stop and hand over

When the content needs sign-in to view, or the next step becomes booking, buying or registering, something the user must decide or pay for, hand what you found to the user.
