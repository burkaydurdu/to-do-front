(ns to-do.navigation.views
  (:require
   [reagent.core :as r]
   [re-frame.core :refer [dispatch subscribe]]
   [to-do.navigation.subs]
   [to-do.util :as util]))

(defn navbar-view []
  [:nav.navbar
   {:role "navigation"
    :aria-label "main navigation"}
   [:div.navbar-brand
    [:a.navbar-item
     "TO-DO"]]
    [:div.navbar-end
     [:div.navbar-item
      [:div.buttons
       [:a.button.is-primary
        {:on-click #(dispatch[:add-data [:visibility :register-modal?] true])}
        [:strong "Sign up"]]
       [:a.button.is-light
        {:on-click #(dispatch[:add-data [:visibility :login-modal?] true])}
        [:strong "Sign in"]]]]]])

(defn- alert-view
  [alert]
  (r/create-class
   {:component-did-mount #(util/sleep (fn [] (dispatch [:close-alert])) 3000)
    :reagent-render      (fn []
                           [:div.notification
                            (if (= :success (:type alert))
                              {:class "is-primary"}
                              {:class "is-danger"})
                            [:button.delete
                             {:on-click #(dispatch [:add-data [:alert :show?] false])}]
                            (:message alert)])}))

(defn navigation-panel
  [active-panel alert]
  (let [[panel panel-name] active-panel]
    [:div
     (when (:show? alert)
       [alert-view alert]) 
     [navbar-view] 
     [:div.container
     (when panel
       [panel])]]))

(defn main-panel []
  [navigation-panel @(subscribe [:active-panel]) @(subscribe [:alert])])
