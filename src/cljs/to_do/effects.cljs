(ns to-do.effects
  (:require [re-frame.core :refer [reg-fx dispatch]]
            [to-do.util :as util]))

(reg-fx
 :set-current-user!
 (fn [current-user]
   (util/set-item! "current_user" current-user)))
