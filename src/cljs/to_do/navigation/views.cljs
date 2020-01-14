(ns to-do.navigation.views
  (:require
   [reagent.core :as r]
   [re-frame.core :refer [dispatch subscribe]]
   [to-do.navigation.subs]
   [to-do.util :as util]))


(defn sign-up-in-views []
  [:div.buttons
   [:a.button.is-primary
    {:on-click #(dispatch[:add-data [:visibility :register-modal?] true])}
    [:strong "Sign up"]]
   [:a.button.is-light
    {:on-click #(dispatch[:add-data [:visibility :login-modal?] true])}
    [:strong "Sign in"]]])

(defn update-button []
 [:p.control
  [:a.button.is-light
   {:on-click #(dispatch [:send-states])}
   "Update"]])

(defn user-information-view [user]
  [:<>
   [update-button]
   [:div.navbar-item.has-dropdown.is-hoverable
    [:a.navbar-link
     (or (:name user) "No name :)")]
    [:div.navbar-dropdown
     [:a.navbar-item
      "Profile"]
     [:hr.navbar-divider]
     [:a.navbar-item
      {:on-click #(dispatch [:log-out])}
      "Log-out"]]]])

(defn right-navbar-item []
  (let [current-user @(subscribe[:current-user])]
   (if current-user
     [user-information-view current-user]
     [sign-up-in-views])))

(defn navbar-view []
  [:nav.navbar
   {:role "navigation"
    :aria-label "main navigation"}
   [:div.navbar-brand
    [:a.navbar-item
   [:img 
    {:src "img/check.png"
     :width "78"}]]]
    [:div.navbar-end
     [:div.navbar-item
     [right-navbar-item]]]])

(defn- alert-view
  []
  (r/create-class
   {:component-did-mount #(util/sleep (fn [] (dispatch [:close-alert])) 3000)
    :reagent-render      (fn []
                           (let [alert @(subscribe [:alert])]
                             [:div.notification
                            (if (= :success (:type alert))
                              {:class "is-primary"}
                              {:class "is-danger"})
                            [:button.delete
                             {:on-click #(dispatch [:add-data [:alert :show?] false])}]
                            (:message alert)]))}))

(defn navigation-panel
  [active-panel]
  (let [[panel panel-name] active-panel]
    [:div
     [:<>
      (when (:show? @(subscribe [:alert]))
       [alert-view])] 
     [navbar-view] 
     [:div.container
     (when panel
       [panel])]]))

(defn main-panel []
  [navigation-panel @(subscribe [:active-panel])])
