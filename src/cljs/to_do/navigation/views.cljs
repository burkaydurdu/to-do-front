(ns to-do.navigation.views
  (:require
   [re-frame.core :refer [dispatch subscribe]]
   [to-do.navigation.subs]))

(defn navigation-panel
  [active-panel]
  (let [[panel panel-name] @active-panel]
    [:div.container {:style {:padding "2rem"}}
     (when panel
       [panel])]))

(defn main-panel []
  [navigation-panel (subscribe [:active-panel])])
