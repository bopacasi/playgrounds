(defn avg
  [numbers]
  (print numbers "=> ")
  (/ (apply + numbers) (count numbers)))


(println (avg [60 80 100 400]))

(def v [42 "foo" 99.2 [5 12]])

(let [[x _ _ [y z]] v]
  (+ x y))

(def m {:a 5 :b 6 :c [7 8 9] :d {:e 10 :f 11} "foo" 88 42 false})

(let [{a :a b :b} m]
  (+ a b))

(let [{{e :e} :d} m]
  (* e 2))

(let [{[x _ y] :c} m]
  (+ x y))

(:import [java.util.Date])
(def map-in-vector ["James" {:birthday (java.util.Date. 73 1 6)}])

(let [[name {bd :birthday}] map-in-vector]
  (str name " was born on " bd))

(let [{k :unknown x :a
       :or {k 50}} m]
  (+ k x))

(let [{opt1 :option} {:option false}
      opt1 (or opt1 true)
      {opt2 :option :or {opt2 true}} {:option false}]
  {:opt1 opt1 :opt2 opt2})

(def chas {:name "Chas" :age 31 :location "Massachusetts"})

(let [{:keys [name age location]} chas]
  (format "%s is %s years old and lives in %s." name age location))

