(ns to-do.home.sortable-hoc
  (:require [re-frame.core :refer [dispatch]]
            [goog.object :as go]
            [reagent.core :as r]
            [sortable :as sortable]
            [react-sortable-hoc :as sort]))

(def drag-handle
  (r/adapt-react-class
   (sort/SortableHandle
    (fn [item]
      (let [i (:children (js->clj item :keywordize-keys true))]
        (r/as-component
         [:div i]))))))

(def sortable-item
  (r/adapt-react-class
   (sort/SortableElement
    (r/reactify-component
     (fn [{:keys [value]}]
       [:div
        [drag-handle value]])))))

(def sortable-list
  (r/adapt-react-class
   (sort/SortableContainer
    (r/reactify-component
     (fn [{:keys [items] :as m}]
       [:div
        (for [[item index] (map vector items (range))]
          [sortable-item {:key   (str "item-" index)
                          :index index
                          :value item}])])))))

(defn vector-move [coll prev-index new-index]
  (let [items (into (subvec coll 0 prev-index)
                    (subvec coll (inc prev-index)))]
    (-> (subvec items 0 new-index)
        (conj (nth coll prev-index))
        (into (subvec items new-index)))))

(defn sortable-component
  [props items event]
  [sortable-list
   (merge {:helperClass     "allowDrop"
           :pressDelay      150
           :items           @items
           :on-sort-end     #(let [item    (js->clj % :keywordize-keys true)
                                   old-idx (:oldIndex item)
                                   new-idx (:newIndex item)]
                               (do
                                 (swap! items vector-move old-idx new-idx)
                                 (dispatch (apply conj event [old-idx new-idx]))))
           :use-drag-handle true}
          (js->clj props :keywordize-keys true))])
