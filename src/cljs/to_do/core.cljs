(ns to-do.core
  (:require
   [reagent.core :as r]
   [re-frame.core :as re-frame]
   [day8.re-frame.http-fx]
   [goog.dom :as dom]
   [to-do.routes :refer [app-routes]]
   [to-do.navigation.views :as views]
   [to-do.common.events]
   [to-do.home.events]
   [to-do.effects]))

(defn mount-root
  []
  (re-frame/clear-subscription-cache!)
  (r/render [views/main-panel] (dom/getElement "app")))

(defn ^:export init
  []
  (app-routes)
  (re-frame/dispatch-sync [:initialise-db])
  (mount-root))
