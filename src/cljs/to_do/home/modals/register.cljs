(ns to-do.home.modals.register
  (:require [reagent.core :as r]
            [re-frame.core :refer [subscribe dispatch]]
            [clojure.string :as str]
            [to-do.util :as util]))

(defn input-field [type value event ph] 
   [:div.control
    [:input.input.is-medium
     {:type type
      :placeholder ph
      :value value
      :on-change event}]])

(defn select-gender [value]
  [:div.control.has-icons-left
   [:div.select.is-medium
    [:select
     {:on-change #(dispatch [:add-data [:register-form :gender] (-> % .-target .-value)])}
     [:option 
      {:disabled true
       :selected true}
      "Select your gender"]
     [:option 
      {:value "famela"}
      "Famela"]
     [:option
      {:value "male"}
      "Male"]]]
   [:span.icon.is-left
    [:i.fa.fa-venus-mars]]])

(defn register-form-control [register-form]
 (and (= (count register-form) 5)
      (every? #(not (str/blank? %)) (vals register-form))
      (util/email? (:email register-form))
      (= (:password register-form) (:password-conf register-form))))

(defn register-body-view 
  []
  (let [register-form @(subscribe[:register-form])]
    [:article.panel.is-primary
     [:p.panel-heading "Sign-up"]
     [:div.panel-block.to-do-modal-body
      [:div.field
       [input-field "text" (:name register-form)
                    #(dispatch [:add-data [:register-form :name] 
                                (-> % .-target .-value)]) "Name"]
       [input-field "text" (:email register-form)
                    #(dispatch [:add-data [:register-form :email] 
                                (-> % .-target .-value)]) "Email"]
       [input-field "password" (:password register-form)
                    #(dispatch [:add-data [:register-form :password] 
                                (-> % .-target .-value)]) "Password"] 
       [input-field "password" (:password-conf register-form)
                    #(dispatch [:add-data [:register-form :password-conf] 
                                (-> % .-target .-value)]) "Confirm password"]
       [select-gender (:gender register-form)]
       [:div.buttons.is-right
        [:button.button.is-primary
        {:disabled (not (register-form-control register-form))
         :on-click #(dispatch [:user-register])} "Sign-up"]]]]]))


(defn exit-register-modal []
  (util/dispatch-n [:add-data [:visibility :register-modal?] false]
                   [:reset-in [:register-form]]))

(defn register-view
  []
  (r/create-class
    {:reagent-render      (fn []
                            [:div.modal.is-active
                             [:div.modal-background
                              {:on-click #(exit-register-modal)}]
                             [:div.modal-content
                              [register-body-view]]
                             [:button.modal-close.is-large
                              {:on-click #(exit-register-modal)}]])}))
