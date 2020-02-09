(ns to-do.routes
  (:require-macros [secretary.core :refer [defroute]])
  (:import goog.history.Html5History)
  (:require
   [secretary.core :as secretary]
   [re-frame.core :refer [dispatch]]
   [goog.events :as events]
   [pushy.core :as pushy]
   [lambdaisland.uri :refer [uri]]
   [goog.history.EventType :as EventType]
   [to-do.common.events]
   [to-do.navigation.events]
   [to-do.user.events]
   [to-do.home.views :refer [home]]
   [to-do.user.views :refer [reset-password-view
                             create-new-password-view]]))

(def history
  (pushy/pushy secretary/dispatch!
               #(when (secretary/locate-route (-> % uri :path)) %)))

(defn hook-browser-navigation! []
  (doto (Html5History.)
    (events/listen
     EventType/NAVIGATE
     (fn [event]
       (secretary/dispatch! (.-token event))))
    (.setEnabled true)))

(defn app-routes []
  (secretary/set-config! :prefix "#")

  (defroute "/" []
    (dispatch [:set-active-panel [home :home]]))

  (defroute "/reset_password" []
    (dispatch [:set-active-panel [reset-password-view
                                  :reset-password]]))

  (defroute "/create_password" []
    (dispatch [:set-active-panel [create-new-password-view
                                  :create-password]]))

  (hook-browser-navigation!))
