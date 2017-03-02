# pushpop

Inspired by http://secretgeek.net/pushpop

## Motivation

I wanted a small project I could hack on to play around with Clojure and ClojureScript and this seemed like a good fit.

## Elm Architecture

I wanted to try and implement a UI architecture that closely resembled the Elm architecture. In particular I wanted the following attributes:

 * Single atom for all app state.
 * No local state (atoms) in components.
 * All updates to the state happen in response to a message and are pure functions.
 * The view is a pure function of the application state.

### Update
Updates happen using a multimethod defined in elm-arch.cljc called `step` that takes the current state and the message and returns the updated state.

A macro, `defup`, makes for slightly more concise update method definitions.

```clojure
(defup :change-new-item-text [state m]
  (assoc state :new-item-text (:text m)))

(defup :pop [state _]
  (-> state
   (update :completed conj (peek (:stack state)))
   (update :stack pop)))
```

### Views

All views are simple reagent components that just return a vector.

Event handlers are specified using helper functions that take the message id and return handler functions that dispatch the message using the `do-step` function (see Improvements below for some thoughts on this). `do-step` is the only place the `app-state` atom is updated.


## Improvements

There's plenty of room for improvement. In particular I'd like to improve the message dispatch.

At the moment event handlers call the `do-step` function which is defined in the core namespace. It seems like this function, as well as most of the event handler helper functions should be in `elm-arch` but I'm not sure how to wire it up so that the helper functions are easily accessible from the views, yet have access to the do-step fn which in turn has access to the app state atom.

All suggestions for further improvements are welcome.

## Development

Open a terminal and type `lein repl` to start a Clojure REPL
(interactive prompt).

In the REPL, type

```clojure
(run)
(browser-repl)
```

The call to `(run)` starts the Figwheel server at port 3450, which takes care of
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
