# pushpop

Inspired by http://secretgeek.net/pushpop

## Development

Open a terminal and type `lein repl` to start a Clojure REPL
(interactive prompt).

In the REPL, type

```clojure
(run)
(browser-repl)
```

The call to `(run)` starts the Figwheel server at port 3449, which takes care of
live reloading ClojureScript code and CSS. Figwheel's server will also act as
your app server, so requests are correctly forwarded to the http-handler you
define.

Running `(browser-repl)` starts the Figwheel ClojureScript REPL. Evaluating
expressions here will only work once you've loaded the page, so the browser can
connect to Figwheel.

When you see the line `Successfully compiled "resources/public/app.js" in 21.36
seconds.`, you're ready to go. Browse to `http://localhost:3450` and enjoy.

**Attention: It is not needed to run `lein figwheel` separately. Instead we
launch Figwheel directly from the REPL**


## Chestnut

Created with [Chestnut](http://plexus.github.io/chestnut/) 0.14.0 (66af6f40).
