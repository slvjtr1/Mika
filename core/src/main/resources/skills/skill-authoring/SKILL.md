---
name: skill-authoring
display: Write a skill
description: Use when the user wants to create, import or change a skill (a set way of doing one kind of task): how to write it, check for duplicates and save it
aliases: ["create a skill", "new skill", "write a skill", "save a skill", "import a skill", "edit a skill", "skill"]
terms: ["skill", "method", "process", "steps", "template", "always do it this way", "save as a skill", "import", "rewrite"]
---
## When to write one

- Write one when the user clearly asks to "create a skill", "save this method", "import this as a skill" or "change a skill". If they are only discussing a method or asking whether it's possible, answer first; don't save anything on your own.
- A skill describes how to do one kind of task from now on, not a one-time fact or preference. "I don't eat coriander" is a memory, not a skill.
- Only `skill_save` can write a skill. If that tool isn't available, tell the user plainly it can't be saved this time; never say it was saved.

## Check whether one exists first

1. Search once with `skill_search` using words for this kind of task.
2. If a personal skill already covers this kind of task: read the whole thing with `skill_read`, change it from there, and when saving keep its name and pass the version you read. Never start a second skill for the same kind of task.
3. If only a built-in skill matches: built-in skills can't be changed. When the user wants a different method, write it as their own skill under a new name, and tell the user which one will be used from now on.

## Clarify before writing

When the user gives only a vague sentence ("make me a skill for ordering coffee"), first use `ask_user` to settle the one or two points that matter most: which app or site, where to stop, and any fixed requirements. List the points you will save in a few lines for the user to confirm, then save. If the user already explained the method clearly or pasted complete content, don't ask again.

## How to write it

The whole thing is one document: a header plus five sections with exactly these headings, at most 4000 characters in total.

```
---
name: order-coffee
display: Order coffee
description: Use when the user wants a coffee delivered or for pickup
aliases: ["order coffee", "buy a coffee"]
---
## Entry
## Steps
## Pitfalls
## Done when
## When to stop and hand over
```

- name uses lowercase English letters, digits and hyphens and says what kind of task it is; display is a short name in English.
- description is one sentence on "when to use it"; put the ways the user might say it into aliases. Whether the skill is ever used depends on these two.
- Entry says where to start (which app, site or search); Steps are short numbered sentences, one action per step, naming buttons and pages, never coordinates.
- Write things that change every time (names, amounts, addresses, text to send) as `<parameter>` or "the contact the user named", never hard-coded.
- Pitfalls lists where things go wrong and how to get around them; Done when says where the result can be seen afterwards; When to stop and hand over lists the moments that need the user to sign in, pay, confirm or choose.
- Use backticks only for tool names, not for app names or button labels. Never write passwords, verification codes, card numbers or other people's private information.
- Shorter is better: don't write what the model already knows; write only what is specific to this kind of task and its traps.

## Importing content the user pastes

When the user pastes a skill, note or process written elsewhere, rewrite it in the format above: keep the method itself, remove what doesn't apply here (other software's commands, tools that don't work on a phone, scripts), and fill a missing section with a sentence or two in the original's spirit. Don't carry over accounts, passwords and the like from the original.

## Save and tell the user

1. Save with `skill_save`: markdown is the whole document, and quote copies the user's own words from this task asking you to create or change the skill.
2. Only an ok result means it was saved. Then tell the user, based on the result: the skill's name, whether it is new or an existing one was changed, that it can be seen in Settings › Memory & skills, and that it will be tried first next time this kind of task comes up.
3. On failure, fix it according to the reason and save again; if it still fails, say plainly it was not saved and why, never that it was created.
4. Renaming and deleting can't be done here: ask the user to delete the old one in Settings › Memory & skills; to change the name, save a copy under the new name and then ask the user to delete the old one.

## Done when

`skill_save` returns ok and the name in the result is the one meant to be saved; the name and whether it was new or changed in your answer match the result.
