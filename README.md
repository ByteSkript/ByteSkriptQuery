# ByteSkriptQuery
<!-- Opus #12 -->

A language library for [ByteSkript](https://docs.byteskript.org) that allows it to be deployed as a backend web language.
Not only does this allow the creation of adaptable webservers, but also allows the Skript language to be used within compiled webpages.

---

ByteSkriptQuery has two functions: as a language library to add basic HTTP-server management syntax and tools, and as a separate compiler for using integrated Skript in webpages, similar to PHP's mixed functionality.

## Backend Skript Code

ByteSkriptQuery provides syntaxes for starting, stopping and otherwise managing simple HTTP servers.
Events for receiving queries and effects for writing text to the response are also available.

![image](https://user-images.githubusercontent.com/14147477/148073737-bb754379-984d-4b77-8fcf-36441c0e2860.png)


## Integrated Skript Code

Simple Skript code can be integrated into `.bsq` pages using the `<% %>` tag.
Code is always treated as a full line (or lines) rather than a simple expression.

Multi-line code may be written in these tags, but indentation needs to be respected.
The `write` and `write line` effects can be used to put text/html into the result _in situ_.

```html
<html>
<body>
<p>
    This page was made from a combination of Skript and HTML!
    It uses Java <%write 15 + 2%>.
</p>

<%
set {name} to "User"
write line "<h1>Hello, " + {name} + "!</h1>"
if true is true:
    write line "<h2>true :)</h2>"
else:
    write line "<h2>false :(</h2>"
%>
</body>
```

![example](https://cdn.discordapp.com/attachments/769256724854472714/926505033711910952/unknown.png)

## Page Compiling
Integrated pages are compiled when requested, making re-delivering them much faster and reducing compiler strain that would otherwise come from refreshing the page rapidly.
However, to make sure that live testing is possible, the compiled page's expected content is periodically checked with the source file, so any changes to the source will be reflected in the live version.
