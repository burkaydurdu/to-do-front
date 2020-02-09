(ns to-do.user.subs
  (:require [re-frame.core :refer [reg-sub]]))

(reg-sub
  :reset-password
  (fn [db]
    (:reset-password db)))

(reg-sub
  :create-password
  (fn [db]
    (:create-password db)))
