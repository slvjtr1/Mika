---
name: gmail
display: Gmail
packages: ["com.google.android.gm"]
aliases: ["Gmail", "谷歌邮箱", "Google 邮箱", "gmail", "Google Mail", "جيميل"]
sites: ["mail.google.com"]
open: Gmail
description: Google's email app: send and receive mail, organize by category and label, plus Meet
---
## What it is
Gmail is Google's email app, package `com.google.android.gm`. Other email accounts can be added and managed alongside.

## Screens
Two bottom tabs: Mail and Meet. (Labels below are the English UI; they follow the system language.)
- **Mail**: top left is the menu button ("Open navigation drawer"), in the middle the search box "Search in mail", on the right the avatar ("Account and settings"). The list starts with the current inbox name (such as Primary), then summary rows for categories such as Updates and Promotions (with new-mail counts), then the emails; bottom right is the Compose button.
- **Drawer**: inbox categories (Primary, Promotions, Social, Updates); under "All labels": Starred, Snoozed, Important, Sent, Scheduled, Outbox, Drafts, All mail, Spam, Bin, Manage subscriptions, Create new, and custom labels; under "Google apps": Calendar, Contacts; at the very bottom Settings and Help & feedback, which only show after scrolling down.
- **Meet**: has "New meeting" and "Join with a code".

## Features
- **Mail list**: one row per email; the element name joins "Unread", sender, subject and snippet; the date and star are separate elements. Tapping the sender's avatar selects the email rather than opening it.
- **Search**: tap the search box at the top; there is a back button, the input and filter chips: Label, From, To, Attachment.
- **Account**: tapping the avatar opens the account page: Manage your Google Account, switch account, Add another account, Manage accounts on this device, storage used.
- **Settings**: from the bottom of the drawer; the first level has General settings, one row per account, and Add account.

## Known behaviour
- On first launch or after an update, these may appear in turn: a "new look" intro page ("Got it"), a page about managing all your email addresses ("Take me to Gmail"), the system notification permission prompt ("Don't allow" / "Allow"), a card introducing Meet ("Learn more" / "Got it"), and a tip card at the top of the list ("Dismiss").
- The system notification permission prompt has no readable elements and can't be closed with Back; tap the buttons on the image.
- The first time you open the Meet tab, "Allow Gmail to make and manage phone calls?"-style prompts may appear ("Cancel" / "Grant permission") without readable elements.
- The account page has no readable elements; use the image.
- While the drawer or search page is open, the mail list behind it is still in the element list; drawer items come after the emails.
- Pressing Back on the mail list exits the app.
