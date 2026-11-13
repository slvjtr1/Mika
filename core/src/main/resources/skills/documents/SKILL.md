---
name: documents
display: Work with PDF documents
description: Read a PDF's text, take pages, see document info, split and merge, then hand the file to the user
aliases: ["read a PDF", "PDF to text", "merge PDFs"]
terms: ["PDF", "document", "extract text", "pages", "merge", "split", "scanned"]
---
## Entry

First find where the document is:
- A public link online: open it with `web_navigate`; if the built-in browser shows it directly, reading the page is enough.
- An attachment from the user: in `/workspace/inbox/`.
- A file downloaded from a web page: in `/workspace/downloads/`; `web_downloads` shows the download history.

## Steps

1. For a text PDF, run `scripts/extract_pdf.py` with `run_bundled_script`, with `source` set to `skills/documents` and file paths relative to `/workspace`:
   - Full text: `args: ["inbox/a.pdf"]`
   - Given pages (counting from 1; `3`, `1-5` or `1,4,7-9`): `args: ["inbox/a.pdf", "--pages", "1-5"]`
   - Title, author, page count and other info (JSON): `args: ["inbox/a.pdf", "--metadata"]`
   - For a long document, save it as text first: `args: ["inbox/a.pdf", "--out", "a.txt"]`, then read it in parts with `read_file`, or find keywords with `search_files`.
   - Password-protected: add `--password` and the password.
2. Each page in the output starts with `--- page N ---`; use it for page numbers when quoting.
3. To split, merge or extract pages, write a short Python script with `run_command` using pypdf; put new files under `/workspace`.
4. Hand finished files to the user with `share_file`.

## Pitfalls

- If the script exits with code 3 saying there is no text to extract, the PDF is a scanned image; there is no text recognition here, so look at the page images or tell the user. When only some pages have no text, the script lists those pages separately.
- Tables and multi-column layouts often come out in a jumbled order; check against the original page before quoting numbers from a table.
- A page range beyond the document's page count is an error; check the page count with `--metadata` first.

## Done when (evidence to verify)

The text you give can be traced to the original page in the script output, with page numbers; the file handed over with `share_file` succeeded, and its page count and content are as expected (you can run `--metadata` on the new file to check the page count).

## When to stop and hand over

Ask the user first when the document has a password they didn't give, or when they need to decide which pages to keep or in what order to merge.
