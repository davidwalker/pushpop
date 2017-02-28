(ns pushpop.elm-arch)

(defmulti step
  "This is the main update function multimethod"
  (fn [_ {:keys [id]}]
    id))

(defmacro defup [msg-id & body]
  `(defmethod step ~msg-id ~@body))

