(ns to-do.home.views
  (:require
   [reagent.core :as reagent]
   [to-do.home.sortable-hoc :as sortable]
   [to-do.home.subs]))

(defn to-do-list []
  [sortable/sortable-component
      {}
      (reagent/atom
       [
        [:p "Hello"]
        [:p "Naber"]
        [:p "IYI"]])
      [:re-balance-for-state]])

(defn home
  []
  (reagent/create-class
   {:reagent-render (fn []
                      [:div 
                       [to-do-list]])}))

