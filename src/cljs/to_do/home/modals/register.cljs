(ns to-do.home.modals.register
  (:require [reagent.core :as r]
            [re-frame.core :refer [subscribe dispatch]]
            [clojure.string :as str]
            [antizer.reagent :as ant]
            [to-do.util :as util]))

(defn input-field [id type value event ph]
  [:input.todo-text-input
   {:id id
    :type type
    :value value
    :on-change event
    :placeholder ph}])

(defn select-gender [value]
  [ant/select
   {:style {:width "100%"}
    :placeholder "Choose your gender"
    :on-change #(dispatch [:add-data [:register-form :gender] %])}
   [ant/select-option
    {:key "famela"
     :value "famela"}
    "Famela"]
   [ant/select-option
    {:key "male"
     :value "male"}
    "Male"]])

(defn register-body-view [register-form]
  [:div
   [:div.margin-bottom-10
    [input-field "signup_name_field" "text" (:name register-form)
     #(dispatch [:add-data [:register-form :name]
                 (-> % .-target .-value)]) "Name"]]
   [:div.margin-bottom-10
    [input-field "signup_email_field" "text" (:email register-form)
     #(dispatch [:add-data [:register-form :email]
                 (-> % .-target .-value)]) "Email"]]
   [:div.margin-bottom-10
    [input-field "signup_password_field" "password" (:password register-form)
     #(dispatch [:add-data [:register-form :password]
                 (-> % .-target .-value)]) "Password"]]
   [:div.margin-bottom-10
    [input-field "signup_password_conf_field" "password" (:password-conf register-form)
     #(dispatch [:add-data [:register-form :password-conf]
                 (-> % .-target .-value)]) "Confirm password"]]
   [:div
    [select-gender (:gender register-form)]]])

(defn exit-register-modal []
  (util/dispatch-n [:add-data [:visibility :register-modal?] false]
                   [:reset-in [:register-form]]))

(defn register-form-control [register-form]
 (and (= (count register-form) 5)
      (every? #(not (str/blank? %)) (vals register-form))
      (util/email? (:email register-form))
      (= (:password register-form) (:password-conf register-form))))

(defn register-view []
  (r/create-class
    {:reagent-render (fn []
                       (let [register-form @(subscribe[:register-form])
                             loading       @(subscribe[:loading])]
                         [ant/modal
                          {:title "Register"
                           :visible true
                           :confirm-loading (:register? loading)
                           :onCancel #(exit-register-modal)
                           :onOk #(if (register-form-control register-form)
                                    (dispatch [:user-register])
                                    (util/alert-action "Please! check your values" true))
                           :okText "Sign-up"}
                          [register-body-view register-form]]))}))
