(ns to-do.home.subs
  (:require [re-frame.core :refer [reg-sub]]))

(reg-sub
  :login-form
  (fn [db]
    (:login-form db)))


(reg-sub
  :register-form
  (fn [db]
    (:register-form db)))

(reg-sub
  :visibility
  (fn [db]
    (:visibility db)))
