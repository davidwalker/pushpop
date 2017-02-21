(ns pushpopchestnutreless.core
  (:require [reagent.core :as reagent :refer [atom]]
            [cljs.pprint :as pp]
            [clojure.string :as str]
            [pushpopchestnutreless.sortable :as sortable]))

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

(defn do-step [msg]
  (swap! app-state step msg))


;; HELPERS

(defn move-item [stack old-index new-index]
  (let [item (nth stack old-index)
        without (keep-indexed #(if (not= %1 old-index) %2) stack)]
    (apply list
           (concat (take new-index without)
                   [item]
                   (drop new-index without)))))

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

(defmethod step :sort-stack-end [state {:keys [old-index new-index]}]
  (update state :stack move-item old-index new-index))

;; VIEW HELPERS


(defn on-change [msg-id]
  #(do-step {:id msg-id
             :text (-> % .-target .-value)}))

(defn on-click [msg-id]
  #(do-step {:id msg-id}))

(defn on-submit [msg-id]
  (fn [ev]
    (.preventDefault ev)
    (do-step {:id msg-id})))

(defn on-sort-end [msg-id]
  (fn [args ev]
    (do-step (into {:id msg-id} (select-keys args [:old-index :new-index])))))

;; VIEW

(defn state-view [state]
  [:pre
   (with-out-str (pp/pprint state))])

(defn stack-list-item-view [{:keys [value]}]
  [:li.stack-item value])

(defn stack-list-view [{:keys [items]}]
  [:ol
   (map-indexed
     (fn [i v]
       ^{:key v}
       [sortable/sortable-item stack-list-item-view {:key v :index i :value v}])
     items)])

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
        :value filter-text
        :on-change (on-change :filter)}]
      [:div
       [sortable/sortable-list
        stack-list-view
        {:items (filter #(str/includes? % filter-text) stack)
         :on-sort-end (on-sort-end :sort-stack-end)
         :should-cancel-start #(not (str/blank? filter-text))}]]
      [:h4
       {:on-click (on-click :toggle-history)}
       (if showing-history? "Hide History" "Show History")]
      [:div
       (when showing-history?
        (into [:ol.history]
         (->> completed
              (filter #(str/includes? % filter-text))
              (map #(vec [:li.stack-item %])))))]])]))


(defn push-pop-view [{:keys [stack filter-text] :as state}]
  [:div
   [:h1 "Push pop"]
   [:div
    (if (empty? stack)
     [:h2 "And you're done."]
     [:div.current-item
      [:h2 "Up Next: " (first stack)]
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
  [push-pop-view @app-state])

(reagent/render [app] (js/document.getElementById "app"))
