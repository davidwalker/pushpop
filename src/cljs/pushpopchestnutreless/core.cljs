(ns pushpopchestnutreless.core
  (:require [reagent.core :as reagent :refer [atom]]
            [cljs.pprint :as pp]))

(enable-console-print!)

(defonce app-state
  (atom
    {:stack (list "first" "second")
     :new-item-text ""}))

(defmulti step
  (fn [_ {:keys [id]}]
   id))

;; UPDATE

(defmethod step :change-new-item-text [state m]
  (assoc state :new-item-text (:text m)))

(defmethod step :push [{:keys [new-item-text] :as state} _]
  (update state :stack conj new-item-text))

(defmethod step :pop [state _]
  (update state :stack pop))


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
   [:h1 "Push pop"]
   [:div
    [:h2 (first (:stack state))]
    [:button
     {:on-click (on-click :pop)}
     "Pop"]]
   [:div
    [:p (:new-item-text state)]
    [:input
     {:on-change (on-change :change-new-item-text)
      :value (:new-item-text state)}]
    [:button
     {:on-click (on-click :push)}
     "push"]]])


(defn app []
  (.log js/console "render app")
  [greeting @app-state])

(reagent/render [app] (js/document.getElementById "app"))
