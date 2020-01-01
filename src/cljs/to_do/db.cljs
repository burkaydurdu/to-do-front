(ns to-do.db
  (:require [re-frame.core :refer [reg-cofx]]
            [to-do.util :as util]))

(def default-db {:name "App"})

(reg-cofx
 :current-user
 (fn [cofx _]
   (assoc cofx :current-user (util/get-item "current_user"))))
