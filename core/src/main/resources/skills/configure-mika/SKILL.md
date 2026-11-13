---
name: configure-mika
display: Change Mika's own settings
description: Use when the user asks Mika to view or change its own settings; models, keys and deleting data are not included
aliases: ["change your settings", "adjust yourself", "go faster", "don't mute", "change the look"]
terms: ["settings", "configure", "faster", "slower", "mute", "appearance", "dark", "light", "capsule", "control mode"]
---
## See what can be changed first

Call `settings_list` first. It lists every item that can be changed right now: the key, a human-readable name, the current value and, when there are only a few choices, all allowed values.

**Use only keys and values from the list**; never guess a word. When what the user said doesn't match what you see (for example the user says "don't mute" and the list calls it "mute_apps"), pick the item by its name and read its `label` back to the user to confirm.

## Say it before changing

When the user clearly says what to change, change it directly with `settings_set` and tell them the new value. When the request is vague ("make it faster"), first say which item you plan to change and to what, then change it:

> I set the operating pace to Fast; it takes effect from the next task.

**Do not change anything the user didn't mention.** When changing several items at once, mention each one.

## What can't be changed

- **Models, API keys, sign-in**: not in the list. When the user asks for these, tell them to go to Settings › Model; you can't do this step.
- **Deleting data** (clearing the workspace, resetting the environment, deleting conversations): not in the list either; ask the user to do it themselves.
- **Container network access and proxy**: not in the list. These let data leave the phone, so the user must turn them on or fill them in themselves in Settings › Files & computing; don't change them for the user.

These are deliberate: the first would let the model replace its own brain or spend the user's money; the second can't be undone; the network line is the user's to draw.

## When changes take effect

Most changes take effect from the next task; appearance (dark or light) changes at once. Mention this after changing, so the user doesn't think it didn't work.
