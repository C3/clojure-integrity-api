(ns integrity-api.util)

(defn flatten-tree [tree]
  (cond
    (map? tree) (mapcat (fn [[k v]] (map #(cons k %) (flatten-tree v))) tree)
    (sequential? tree) (mapcat flatten-tree tree)
    :else (list (list tree))))

