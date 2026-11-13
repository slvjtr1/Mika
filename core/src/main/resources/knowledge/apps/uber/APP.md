---
name: uber
display: Uber
packages: ["com.ubercab"]
aliases: ["Uber", "优步", "uber", "أوبر"]
sites: []
open: Uber
description: Ride types, prices and trips
---
## What it is
Uber is a ride-hailing app, package `com.ubercab`.

## Screens
The bottom tabs are Home, Services, Activity and Account. "Where to?" on Home sets up a trip; entries such as Later book a trip for a set time.

## Features
- **Estimates**: after filling in pickup and destination you get a list of ride types with name, estimate in the local currency, ETA, capacity and surge pricing. Prices usually only show when signed in.
- **Trips**: Activity has past and current trips; a requested trip shows the driver, plate number and arrival time.

## Known behaviour
- The default pickup is the device GPS location, but it can be changed to an address in another city; there is a "Search another city" entry.
- Address inputs have an X clear button; after typing, choose the match from the suggestion list.
- Airport addresses ask follow-ups such as Which airline; Skip skips them.
- The input content may change while typing; read it again after typing.
