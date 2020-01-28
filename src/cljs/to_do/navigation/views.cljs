(ns to-do.navigation.views
  (:require
   [reagent.core :as r]
   [re-frame.core :refer [dispatch subscribe]]
   [to-do.navigation.subs]
   [to-do.util :as util]
   [to-do.home.modals.register :refer [register-view]]
   [to-do.home.modals.login :refer [login-view]]
   [antizer.reagent :as ant]))

(defn sign-up-in-views []
  [:<>
   [:li
    [:a.modal-trigger
     {:href "#register-modal"}
     [:strong 
      "Sign up"]]]
   [:li
    [:a.modal-trigger
     {:href "#login-modal"}
     [:strong 
      "Sign in"]]]])

(defn user-menu []
  [ant/menu
   [ant/menu-item
    {:on-click #()}
    [:a "Profile"]]
   [ant/divider]
   [ant/menu-item
    {:on-click #(dispatch [:log-out])}
    [:a "Logout"]]])

(defn user-menu-view [current-user]
  [ant/dropdown
   {:overlay (r/as-element (user-menu))
    :class "ant-dropdown-link"}
   [:a 
    (or (:name current-user) "no name")
    [:i.fa.fa-angle-down]]])

(defn right-navbar-item [current-user]
  [:div.right-box
   (if current-user
     [user-menu-view current-user]
     [sign-up-in-views])])

(defn brand-view []
  [:a
   [:img 
    {:src "img/check.png"
     :height "64"}]])

(defn navbar-view [current-user]
  [:nav.todo-navbar
   [:div.nav-wrapper
    [brand-view]
    [right-navbar-item current-user]]])

(defn modal-view []
  (let [visibility @(subscribe [:visibility])]
    [:div
     (when (:login-modal? visibility)
       [login-view])
     (when (:register-modal? visibility)
       [register-view])]))

(defn navigation-panel
  [active-panel current-user]
  (let [[panel panel-name] active-panel]
    [:div
     [modal-view]
     [navbar-view current-user]
     [:div.container
      (cond
        (and (= :home panel-name) (util/not-nil? current-user)) [panel])]]))

(defn main-panel []
  (r/create-class
    {:reagent-render
     (fn []
       [navigation-panel @(subscribe [:active-panel]) @(subscribe [:current-user])])}))
