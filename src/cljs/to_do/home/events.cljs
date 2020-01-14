(ns to-do.home.events
 (:require [re-frame.core :refer [reg-event-fx reg-event-db]]
           [to-do.common.helper :as helper]
           [com.rpl.specter :as sp :refer-macros [transform setval]]
           [clojure.set :refer [difference]]
           [to-do.util :as util]))

(defn vector-move [coll prev-index new-index]
  (let [items (into (subvec coll 0 prev-index)
                    (subvec coll (inc prev-index)))]
    (-> (subvec items 0 new-index)
        (conj (nth coll prev-index))
        (into (subvec items new-index)))))

(defn set-state-order [states]
 (vec (map-indexed (fn [idx itm] (assoc itm :s_order idx)) states)))

(reg-event-db
  :re-balance-for-state
  (fn [db [_ old-idx new-idx]]
    (assoc db :states 
           (-> (:states db)
               (vector-move old-idx new-idx)
               (set-state-order)))))

(reg-event-fx
  :user-register
  (fn [{:keys [db]} _]
    (let [register-form (:register-form db)]
      {:http-xhrio (merge (util/create-request-map :post "/register"
                                                   :user-register-result-ok
                                                   :user-register-fail-on)
                          {:params (dissoc register-form :password-conf)})})))

(reg-event-db
  :user-register-result-ok
  (fn [db _]
    (-> db 
        (helper/show-alert {:message "Success"})
        (assoc-in [:visibility :register-modal?] false)
        (dissoc :register-form))))

(reg-event-db
  :user-register-fail-on
  (fn [db [_ response]]
    (helper/show-alert db {:type    :error
                           :message "Error"})))

(reg-event-fx
  :user-login
  (fn [{:keys [db]} _]
    (let [login-form (:login-form db)]
      {:http-xhrio (merge (util/create-request-map :post "/login"
                                                   :user-login-result-ok
                                                   :user-login-fail-on)
                          {:params login-form})})))

(reg-event-fx
  :user-login-result-ok
  (fn [{:keys [db]} [_ response]]
    {:set-current-user! response
     :db (-> db
             (helper/show-alert {:message "Success"})
             (assoc :current-user response)
             (assoc-in [:visibility :login-modal?] false)
             (dissoc :login-form))}))

(reg-event-db
  :user-login-fail-on
  (fn [db [_ response]]
    (helper/show-alert db {:type    :error
                           :message "error"})))

(reg-event-fx
  :get-user-states
  (fn [_ _]
    {:http-xhrio (util/create-request-map :get "/state"
                                          :get-user-states-result-ok
                                          :get-user-states-fail-on)}))

(reg-event-db
  :get-user-states-result-ok
  (fn [db [_ response]]
    (assoc db :states response
              :coming-states response)))

(reg-event-db
  :get-user-states-fail-on
  (fn [db [_ response]]
    (helper/show-alert db {:type    :error
                           :message "error"})))

(reg-event-db
  :update-todo
  (fn [db [_ id t-key t-val]]
    (transform [:states sp/ALL #(= (:id %) id)] #(assoc % t-key t-val) db)))

(reg-event-db
  :delete-todo
  (fn [db [_ id]]
    (setval [:states sp/ALL #(= (:id %) id)] sp/NONE db)))

(reg-event-fx
  :add-todo
  (fn [{:keys [db]} [_ id-list]]
    (let [s-order (-> db :states count)]
      {:db (update db :states conj {:id (str (random-uuid))
                                    :title ""
                                    :content []
                                    :all_done false
                                    :s_order s-order})})))

(reg-event-fx
  :send-states
  (fn [{:keys [db]} _]
    (let [states (difference (-> db :states set) (-> db :coming-states set))]
      {:http-xhrio (merge (util/create-request-map :post "/state"
                                          :send-states-result-ok
                                          :send-states-fail-on)
                        {:params (vec states)})})))

(reg-event-db
 :send-states-result-ok
 (fn [db _]
   (helper/show-alert db {:message "Update is success"})))

(reg-event-db
  :send-states-fail-on
  (fn [db [_ response]]
    (helper/show-alert db {:type    :error
                           :message "error"})))
