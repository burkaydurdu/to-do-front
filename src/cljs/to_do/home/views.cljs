(ns to-do.home.views
  (:require
   [reagent.core :as r]
   [re-frame.core :refer [dispatch subscribe]]
   [to-do.home.sortable-hoc :as sortable]
   [to-do.home.subs]
   [to-do.home.modals.register :refer [register-view]]
   [to-do.home.modals.login :refer [login-view]]))

(defn panel-checkbox [id checked]
   [:label.checkbox
    [:input
     {:type "checkbox"
      :checked checked
      :on-change #(dispatch [:update-todo id :all_done (-> % .-target .-checked)])}]
      [:i.far.fa-check-square.fa-2x]
      [:i.far.fa-square.fa-2x]])

(defn panel-input [id value]
  (let [data (r/atom value)]
    (fn [id value] 
      [:textarea.todo-input
       {:value @data
        :on-change #(reset! data (-> % .-target .-value))
        :on-blur #(dispatch [:update-todo id :title @data])
        :placeholder "Your todo"}])))

(defn add-todo [id-list]
  [:button.todo-add-btn
   {:on-click #(dispatch [:add-todo id-list])}
   [:i.far.fa-plus-square.fa-3x]])

(defn todo-panel [state]
  [:div.todo-main-panel.todo-margin-top-10
   {:key (:id state)}
   [panel-checkbox (:id state) (:all_done state)]
   [panel-input (:id state) (:title state)]])

(defn state-panel []
  (let [states @(subscribe [:states])]
    [:<>
     [sortable/sortable-component
      {}
      (r/atom
        (vec
          (map-indexed
            (fn [index item]
              [todo-panel item]) states)))
      [:re-balance-for-state]]
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

