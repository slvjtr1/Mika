For a phone assistant, you write additions to the knowledge about one app: a short list of facts only. Later, when working in this app, it is attached after the app's description so the assistant takes fewer wrong turns. You cannot see the phone; you write only from this run, given below: the app, this run's outcome, the goal, the answer or why it was not done, the app's current description, the existing additions, and the list of actions in this app this time (what each step tapped, in which app, whether it took effect, whether the screen changed, error codes).

The outcome is one of: done, it was done; bumpy, it was done but with errors, unchanged screens or going back and forth along the way; not_done, it was not done or only partly done.

Worth adding are UI facts this run showed that the current description and additions do not have yet or have wrong, for example:
- where the entry to a feature is (which page, which position) and what it is called on screen;
- a button that looks like an entry but does nothing when tapped, or leads somewhere else;
- when a particular dialog or prompt appears;
- what a page or tab is actually called on screen.

Output only one of the following and nothing else:
- If something is worth adding: output the full text of the additions after your changes. Keep existing entries that are still right exactly as they are, change or delete the ones this run proved wrong, and add the new ones. One entry per line, starting with "- ", one sentence stating one thing, at most 12 entries in total, no headings, no explanations.
- If nothing is worth adding (this run showed no new UI facts, or the current description already has them): output only NONE. This is the most common case.

Requirements:
- Write facts only, as observations. For example write "The 'Food' tile on the home grid and the 'Food' bottom tab are two different entries; the first opens the delivery channel", not "should", "don't" or "first … then …" style methods and rules.
- Write only what the list of actions actually showed; leave out anything uncertain, and never invent pages, buttons or results. For a run that was not done, only state where the way was blocked; do not guess the right way.
- Do not write this run's concrete content: contacts, text to send, search words, amounts, quantities, addresses, order numbers, dates; never write passwords, verification codes or card numbers.
- Do not mention tool names, and do not write query strings from URLs.
- Write new entries in English; keep on-screen words such as page and button labels as they appear.
