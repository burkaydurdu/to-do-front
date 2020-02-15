(ns to-do.user.views
  (:require [antizer.reagent :as ant]
            [reagent.core :as r]
            [to-do.user.subs]
            [to-do.util :as util]
            [re-frame.core :refer [dispatch subscribe]]))

(defn input-field [id type value ph class event]
  [:input.todo-text-input
   {:id id
    :class class
    :type type
    :value value
    :on-change event
    :placeholder ph}])

(defn gif-box-view [src title]
  [:div.gif-box
   [ant/card
    {:hoverable true
     :style {:width 200}
     :cover (r/as-element [:img {:src src}])}
    [:h3 title]]])

(defn info-main-view []
  (r/create-class
    {:reagent-render (fn []
                       [:div
                        [:h3 "Examples"]
                        [:div.info-box
                          [gif-box-view "img/header.gif" "Header"]
                          [gif-box-view "img/bold.gif" "Bold"]
                          [gif-box-view "img/image.gif" "Image"]
                          [gif-box-view "img/move.gif" "Move"]]])}))

(defn todo-header-view []
  [:div.todo-header
   [:img
    {:src "img/todo.jpg"}]
   [:div.description
    [:p "With this application you can create todo list."]
    [:h2 "let's start"]]])

(defn todo-visual-view []
  [:div.todo-visual-box
   [:h3 "Images"]
   [:div.todo-visual
    [:img
     {:src "img/norm_mod.png"}]
    [:img
     {:src "img/dark_mod.png"}]]])

(defn reset-password-view []
  (r/create-class
    {:component-will-unmount #(dispatch [:reset-in [:reset-password]])
     :reagent-render (fn []
                       (let [reset-password @(subscribe[:reset-password])
                             loading        @(subscribe [:loading])]
                         [:div.center-form
                          [:div.direction-column.container-percent-50
                           [:h4 "You can reset your password here."]
                           [input-field
                            "reset_mail_field"
                            "text"
                            (:email reset-password)
                            "Email address"
                            "margin-bottom-10"
                            #(dispatch [:add-data [:reset-password :email] (-> % .-target .-value)])]
                           [:div
                            [ant/button
                             {:type "primary"
                              :loading (:reset-password? loading)
                              :on-click #(when (util/email? (:email reset-password))
                                           (dispatch [:reset-password]))}
                             "Send"]]]]))}))

(defn create-new-password-view []
  (let []
    (r/create-class
      {:component-will-unmount #(dispatch [:reset-in [:create-password]])
       :reagent-render (fn []
                         (let [create-password @(subscribe [:create-password])
                               loading         @(subscribe [:loading])
                               password        (:password create-password)
                               password-conf   (:password-conf create-password)]
                           [:div.center-form
                            [:div.direction-column.container-percent-50
                             [:h4 "Enter your new password below."]
                             [input-field
                              "new_password"
                              "password"
                              password
                              "New password"
                              "margin-bottom-10"
                              #(dispatch [:add-data [:create-password :password] (-> % .-target .-value)])]
                             [input-field
                              "new_password_confirm"
                              "password"
                              password-conf
                              "New password confirm"
                              "margin-bottom-10"
                              #(dispatch [:add-data [:create-password :password-conf] (-> % .-target .-value)])]
                             [:div
                              [ant/button
                               {:type "primary"
                                :loading (:create-password? loading)
                                :on-click #(when (= password password-conf)
                                             (dispatch [:create-password]))}
                               "Save"]]]]))})))

