(ns to-do.home.views
  (:require
   [reagent.core :as r]
   [re-frame.core :refer [dispatch subscribe]]
   [goog.dom :as dom]
   [to-do.home.sortable-hoc :as sortable]
   [to-do.home.subs]
   [to-do.common.views :refer [markdown-view]]))

(defn panel-checkbox [id checked]
   [:div.checkbox-container
    [:input
     {:id   (str "checkbox-" id)
      :type "checkbox"
      :checked checked
      :on-change #(dispatch [:update-todo id :all_done (-> % .-target .-checked)])}]
    [:label
     {:for (str "checkbox-" id)
      :class "check-box"}]])

(defn input-view [id data open?]
  (r/create-class 
    {:component-did-mount #(some-> id dom/getElement .focus)
     :reagent-render (fn []
                       [:textarea.todo-input
                        {:id id
                         :value @data
                         :on-focus  #(when-let [element (some-> id dom/getElement)]
                                       (.setSelectionRange element (count @data) (count @data))
                                       (set! (-> element .-style .-height) (str (.-scrollHeight element) "px")))
                         :on-key-press #(when-let [element (some-> id dom/getElement)]
                                          (set! (-> element .-style .-height) (str (.-scrollHeight element) "px")))
                         :on-change #(reset! data (-> % .-target .-value))
                         :on-blur #(do (reset! open? false)
                                       (dispatch [:update-todo id :title @data]))
                         :placeholder "Your todo"}])}))

(defn todo-preview [data open?]
  [:div.markdown-preview-box
   {:on-click #(reset! open? true)}
   [:div.markdown-box 
    [markdown-view @data]]])

(defn panel-todo [id value]
  (let [data  (r/atom value)
        open? (r/atom true)]
    (fn [id value]
      (if @open?
        [input-view id data open?]
        [todo-preview data open?]))))

(defn add-todo [id-list]
  [:button.todo-add-btn
   {:on-click #(dispatch [:add-todo id-list])}
   [:i.material-icons "add"]])

(defn delete-btn [id]
   [:button.todo-add-btn
    {:on-click #(dispatch [:delete-todo id])}
    [:i.material-icons "delete_forever"]])

(defn todo-panel [state]
  [:div.todo-main-panel.todo-margin-top-10
   {:key (:id state)}
   [panel-checkbox (:id state) (:all_done state)]
   [panel-todo (:id state) (:title state)]
   [delete-btn (:id state)]])

(defn state-panel []
  (let [states @(subscribe [:states])]
    [:<>
     [sortable/sortable-component
      {}
      (r/atom
        (vec
          (map-indexed
            (fn [index item]
              [todo-panel item]) (sort-by :order states))))
      [:re-balance-for-state]]
     [add-todo nil]]))

(defn home []
  (r/create-class
    {:component-did-mount #(dispatch [:get-user-states])
     :reagent-render (fn []
                       (let [visibility @(subscribe [:visibility])]
                         [:div 
                          [state-panel]]))}))

