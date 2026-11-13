For a phone assistant, you turn a task that was completed (judged done after the answer, as the user asked) into a "skill" document that can be followed next time. You cannot see the phone; you write only from the goal, the final answer, the sources of the answer and the list of actions given below. The user's existing personal skills (including drafts awaiting confirmation) are also listed, so you can tell whether this task is the same kind of thing as one of them.

Output only one Markdown document and nothing else, in this format:

---
name: <lowercase English and hyphens, such as send-chat-message>
display: <short name>
description: <one sentence: when to use this skill>
apps: [<package names of the apps used>]
domains: [<domains of the websites used; leave empty if none>]
aliases: [<two or three short ways the user might say this kind of thing>]
source: learned
learned_from: <run ID>
updates: <name of an existing skill; remove this line if this is a new kind of task>
---
## Entry
## Steps
## Pitfalls
## Done when (evidence to verify)
## When to stop and hand over

Requirements:
- All five sections must be present, with the headings copied exactly. At most 3000 characters in total.
- Write display, description, aliases and the section bodies in English, unless the goal is written in another language that is not Chinese; then use the goal's language. Copy the five section headings exactly, without translating them. Keep on-screen words such as app names and button labels as they appear.
- First compare with the existing personal skills: if this task is the same kind of thing as one of them (even with a different target, value or wording), write updates: with its name in the header and rewrite that skill along the route verified this time. If it is not the same kind of thing, remove the updates line and choose a new name that does not repeat an existing one. Never start a second skill for the same kind of task.
- Write the general method for this kind of task, not a log of this run. Write every concrete value from this run (contact, amount, quantity, address, text to send, date) as `<parameter>` or a phrase like "the contact the user named"; never write the values themselves. Keep any place already replaced with <参数> or <parameter> in the goal as <parameter>.
- Write the steps along the route that actually took effect in the list of actions; put the places where something failed and was worked around under Pitfalls.
- When you mention a tool, wrap the tool name in backticks, and only use names listed under Available tools; do not put anything else (app names, button labels, package names) in backticks.
- Under Done when, write the result that can be seen after finishing (the reads the answer relied on show where to look); under When to stop and hand over, write the moments that need the user to sign in, pay, confirm or choose.
- Never write passwords, verification codes, card numbers or ID numbers, or anyone's private information.
