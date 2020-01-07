(ns to-do.navigation.events
  (:require [re-frame.core :refer [reg-event-db reg-event-fx]]))

(reg-event-db
 :set-active-panel
 (fn [db [_ active-panel]]
   (assoc db :active-panel active-panel)))

(reg-event-db
 :close-alert
 (fn [db _]
   (assoc-in db [:alert :show?] false)))

(reg-event-fx
  :log-out
  (fn [{:keys [db]} _]
    {:db (dissoc db :current-user)
     :remove-current-user! []}))
