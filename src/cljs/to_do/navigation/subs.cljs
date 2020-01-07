(ns to-do.navigation.subs
  (:require [re-frame.core :refer [reg-sub]]
            [to-do.common.subs]))

(reg-sub
 :active-panel
 (fn [db _]
   (:active-panel db)))

(reg-sub
  :alert
  (fn [db _]
    (:alert db)))

(reg-sub
  :current-user
  (fn [db]
    (:current-user db)))
