(ns to-do.common.events
  (:require [re-frame.core :refer [reg-event-fx reg-event-db]]
            [to-do.db :as db]
            [to-do.util :as util]))

(reg-event-fx
 :initialise-db
 (fn [{:keys [db]} _]
   {:db (assoc db/default-db :name "Example")}))

(reg-event-db
  :add-data
  (fn [db [_ d-key d-value]]
    (assoc-in db d-key d-value)))

(reg-event-db
 :reset-in
 (fn [db [_ ks]]
   (util/dissoc-in db ks)))
