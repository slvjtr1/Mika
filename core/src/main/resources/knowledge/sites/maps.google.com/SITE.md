---
name: maps.google.com
display: Google Maps
description: Look up place details and driving, transit and walking routes
domains: ["maps.google.com"]
aliases: ["Google Maps", "谷歌地图", "خرائط جوجل"]
apps: []
open: maps.google.com
mode: desktop
---
## What it is
Google's web map for place details and routes, usually without sign-in. The real URLs are under `google.com/maps`, sharing the google.com domain with Google Flights and others.

## Pages and URLs
- Search: `https://www.google.com/maps/search/<encoded query>`.
- Directions: `https://www.google.com/maps/dir/<encoded origin>/<encoded destination>`.

## Features
- **Places**: name, address, rating and review count, open status, full opening hours and phone. Opening hours and phone only show after expanding the details.
- **Directions**: open the directions panel, enter origin and destination, and switch the travel mode (driving, transit, walking and so on). Each mode lists several options, each with time, distance, the roads or transit lines used, and notes.

## Known behaviour
- A place name in the URL doesn't mean the page has that place selected; there are many branches with the same name, so check the name, address and city on the page.
- Pressing Enter directly in the origin or destination box may pick your current location, a recent search or another place with the same name; tapping from the suggestion list is more reliable.
- Switching travel mode or opening another option card refreshes the whole page; data read before belongs to the previous state.
