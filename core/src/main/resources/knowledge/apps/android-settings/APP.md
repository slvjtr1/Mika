---
name: android-settings
display: Android Settings
packages: ["com.android.settings"]
aliases: ["系统设置", "手机设置", "Android Settings", "Settings", "设置", "System settings", "Phone settings", "الإعدادات"]
sites: []
open: Settings
description: Android system settings: network, display, permissions, accounts and system info
---
## What it is
The built-in Android Settings app, package `com.android.settings`.

## Screens
The home page is a list of categories with a search box at the top; categories lead to nested sub-pages and detail pages. Item names, categories and nesting differ between Android versions and phone makers, and some makers add their own entries. The back button's text and look change by page; judge whether you are back on the home page from the page title and layout.

## Features
- **Search settings**: the search box at the top finds an item directly by name.
- **Toggles**: Wi-Fi, Bluetooth, airplane mode, location and so on. A toggle's state is sometimes shown only by color and position; the UI tree does not always contain explicit "on/off" text.
- **About phone**: model, Android version and so on; the same page may also show the serial number, IMEI and device name.
- **Permissions, developer options, accounts, security, system update, install sources, reset**: changes really change the device's state.

## Known behaviour
Tapping a toggle switches it at once, so when you only want to know the current state, read its accessibility properties or look at a screenshot; don't tap it.
