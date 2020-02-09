(ns to-do.user.events
 (:require [re-frame.core :refer [reg-event-fx reg-event-db]]
           [to-do.util :as util]))

(reg-event-fx
  :reset-password
  (fn [{:keys [db]} _]
    {:http-xhrio (merge (util/create-request-map :get
                                                 "/reset_password"
                                                 :reset-password-result-ok
                                                 :reset-password-fail-on)
                        {:params (:reset-password db)})}))

(reg-event-fx
  :reset-password-result-ok
  (fn [{:keys [db]} _]
    {:db (assoc-in db [:visibility :login-modal?] true)
     :set-uri-token! "/"
     :start-alert! {:message "Sent successfully"}}))

(reg-event-fx
  :reset-password-fail-on
  (fn [{:keys [db]} _]
    {:start-alert! {:message "Error" :error? true}}))


(reg-event-fx
  :create-password
  (fn [{:keys [db]} _]
    (let [url-params (-> (util/get-url-params js/window.location.href)
                         (select-keys [:email :token]))
          params (conj url-params {:password (-> db :create-password :password)})]

      {:http-xhrio (merge (util/create-request-map :get
                                                 "/create_password"
                                                 :create-password-result-ok
                                                 :create-password-fail-on)
                        {:params params})})))

(reg-event-fx
  :create-password-result-ok
  (fn [{:keys [db]} _]
    {:db (assoc-in db [:visibility :login-modal?] true)
     :set-uri-token! "/"
     :start-alert! {:message "Save successfully"}}))

(reg-event-fx
  :create-password-fail-on
  (fn [{:keys [db]} _]
    {:start-alert! {:message "Error" :error? true}}))
