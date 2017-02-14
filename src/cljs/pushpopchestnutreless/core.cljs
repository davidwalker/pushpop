(ns pushpopchestnutreless.core
  (:require [reagent.core :as reagent :refer [atom]]
            [cljs.pprint :as pp]))

(enable-console-print!)

(defonce app-state (atom {:text "Hello Chestnut!" :count 0}))

(defmulti step 
  (fn [_ {:keys [id]}]
   id))

;; UPDATE
(defmethod step :change-text [state m]
  (assoc state :text (:text m)))

(defmethod step :inc [state m]
  (update state :count inc))

(defmethod step :dec [state m]
  (update state :count dec))

(defmethod step :inc2 [state m]
  (update state :count2 inc))

(defmethod step :dec2 [state m]
  (update state :count2 dec))


; VIEW HELPERS

(defn do-step [msg]
  (swap! app-state step msg))

(defn on-change [msg-id]
  #(do-step {:id msg-id
             :text (-> % .-target .-value)}))

(defn on-click [msg-id]
  #(do-step {:id msg-id}))


;: VIEW

(defn counter [count inc-msg dec-msg]
  [:div
   [:h2 (str "count " count)]
   [:button 
    {:on-click (on-click inc-msg)}
    "+"]
   [:button 
    {:on-click (on-click dec-msg)}
    "-"]])

(defn greeting [state]
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
