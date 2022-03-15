(defproject to-do "0.1.0"
  :description "Client and server side template"
  :url "https://github/burkaydurdu/charizard"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [org.clojure/clojurescript "1.10.520"]
                 [reagent "0.8.1"]
                 [re-frame "0.10.9"]
                 [com.rpl/specter "1.1.2"]
                 [com.cemerick/url "0.1.1"]
                 [kibu/pushy "0.3.8"]
                 [lambdaisland/uri "1.1.0"]
                 [day8.re-frame/http-fx "0.1.6"]
                 [cljs-ajax "0.8.0"]
                 [ring "1.7.1"]
                 [amalloy/ring-gzip-middleware "0.1.3"]
                 [compojure "1.6.1"]
                 [markdown-to-hiccup "0.6.2"]
                 [antizer-latest "0.1.0"]
                 [cljsjs/moment "2.24.0-0"]
                 [clj-commons/secretary "1.2.4"]
                 [cljsjs/react-sortable-hoc "1.11.0-1"]]
  :plugins [[lein-cljsbuild "1.1.7"]
            [lein-less "1.7.5"]
            [lein-ring "0.12.5"]
            [lein-cljfmt "0.6.4"]]

  :source-paths ["src/clj" "src/cljs"]

  :less {:source-paths ["less/"]
         :target-path  "resources/public/css"}

  :resource-paths ["resources"]

  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"]

  :main ^:skip-aot to-do.core

  :ring {:handler to-do.core/handler}

  :min-lein-version "2.6.1"

  :profiles {:dev {:dependencies [[binaryage/devtools "0.9.10"]
                                  [day8.re-frame/re-frame-10x "0.4.2"]
                                  [day8.re-frame/tracing "0.5.3"]
                                  [figwheel-sidecar "0.5.18"]
                                  [ring "1.7.1"]
                                  [compojure "1.6.1"]
                                  [amalloy/ring-gzip-middleware "0.1.3"]]
                   :plugins [[lein-figwheel "0.5.18"]]}
             :uberjar {:omit-source  true
                       :aot          :all
                       :auto-clean   false
                       :uberjar-name "todo_front.jar"
                       :source-paths ["src/clj" "src/cljs"]}}

  :cljsbuild {:builds [{:id           "dev"
                        :source-paths ["src/cljs"]
                        :figwheel     {:on-jsload "to-do.core/mount-root"}
                        :compiler     {:main to-do.core
                                       :output-to            "resources/public/js/compiled/app.js"
                                       :output-dir           "resources/public/js/compiled/out"
                                       :asset-path           "js/compiled/out"
                                       :source-map-timestamp true
                                       :preloads             [devtools.preload
                                                              day8.re-frame-10x.preload]
                                       :closure-defines      {"re_frame.trace.trace_enabled_QMARK_"        true
                                                              "day8.re_frame.tracing.trace_enabled_QMARK_" true
                                                              "to_do.util.api_url" "http://localhost:3022"}
                                       :externs              ~externs}}
                       {:id               "prod"
                         :source-paths     ["src/cljs"]
                         :compiler         {:main             to-do.core
                                            :output-to        "resources/public/js/compiled/app.js"
                                            :output-dir       "resources/public/js/compiled/prod/out"
                                            :optimizations    :advanced
                                            :pretty-print     false
                                            :warnings         false
                                            :externs          ~externs
                                            :closure-defines  {goog.DEBUG false
                                                               "to_do.util.api_url" "http://159.203.74.207:3022"}
                                            :closure-warnings {:externs-validation :off}}}]}
  :aliases {"package" ["do" "clean" ["cljsbuild" "once" "prod"]]})


