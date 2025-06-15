(ns demo
  (:require [fastmath.interpolation :as interpolation]
            [scicloj.tableplot.v1.plotly :as plotly]
            [tablecloth.api :as tc]))

(def px [0 1 3 4 5 8 9])
(def py [0 0 1 7 3 4 6])

(def in    (tc/dataset {:x (range 0 8.0 0.01)}))
;;subset of available interpolations.
(def kinds [:linear
            :cubic
            :monotone
            :akima
            :neville
            :divided-difference
            :polynomial
            :sprague
            :step-before
            :step-after])
kinds

(def results
  (->> (for [k kinds]
         (let [f (interpolation/interpolation k px py)]
           [k (mapv f x)]))
       (reduce (fn [acc [k v]]
                 (tc/add-column acc k v)) in)))

results

(defn plot-it [data kind]
  (-> data
      (plotly/layer-line
       {:=x :x
        :=y kind})))

(for [k kinds]
  `(~'plot-it ~'results ~k))

(plot-it results :linear)
(plot-it results :cubic)
(plot-it results :monotone)
(plot-it results :akima)
(plot-it results :neville)
(plot-it results :divided-difference)
(plot-it results :polynomial)
(plot-it results :sprague)
(plot-it results :step-before)
(plot-it results :step-after)


(def indices
  (->> kinds (map-indexed (fn [idx x] [x idx])) (into {})))

(def wide-results
  (-> (tc/pivot->longer results
                        (complement #{:x})
                        {:target-columns    :interpolation
                         :value-column-name :y})
      (tc/map-rows (fn [{:keys [interpolation] :as r}]
                     {:z (indices interpolation)}))))

wide-results


(-> wide-results
    (plotly/layer-line
     {:=x :x
      :=y :y
      :=color :interpolation }))

(-> wide-results
    (plotly/layer-line
     {:=x :x
      :=y :y
      :=z :z
      :=color :interpolation
      :=coordinates :3d}))
