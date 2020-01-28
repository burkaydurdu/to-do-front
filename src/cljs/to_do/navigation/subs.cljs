(ns to-do.navigation.subs
  (:require [re-frame.core :refer [reg-sub]]
            [to-do.common.subs]))

(reg-sub
 :active-panel
 (fn [db _]
   (:active-panel db)))

(reg-sub
  :current-user
  (fn [db]
    (:current-user db)))

(reg-sub
  :visibility
  (fn [db]
    (:visibility db)))
