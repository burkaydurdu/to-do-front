(ns to-do.navigation.events
  (:require [re-frame.core :refer [reg-event-db reg-event-fx]]
            [to-do.util :as util]))

(reg-event-db
 :set-active-panel
 (fn [db [_ active-panel]]
   (assoc db :active-panel active-panel)))

(reg-event-fx
  :log-out
  (fn [{:keys [db]} _]
    {:http-xhrio (util/create-request-map :post "/logout"
                                          :log-out-result-ok
                                          :log-out-fail-on)}))

(reg-event-fx
  :log-out-result-ok
  (fn [{:keys [db]} _]
    {:db (dissoc db :current-user :states :coming-states)
     :remove-current-user! []
     :start-alert! {:message (str "Bye Bye " (-> db :current-user :name))}}))

(reg-event-fx
  :log-out-fail-on
  (fn [{:keys [db]} _]
    {:db (dissoc db :current-user :states :coming-states)
     :remove-current-user! []
     :start-alert! {:message (str "Bye Bye " (-> db :current-user :name))}}))
