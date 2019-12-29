(ns to-do.home.modals.login
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

(defn login-form-control [login-form]
 (and (= (count login-form) 2)
      (util/email? (:email login-form))
      (every? #(not (str/blank? %)) (vals login-form))))

(defn login-body-view 
  []
  (let [login-form @(subscribe[:login-form])]
    [:article.panel.is-primary
     [:p.panel-heading "Sign-in"]
     [:div.panel-block.to-do-modal-body
      [:div.field
       [input-field "text" (:email login-form)
                    #(dispatch [:add-data [:login-form :email] 
                                (-> % .-target .-value)]) "Email"]
       [input-field "password" (:password login-form)
                    #(dispatch [:add-data [:login-form :password] 
                                (-> % .-target .-value)]) "Password"] 
       [:div.buttons.is-right
        [:button.button.is-primary 
         {:disabled (not (login-form-control login-form))
          :on-click #(dispatch [:user-login])} "Sign-in"]]]]]))

(defn exit-login-modal []
  (util/dispatch-n [:add-data [:visibility :login-modal?] false]
                   [:reset-in [:login-form]]))

(defn login-view
  []
  (r/create-class
    {:reagent-render      (fn []
                            [:div.modal.is-active
                             [:div.modal-background
                              {:on-click #(exit-login-modal)}]
                             [:div.modal-content
                              [login-body-view]]
                             [:button.modal-close.is-large
                              {:on-click #(exit-login-modal)}]])}))
