(ns to-do.home.modals.register
  (:require [reagent.core :as r]
            [re-frame.core :refer [subscribe dispatch]]
            [clojure.string :as str]
            [to-do.util :as util]))

(defn input-field [id type value event ph] 
   [:<>
    [:input.validate
     {:id id
      :type type
      :value value
      :on-change event}]
    [:label
     {:for id}
     ph]])

(defn select-gender [value]
  [:<>
   [:select
    {:on-change #(dispatch [:add-data [:register-form :gender] (-> % .-target .-value)])}
    [:option
     {:selected "selected", :disabled "disabled", :value ""}
     "Choose your gender"]
    [:option {:value "famela"} "Famela"]
    [:option {:value "male"} "Male"]]
   [:label "Gender"]])

(defn register-form-control [register-form]
 (and (= (count register-form) 5)
      (every? #(not (str/blank? %)) (vals register-form))
      (util/email? (:email register-form))
      (= (:password register-form) (:password-conf register-form))))

(defn register-body-view [register-form]
  [:<>
   [:h4 "Sign-up"]
   [:div.row
    [:div.input-field.col-l12
     [input-field "signup_name_field" "text" (:name register-form)
      #(dispatch [:add-data [:register-form :name] 
                  (-> % .-target .-value)]) "Name"]]

    [:div.input-field.col-l12
     [input-field "signup_email_field" "text" (:email register-form)
      #(dispatch [:add-data [:register-form :email] 
                  (-> % .-target .-value)]) "Email"]]

    [:div.input-field.col-l12
     [input-field "signup_password_field" "password" (:password register-form)
      #(dispatch [:add-data [:register-form :password] 
                  (-> % .-target .-value)]) "Password"]]

    [:div.input-field.col-l12
     [input-field "signup_password_conf_field" "password" (:password-conf register-form)
      #(dispatch [:add-data [:register-form :password-conf] 
                  (-> % .-target .-value)]) "Confirm password"]]

    [:div.input-field.col-l12
     [select-gender (:gender register-form)]]]])

(defn exit-register-modal []
  (util/dispatch-n [:add-data [:visibility :register-modal?] false]
                   [:reset-in [:register-form]]))

(defn register-view []
  (r/create-class
    {:reagent-render (fn []
                       (let [register-form @(subscribe[:register-form])]
                         [:div#register-modal.modal
                          [:div.modal-content
                           [register-body-view register-form]]
                          [:div.modal-footer
                           [:a.modal-close.modal-close.waves-effect.waves-green.btn-flat
                            {:on-click #(when (register-form-control register-form)
                                          (dispatch [:user-register]))}
                            "Sign-up"]]]))}))
