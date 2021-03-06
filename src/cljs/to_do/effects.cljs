(ns to-do.effects
  (:require [re-frame.core :refer [reg-fx dispatch]]
            [goog.dom :as dom]
            [to-do.util :as util]))

(reg-fx
 :set-current-user!
 (fn [current-user]
   (util/set-item! "current_user" current-user)))

(reg-fx
  :remove-current-user!
  (fn [_]
    (util/remove-item! "current_user")))

(reg-fx
 :focus-by-id!
 (fn [id]
   (some-> id dom/getElement .focus)))

(reg-fx
  :start-alert!
  (fn [opt]
    (util/alert-action (:message opt) (:error? opt))))

(reg-fx
  :change-url!
  (fn [path]
    (util/change-url path)))

(reg-fx
  :set-uri-token!
  (fn [uri]
    (util/set-uri-token! uri)))
