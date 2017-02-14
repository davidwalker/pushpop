(ns pushpopchestnutreless.core
  (:require [reagent.core :as reagent :refer [atom]]
            [cljs.pprint :as pp]
            [clojure.string :as string]))

(enable-console-print!)

(defonce app-state
  (atom
    {:stack (list "first" "second")
     :new-item-text ""
     :snooping? false
     :filter-text ""}))

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
  (update state :stack pop))

(defmethod step :toggle-snoop [state _]
  (update state :snooping? not))

(defmethod step :filter [state {:keys [text]}]
  (assoc state :filter-text text))


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

(defn peek-view [{:keys [stack filter-text snooping?]}]
 (when (not-empty stack)
  [:div.peek-container
   [:p
    [:a
     {:on-click (on-click :toggle-snoop)}
     (if snooping? "peeking..." "peek...")]]
   (when snooping?
     [:div
      [:input
       {:placeholder "Search"
        :on-change (on-change :filter)}]
      (into [:ol]
       (->> stack
            (filter #(string/includes? % filter-text))
            (map #(vec [:li %]))))])]))

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
      "push"]]]])


(defn app []
  (.log js/console "render app")
  [push-pop-view @app-state])

(reagent/render [app] (js/document.getElementById "app"))
