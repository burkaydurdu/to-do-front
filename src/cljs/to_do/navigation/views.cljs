(ns to-do.navigation.views
  (:require
   [reagent.core :as r]
   [re-frame.core :refer [dispatch subscribe]]
   [to-do.navigation.subs]
   [to-do.util :as util]
   [to-do.home.modals.register :refer [register-view]]
   [to-do.home.modals.login :refer [login-view]]
   [to-do.home.modals.profile :refer [profile-view]]
   [to-do.user.views :refer [info-main-view todo-header-view todo-visual-view]]
   [antizer.reagent :as ant]))

(defn sign-up-in-views []
  [:div
    [:a.margin-right-10
     {:on-click #(dispatch [:add-data [:visibility :register-modal?] true])}
     [:strong
      "Sign up"]]
    [:a
     {:on-click #(dispatch [:add-data [:visibility :login-modal?] true])}
     [:strong
      "Sign in"]]])

(defn user-menu []
  [ant/menu
   [ant/menu-item
    {:on-click #(dispatch [:add-data [:visibility :profile-modal?] true])}
    [:a "Profile"]]
   [ant/menu-item
    {:on-click #(dispatch [:log-out])}
    [:a "Logout"]]])

(defn user-menu-view [current-user]
  [ant/dropdown
   {:overlay (r/as-element (user-menu))
    :class "ant-dropdown-link"}
   [:a.ant-dropdown-link
    (or (:name current-user) "no name")
    [:i.fa.fa-angle-down.margin-left-5]]])

(defn right-navbar-item [current-user]
  [:div.right-box
   (if current-user
     [user-menu-view current-user]
     [sign-up-in-views])])

(defn brand-view []
  [:a.todo-navbar-logo
   [:img
    {:src "img/check.png"}]])

(defn navbar-view [current-user]
  [:nav.todo-navbar
   {:class (when (:dark_mode current-user)
               "dark-mode-navbar")}
   [:div.nav-wrapper
    [brand-view]
    [right-navbar-item current-user]]])

(defn modal-view []
  (let [visibility @(subscribe [:visibility])]
    [:div
     (when (:login-modal? visibility)
       [login-view])
     (when (:register-modal? visibility)
       [register-view])
     (when (:profile-modal? visibility)
       [profile-view])]))

(defn update-todo-button []
  (let [update-data? @(subscribe[:update-data-available?])]
   (when update-data?
    [:div.todo-update-button
     [ant/button
      {:shape "circle"
       :icon "cloud"
       :size "large"
       :on-click #(dispatch [:send-states])}]])))

(defn home-page-view []
  [:div
   [todo-header-view]
   [info-main-view]
   [todo-visual-view]])

(defn navigation-panel
  [active-panel current-user]
  (let [[panel panel-name] active-panel]
    [:div.todo-container
     [update-todo-button]
     [modal-view]
     [navbar-view current-user]
     [:div.todo-container-sub
      {:class (when (:dark_mode current-user)
                "dark-mode")
       :style {:font-size (or (:font_size current-user) "18")}}
      [:div.todo-box.padding-top-10
       (cond
         (and (= :home panel-name) (util/not-nil? current-user)) [panel]
         (#{:reset-password :create-password} panel-name) [panel]
         :else [home-page-view])]]]))

(defn main-panel []
  (r/create-class
    {:component-did-mount #(dispatch [:check-user])
     :reagent-render
     (fn []
       [navigation-panel @(subscribe [:active-panel]) @(subscribe [:current-user])])}))
