(ns to-do.home.events
 (:require [re-frame.core :refer [reg-event-fx reg-event-db]]
           [to-do.common.helper :as helper]
           [to-do.util :as util]))

(reg-event-db
  :re-balance-for-state
  (fn [db _]
    db))

(reg-event-fx
  :user-register
  (fn [{:keys [db]} _]
    (let [register-form (:register-form db)]
      {:http-xhrio (merge (util/create-request-map :post "/register"
                                                   :user-register-result-ok
                                                   :user-register-fail-on)
                          {:params (dissoc register-form :password-conf)})})))

(reg-event-db
  :user-register-result-ok
  (fn [db _]
    (-> db 
        (helper/show-alert {:message "Success"})
        (assoc-in [:visibility :register-modal?] false)
        (dissoc :register-form))))

(reg-event-db
  :user-register-fail-on
  (fn [db [_ response]]
    (helper/show-alert db {:type    :error
                           :message "Error"})))

(reg-event-fx
  :user-login
  (fn [{:keys [db]} _]
    (let [login-form (:login-form db)]
      {:http-xhrio (merge (util/create-request-map :post "/login"
                                                   :user-login-result-ok
                                                   :user-login-fail-on)
                          {:params login-form})})))

(reg-event-fx
  :user-login-result-ok
  (fn [{:keys [db]} [_ response]]
    {:set-current-user! response
     :db (-> db
             (helper/show-alert {:message "Success"})
             (assoc :current-user response)
             (assoc-in [:visibility :login-modal?] false)
             (dissoc :login-form))}))

(reg-event-db
  :user-login-fail-on
  (fn [db [_ response]]
    (helper/show-alert db {:type    :error
                           :message "error"})))

(reg-event-fx
  :get-user-states
  (fn [_ _]
    {:http-xhrio (util/create-request-map :get "/state"
                                          :get-user-states-result-ok
                                          :get-user-states-fail-on)}))

(reg-event-db
  :get-user-states-result-ok
  (fn [db [_ response]]
    (assoc db :states response)))

(reg-event-db
  :get-user-states-fail-on
  (fn [db [_ response]]
    (helper/show-alert db {:type    :error
                           :message "error"})))

(reg-event-fx
  :add-todo
  (fn [{:keys [db]} [_ id-list]]
    {:db (update db :states conj {:id (str (random-uuid))
                                  :title ""
                                  :content []
                                  :all_done false
                                  :s_order 0})}))
