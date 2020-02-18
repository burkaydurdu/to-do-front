(ns to-do.home.events
 (:require [re-frame.core :refer [reg-event-fx reg-event-db]]
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
      {:db  (assoc-in db [:loading :register?] true)
       :http-xhrio (merge (util/create-request-map :post "/register"
                                                   :user-register-result-ok
                                                   :user-register-fail-on)
                          {:params (dissoc register-form :password-conf)})})))

(reg-event-fx
  :user-register-result-ok
  (fn [{:keys [db]} _]
    {:start-alert! {:message "Register is successfully. Check your email"}
     :db (-> db
             (assoc-in [:visibility :register-modal?] false)
             (assoc-in [:loading :register?] false)
             (dissoc :register-form))}))

(reg-event-fx
  :user-register-fail-on
  (fn [{:keys [db]} [_ response]]
    {:db (assoc-in db [:loading :register?] false)
     :start-alert! {:message "Error" :error? true}}))

(reg-event-fx
  :user-login
  (fn [{:keys [db]} _]
    (let [login-form (:login-form db)]
      {:db (assoc-in db [:loading :login?] true)
       :http-xhrio (merge (util/create-request-map :post "/login"
                                                   :user-login-result-ok
                                                   :user-login-fail-on)
                          {:params login-form})})))

(reg-event-fx
  :user-login-result-ok
  (fn [{:keys [db]} [_ response]]
    {:set-current-user! response
     :start-alert! {:message (str "Welcome " (:name response))}
     :db (-> db
             (assoc :current-user response)
             (assoc-in [:visibility :login-modal?] false)
             (assoc-in [:loading :login?] false)
             (dissoc :login-form))}))

(reg-event-fx
  :user-login-fail-on
  (fn [{:keys [db]} [_ response]]
    {:db  (assoc-in db [:loading :login?] false)
     :start-alert! {:message "Error" :error? true}}))

(reg-event-fx
  :get-user-states
  (fn [_ _]
    {:http-xhrio (util/create-request-map :get "/state"
                                          :get-user-states-result-ok
                                          :get-user-states-fail-on)}))

(reg-event-db
  :get-user-states-result-ok
  (fn [db [_ response]]
    (let [sort-res (vec (sort-by :s_order response))]
     (assoc db :states sort-res
               :coming-states sort-res))))

(reg-event-fx
  :get-user-states-fail-on
  (fn [_ [_ response]]
    {:start-alert! {:message "Error" :error? true}}))

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
                                    :content nil
                                    :all_done false
                                    :s_order s-order})})))

(reg-event-fx
  :send-states
  (fn [{:keys [db]} _]
    (let [create-or-update-list (difference (-> db :states set) (-> db :coming-states set))
          delete-list  (difference (->> db :coming-states (map :id ) set) (->> db :states (map :id ) set))]
      (when-not (and (empty? create-or-update-list)
                     (empty? delete-list))
        {:http-xhrio (merge (util/create-request-map :post "/state"
                                                     :send-states-result-ok
                                                     :send-states-fail-on)
                            {:params {:create-or-update-list (vec create-or-update-list)
                                      :delete-list (vec delete-list)}})}))))

(reg-event-fx
  :send-states-result-ok
  (fn [{:keys [db]} _]
    {:start-alert! {:message "Update is success"}
     :db (assoc db :coming-states (:states db))}))

(reg-event-fx
  :send-states-fail-on
  (fn [_ [_ response]]
    {:start-alert! {:message "Error" :error? true}}))

(reg-event-db
  :copy-current-user-profile-data
  (fn [db _]
    (assoc db :profile-form (select-keys (:current-user db) [:dark_mode
                                                             :font
                                                             :font_size
                                                             :name]))))

(reg-event-fx
  :user-profile-update
  (fn [{:keys [db]} _]
    {:db         (assoc-in db [:loading :profile?] true)
     :http-xhrio (merge (util/create-request-map :put "/user"
                                                 :user-profile-update-result-ok
                                                 :user-profile-update-fail-on)
                        {:params (:profile-form db)})}))

(reg-event-fx
  :user-profile-update-result-ok
  (fn [{:keys [db]} _]
    (let [current-user (merge (:current-user db) (:profile-form db))]
      {:set-current-user! current-user
       :db (-> db
               (assoc :current-user current-user)
               (assoc-in [:visibility :profile-modal?] false)
               (assoc-in [:loading :profile?] false)
               (dissoc :profile-form))
       :start-alert! {:message "Update is success"}})))

(reg-event-fx
  :user-profile-update-fail-on
  (fn [{:keys [db]}]
    {:db (assoc-in db [:loading :profile?] false)
     :start-alert! {:message "Error" :error? true}}))

