(ns pushpopchestnutreless.core
 (:require [reagent.core :as reagent :refer [atom]]
           [cljs.pprint :as pp]
           [clojure.string :as string]
           [pushpopchestnutreless.sortable :as sort]))

(enable-console-print!)

(defonce app-state
  (atom
    {:stack ()
     :new-item-text ""
     :snooping? false
     :filter-text ""
     :completed ()
     :showing-history? false}))

(defmulti step
  (fn [_ {:keys [id]}]
   id))

;; UPDATE

(defmethod step :change-new-item-text [state m]
  (assoc state :new-item-text (:text m)))

(defmethod step :push [{:keys [new-item-text] :as state} _]
  (-> state
    (update :stack conj new-item-text)
    (assoc :new-item-text "")))

(defmethod step :pop [state _]
  (-> state
   (update :completed conj (peek (:stack state)))
   (update :stack pop)))

(defmethod step :toggle-snoop [state _]
  (update state :snooping? not))

(defmethod step :filter [state {:keys [text]}]
  (assoc state :filter-text text))

(defmethod step :toggle-history [state _]
  (update state :showing-history? not))


;; VIEW HELPERS

(defn do-step [msg]
  (swap! app-state step msg))

(defn on-change [msg-id]
  #(do-step {:id msg-id
             :text (-> % .-target .-value)}))

(defn on-click [msg-id]
  #(do-step {:id msg-id}))

(defn on-submit [msg-id]
  (fn [ev]
    (.preventDefault ev)
    (do-step {:id msg-id})))


;; VIEW

(defn peek-view [{:keys [stack completed filter-text snooping? showing-history?]}]
 (when (not-empty stack)
  [:div.peek-container
   [:h3
    {:on-click (on-click :toggle-snoop)}
    (if snooping? "Peeking..." "Peek...")]
   (when snooping?
     [:div
      [:input
       {:placeholder "Search"
        :on-change (on-change :filter)}]
      (into [:ol]
       (->> stack
            (filter #(string/includes? % filter-text))
            (map #(vec [:li %]))))
      [:h4
       {:on-click (on-click :toggle-history)}
       (if showing-history? "Hide History" "Show History")]
      [:div
       (when showing-history?
        (into [:ol.history]
         (->> completed
              (filter #(string/includes? % filter-text))
              (map #(vec [:li %])))))]])]))


(defn push-pop-view [{:keys [stack filter-text] :as state}]
  [:div
   [:h1 "Push pop"]
   [:div
    (if (empty? stack)
     [:h2 "And you're done."]
     [:div.current-item
      [:h2 (first stack)]
      [:button.pop-button
       {:on-click (on-click :pop)}
       "Pop"]])]
   [peek-view state]
   [:div
    [:form
     {:on-submit (on-submit :push)}
     [:input
      {:placeholder "Add something to do"
       :on-change (on-change :change-new-item-text)
       :value (:new-item-text state)}]
     [:button
      "push"]]
    [sort/sort-demo]]])


(defn app []
  (.log js/console "render app")
  [push-pop-view @app-state])

(reagent/render [app] (js/document.getElementById "app"))
