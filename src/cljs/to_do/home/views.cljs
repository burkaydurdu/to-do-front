(ns to-do.home.views
  (:require
   [reagent.core :as r]
   [re-frame.core :refer [subscribe]]
   [to-do.home.sortable-hoc :as sortable]
   [to-do.home.subs]
   [to-do.home.modals.register :refer [register-view]]
   [to-do.home.modals.login :refer [login-view]]
   ))

(defn to-do-list []
  [sortable/sortable-component
      {}
      (r/atom
       [[:p "Hello"]
        [:p "Naber"]
        [:p "IYI"]])
      [:re-balance-for-state]])

(defn home
  []
  (r/create-class
   {:reagent-render (fn []
                      (let [visibility @(subscribe [:visibility])]
                        [:div 
                       [to-do-list]
                       (when (:register-modal? visibility)
                         [register-view])
                       (when (:login-modal? visibility)
                         [login-view])]))}))

