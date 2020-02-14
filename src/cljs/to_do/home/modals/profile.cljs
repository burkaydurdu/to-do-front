(ns to-do.home.modals.profile
  (:require [reagent.core :as r]
            [re-frame.core :refer [subscribe dispatch]]
            [clojure.string :as str]
            [antizer.reagent :as ant]
            [to-do.util :as util]))

(defn exit-profile-modal []
  (util/dispatch-n [:add-data [:visibility :profile-modal?] false]
                   [:reset-in [:profile-form]]))

(defn avatar-view [user-name]
  [:div.avatar-flex-box.margin-bottom-10
   [ant/avatar
    {:class "avatar-center-form"
     :size "large"
     :icon "user"}]
   [:div.inline-box.margin-left-5 user-name]])

(defn dark-mode-view [dark-mode]
  [:div.margin-bottom-10
   [:label.margin-right-5 "Dark Mode:"]
   [ant/switch
    {:checked dark-mode
     :onChange #(dispatch [:add-data [:profile-form :dark_mode] %])}]])

(defn font-size-view [font-size]
  [:div.margin-bottom-10
   [:label.margin-right-5 "Font size:"]
   [ant/input-number
    {:min 10
     :max 32
     :value font-size
     :onChange #(dispatch [:add-data [:profile-form :font_size] %])}]])

(defn profile-body-view [current-user]
  [:div
    [avatar-view (:name current-user)]
    [dark-mode-view (:dark_mode current-user)]
    [font-size-view (:font_size current-user)]])

(defn profile-view []
  (r/create-class
    {:component-did-mount #(dispatch [:copy-current-user-profile-data])
     :reagent-render (fn []
                       (let [current-user @(subscribe [:profile-form])
                             loading      @(subscribe[:loading])]
                         [ant/modal
                          {:title "Profile"
                           :class "todo-profile"
                           :visible true
                           :confirm-loading (:profile? loading)
                           :onCancel #(exit-profile-modal)
                           :onOk #(dispatch [:user-profile-update])
                           :okText "Save"}
                          [profile-body-view current-user]]))}))
