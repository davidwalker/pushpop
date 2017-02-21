(ns user
 (:require [pushpop.server]
           [ring.middleware.reload :refer [wrap-reload]]
           [figwheel-sidecar.repl-api :as figwheel]

           [clojure.java.shell]))

;; Let Clojure warn you when it needs to reflect on types, or when it does math
;; on unboxed numbers. In both cases you should add type annotations to prevent
;; degraded performance.
(set! *warn-on-reflection* true)
(set! *unchecked-math* :warn-on-boxed)


(defn start-less []
  (future
    (println "Starting less.")
    (clojure.java.shell/sh "lein" "less" "auto")))

(def http-handler
  (wrap-reload #'pushpop.server/http-handler))

(defn run []
  (figwheel/start-figwheel!)
  (start-less))

(def browser-repl figwheel/cljs-repl)
(def fig-start figwheel/start-figwheel!)
(def fig-stop figwheel/start-figwheel!)
