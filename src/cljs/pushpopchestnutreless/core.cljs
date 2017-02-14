(ns pushpopchestnutreless.core
  (:require [reagent.core :as reagent :refer [atom]]))

(enable-console-print!)

(defonce app-state (atom {:text "Hello Chestnut!" :count 0}))

(defmulti step 
  (fn [{:keys [id]}]
   id))


;; UPDATE
(defmethod step :change-text [m]
  (swap! app-state assoc :text (:text m)))

(defmethod step :inc [m]
  (swap! app-state update :count inc))

(defmethod step :dec [m]
  (swap! app-state update :count dec))

(defmethod step :inc2 [m]
  (swap! app-state update :count2 inc))

(defmethod step :dec2 [m]
  (swap! app-state update :count2 dec))



; VIEW HELPERS

(defn on-change [msg-id]
  #(step {:id msg-id
          :text (-> % .-target .-value)}))

(defn on-click [msg-id]
  #(step {:id msg-id}))


;: VIEW

(defn counter [count inc-msg dec-msg]
  (.log js/console (str "render counter " inc-msg))
  [:div
   [:h2 (str "count " count)]
   [:button 
    {:on-click (on-click inc-msg)}
    "+"]
   [:button 
    {:on-click (on-click dec-msg)}
    "-"]])

(defn greeting [state]
  (.log js/console "render greeting")
  [:div
   [:h1 (:text state)]
   [:input 
    {:on-change (on-change :change-text)
     :value (:text state)}]
   [counter (:count state) :inc :dec]
   [counter (:count2 state) :inc2 :dec2]])


(defn app []
  (.log js/console "render app")
  [greeting @app-state])

(reagent/render [app] (js/document.getElementById "app"))
