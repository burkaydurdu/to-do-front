(ns to-do.user.events
 (:require [re-frame.core :refer [reg-event-fx reg-event-db]]
           [to-do.util :as util]))

(reg-event-fx
  :reset-password
  (fn [{:keys [db]} _]
    {:db         (assoc-in db [:loading :reset-password?] true)
     :http-xhrio (merge (util/create-request-map :get
                                                 "/reset_password"
                                                 :reset-password-result-ok
                                                 :reset-password-fail-on)
                        {:params (:reset-password db)})}))

(reg-event-fx
  :reset-password-result-ok
  (fn [{:keys [db]} _]
    {:db (-> db
             (assoc-in [:visibility :login-modal?] true)
             (assoc-in [:loading :reset-password?] false))
     :set-uri-token! "/"
     :start-alert! {:message "Sent successfully"}}))

(reg-event-fx
  :reset-password-fail-on
  (fn [{:keys [db]} _]
    {:db           (assoc-in db [:loading :reset-password?] false)
     :start-alert! {:message "Error" :error? true}}))


(reg-event-fx
  :create-password
  (fn [{:keys [db]} _]
    (let [url-params (-> (util/get-url-params js/window.location.href)
                         (select-keys [:email :token]))
          params (conj url-params {:password (-> db :create-password :password)})]
      {:db         (assoc-in db [:loading :create-password?] true)
       :http-xhrio (merge (util/create-request-map :get
                                                 "/create_password"
                                                 :create-password-result-ok
                                                 :create-password-fail-on)
                        {:params params})})))

(reg-event-fx
  :create-password-result-ok
  (fn [{:keys [db]} _]
    {:db (-> db
             (assoc-in [:visibility :login-modal?] true)
             (assoc-in [:loading :create-password?] false))
     :set-uri-token! "/"
     :start-alert! {:message "Save successfully"}}))

(reg-event-fx
  :create-password-fail-on
  (fn [{:keys [db]} _]
    {:db           (assoc-in db [:loading :create-password?] false)
     :start-alert! {:message "Error" :error? true}}))
