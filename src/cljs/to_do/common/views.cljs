(ns to-do.common.views
  (:require [to-do.util :as util]
            [markdown-to-hiccup.core :as markdown]))

(defn markdown-view [content]
  (some-> content
          (markdown/md->hiccup)
          (markdown/component)))
