(ns to-do.common.helper
  (:require [clojure.string :as str]))

(defn show-alert
  [db {:keys [type message desc] :or {type :success}}]
  (assoc db :alert {:type    type
                    :message message
                    :desc    desc
                    :show?   true}))

