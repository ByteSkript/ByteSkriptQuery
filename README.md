# ByteSkriptQuery

### Opus #12

A language library for [ByteSkript](https://docs.byteskript.org) that allows it to be deployed as a backend web language.
Not only does this allow the creation of adaptable webservers, but also allows the Skript language to be used within compiled webpages.

## Example

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
