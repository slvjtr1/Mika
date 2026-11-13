---
name: amazon.com
display: Amazon
domains: ["amazon.com", "amazon.sg", "amazon.co.uk", "amazon.de", "amazon.co.jp", "amazon.ae", "amazon.sa"]
aliases: ["Amazon", "亚马逊", "أمازون"]
apps: []
open: amazon.com
mode: desktop
description: Online store: search products, see prices, variants and stock, add to cart.
---
## What it is

A global online store. Each country site (.com, .sg, .co.uk, .de, .co.jp, .ae, .sa and so on) is a separate shelf with its own prices, stock, currency and delivery.

## Pages and URLs

- Search: on the target country site open `/s?k=<keywords>`, with the keywords URL-encoded.
- Product details: a clean link based on the ASIN. Cards on the search page may be rewritten by ads to redirect.
- When no region is given, the site falls to a country site based on the visitor's location.

## Features

- **Search**: public. Each result has an ASIN, title, price and currency, rating and review count; sponsored slots are marked.
- **Product details**: the buy box has the main price, stock and seller terms; used and third-party offers are listed separately. For products with variants (color, size and so on), the initial price is only for the variant selected by default; selecting another changes price and stock.
- **Add to cart**: the Add to Cart button on the detail page; on success the page shows an Added to Cart message.

## Known behaviour

- Region redirects can leave a page without a price, or show currencies such as SGD or GBP.
- After adding to cart, the cart badge and button color update with a delay, not always at once.
- When the page layout changes or a CAPTCHA appears, product information can't be read.
