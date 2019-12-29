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
    (helper/show-alert db {:message "Success"})))

(reg-event-db
  :user-register-fail-on
  (fn [db [_ response]]
    (helper/show-alert db {:type    :error
                           :message "Error"})))
