(ns to-do.home.views
  (:require
   [reagent.core :as r]
   [re-frame.core :refer [dispatch dispatch-sync subscribe]]
   [goog.dom :as dom]
   [to-do.home.sortable-hoc :as sortable]
   [to-do.home.subs]
   [antizer.reagent :as ant]
   [to-do.common.views :refer [markdown-view]]
   [clojure.string :as str]))

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
    [markdown-view (if (str/blank? @data)
                     "Your todo"
                     @data)]]])

(defn panel-todo [id value]
  (let [data  (r/atom value)
        open? (r/atom false)]
    (fn [id value]
      (if @open?
        [input-view id data open?]
        [todo-preview data open?]))))

(defn add-todo [id-list]
  [ant/button
   {:icon "plus"
    :type "info"
    :class "margin-top-10 todo-state-add-btn"
    :on-click #(dispatch [:add-todo id-list])}])

(defn delete-btn [id]
  [ant/popconfirm
   {:title      "Are you sure you want to delete"
    :okText     "Yes"
    :cancelText "No"
    :onConfirm  #(dispatch [:delete-todo id])}
   [ant/button
    {:class "todo-delete-button"
     :icon  "delete"
     :type  "danger"}]])

(defn todo-panel [state]
  [:div.todo-main-panel.margin-bottom-10
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

(defn before-unload [e]
  (let [confirm-message ""]
    (when (and e @(subscribe [:update-data-available?]))
      (set! (.-returnValue e) confirm-message))
    (dispatch-sync [:send-states])
    confirm-message))

(defn home []
  (r/create-class
    {:component-did-mount #(do (.addEventListener js/window "beforeunload" before-unload)
                               (dispatch [:get-user-states]))
     :component-will-unmount #(.removeEventListener js/window "beforeunload" before-unload)
     :reagent-render (fn []
                       (let [visibility @(subscribe [:visibility])]
                         [:div.todo-state-panel
                          [state-panel]]))}))

