(ns to-do.common.subs
  (:require [re-frame.core :refer [reg-sub]]))

(reg-sub
  :token
  (fn [db]
    (-> db :current-user :token)))
