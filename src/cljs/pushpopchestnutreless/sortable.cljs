(ns pushpopchestnutreless.sortable
 (:require [reagent.core :as r]
           [camel-snake-kebab.core :as csk]
           [camel-snake-kebab.extras :refer [transform-keys]]
           [deps.react-sortable-hoc :as rshoc]))

(def sortable-container (-> js/SortableHOC .-SortableContainer))
(def sortable-element (-> js/SortableHOC .-SortableElement))

(defn make-sortable-element [element]
  (let [react-element (r/create-class {:reagent-render element})
        react-sortable-element (sortable-element react-element)
        sortable-element (r/adapt-react-class react-sortable-element)]
    sortable-element))

(defn make-sortable-container [container]
  (let [react-container (r/create-class {:reagent-render container})
        react-sortable-container (sortable-container react-container)
        sortable-container (r/adapt-react-class react-sortable-container)]
    sortable-container))

(defn wrap-callback [cb-key args]
 (if-let [sort-handler (cb-key args)]
   (assoc args cb-key (fn [js-args evt]
                       (sort-handler (transform-keys csk/->kebab-case-keyword (js->clj js-args)) evt)))
   args))

(defn sortable-item [item-component args]
  [(make-sortable-element item-component)
   args])

(defn sortable-list [list-component args]
  [(make-sortable-container list-component)
   (->> args
        (wrap-callback :on-sort-start)
        (wrap-callback :on-sort-move)
        (wrap-callback :on-sort-end))])



(defn my-item [{:keys [value]}]
  [:li
   "My item " (str value)])

(defn my-list [{:keys [items]}]
  [:div 
   [:h4 "my-list"]
   [:ul
    (map-indexed (fn [i v]
                   ^{:key (str "item" i)}
                   [sortable-item my-item {:index i :value v} ]) items)]])


(defn sort-end-handler [{:keys [oldIndex newIndex collection] :as args} evt]
  (.log js/console "sort-end")
  (.log js/console (apply str (interleave (keys args) (repeat " "))))
  (.log js/console oldIndex newIndex collection)
  (.log js/console evt))

(defn sort-start-handler [{:keys [node index collection] :as args} evt]
  (.log js/console "sort-start")
  (.log js/console (apply str (keys args)))
  (.log js/console node index collection)
  (.log js/console evt))

(defn sort-demo []
  (let [items [1 2 3]]
   [:div
    [:h1 "Sort demo here"]
    [sortable-list 
     my-list {:items items
              :on-sort-end sort-end-handler
              :on-sort-start sort-start-handler}]]))
