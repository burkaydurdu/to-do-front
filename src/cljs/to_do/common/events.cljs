(ns to-do.common.events
  (:require [re-frame.core :refer [reg-event-fx reg-event-db inject-cofx]]
            [to-do.db :as db]
            [to-do.util :as util]))

(reg-event-fx
 :initialise-db
 
 [(inject-cofx :current-user)]
 (fn [{:keys [_ current-user]} _]
   {:db (assoc db/default-db :name "TO-DO"
                             :current-user current-user)}))

(reg-event-db
  :add-data
  (fn [db [_ d-key d-value]]
    (assoc-in db d-key d-value)))

(reg-event-db
 :reset-in
 (fn [db [_ ks]]
   (util/dissoc-in db ks)))

(reg-event-fx
  :focus-by-id
  (fn [_ [_ id]]
    {:focus-by-id! id}))
