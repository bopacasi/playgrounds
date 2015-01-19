;(ns Base64
;  (:import [java.util.Base64]))

(def encoder (java.util.Base64/getEncoder))

(println (.encodeToString encoder (.getBytes "hello world")))

;(println (+ 1 1))
