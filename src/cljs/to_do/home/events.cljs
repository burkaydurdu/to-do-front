(ns to-do.home.events
 (:require [re-frame.core :refer [reg-event-fx reg-event-db]]))

(reg-event-db
  :re-balance-for-state
  (fn [db _]
    db))
