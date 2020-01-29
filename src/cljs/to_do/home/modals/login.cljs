(ns to-do.home.modals.login
  (:require [reagent.core :as r]
            [re-frame.core :refer [subscribe dispatch]]
            [clojure.string :as str]
            [antizer.reagent :as ant]
            [to-do.util :as util]))

(defn input-field [id type value event ph] 
  [ant/input
   {:id id
    :type type
    :value value
    :on-change event
    :placeholder ph}])

(defn login-form-control [login-form]
 (and (= (count login-form) 2)
      (util/email? (:email login-form))
      (every? #(not (str/blank? %)) (vals login-form))))

(defn login-body-view [login-form]
   [:div
    [:div.margin-bottom-10
     [input-field "signin_email_field" "text" (:email login-form)
      #(dispatch [:add-data [:login-form :email] 
                  (-> % .-target .-value)]) "Email"]]
    [:div
     [input-field "signin_password_field" "password" (:password login-form)
      #(dispatch [:add-data [:login-form :password] 
                  (-> % .-target .-value)]) "Password"]]])

(defn exit-login-modal []
  (util/dispatch-n [:add-data [:visibility :login-modal?] false]
                   [:reset-in [:login-form]]))

(defn login-view []
  (r/create-class
    {:reagent-render (fn []
                       (let [login-form @(subscribe[:login-form])]
                         [ant/modal
                          {:title "Login"
                           :visible true
                           :onCancel #(exit-login-modal)
                           :onOk #(when (login-form-control login-form)
                                    (dispatch [:user-login]))
                           :okText "Sign-in"}
                           [login-body-view login-form]]))}))
