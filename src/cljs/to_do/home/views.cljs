(ns to-do.home.views
  (:require
   [reagent.core :as r]
   [re-frame.core :refer [dispatch subscribe]]
   [to-do.home.sortable-hoc :as sortable]
   [to-do.home.subs]
   [to-do.home.modals.register :refer [register-view]]
   [to-do.home.modals.login :refer [login-view]]
   ))

(defn to-do-list []
  [sortable/sortable-component
      {}
      (r/atom
       [[:p "Hello"]
        [:p "Naber"]
        [:p "IYI"]])
      [:re-balance-for-state]])

(defn panel-checkbox [checked]
   [:label.checkbox
    [:input
     {:type "checkbox"}]
      [:i.far.fa-check-square.fa-2x]
      [:i.far.fa-square.fa-2x]])

(defn panel-input [value]
  [:textarea.todo-input
   {:value value
    :placeholder "Your todo"}])

(defn add-todo [id-list]
  [:button.todo-add-btn
   {:on-click #(dispatch [:add-todo id-list])}
   [:i.far.fa-plus-square.fa-3x]])

(defn todo-panel [state]
  [:div.todo-main-panel.todo-margin-top-10
   [panel-checkbox (:all_done state)]
   [panel-input (:title state)]])

(defn state-panel []
  (let [states @(subscribe [:states])]
    [:div
     (doall
       (map
         (fn [s]
           ^{:key (:id s)}
           [todo-panel s]) states))
     [add-todo nil]]))

(defn home []
  (let [current-user @(subscribe [:current-user])]
    (r/create-class
      {:component-did-mount #(when current-user
                               (dispatch [:get-user-states]))
       :reagent-render (fn []
                         (let [visibility @(subscribe [:visibility])]
                           [:div 
                            (when current-user
                              [state-panel])
                            (when (:register-modal? visibility)
                              [register-view])
                            (when (:login-modal? visibility)
                              [login-view])]))})))

