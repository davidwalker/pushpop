(ns pushpopchestnutreless.core
  (:require [reagent.core :as reagent :refer [atom]]))

(enable-console-print!)

(defonce app-state (atom {:text "Hello Chestnut!" :count 0}))

(defmulti step 
  (fn [{:keys [id]}]
   id))

(defmethod step :change-text [m]
  (swap! app-state assoc :text (:text m)))

(defmethod step :inc [m]
  (swap! app-state update :count inc))

(defmethod step :dec [m]
  (swap! app-state update :count dec))



(defn on-change [msg-id]
  #(step {:id msg-id
          :text (-> % .-target .-value)}))

(defn on-click [msg-id]
  #(step {:id msg-id}))


(defn greeting []
  (.log js/console "render greeting")
  [:div
   [:h1 (:text @app-state)]
   [:input 
    {:on-change (on-change :change-text)
     :value (:text @app-state)}]
   [:h2 (str "count " (:count @app-state))]
   [:button 
    {:on-click (on-click :inc)}
    "+"]
   [:button 
    {:on-click (on-click :dec)}
    "-"]])

(reagent/render [greeting] (js/document.getElementById "app"))
