(ns clj-try.core)

(defmacro try>
  "Takes an expression and wraps it in a try / catch.
  returning the value or exception in a map."
  [fn]
  `(try
     { :value (~@fn) }
     (catch Exception ex#
       { :error ex# })))

; If value, apply function with value. If error, return error.
(defn bind-error
  "Bind method. Determines if to run the expression or return the
  previous result if an error occurred."
  [f {val :value err :error :as all}]
  (if (nil? err)
    (try> (f val))
    all))


(defmacro my->>
  "Threads the expr through the forms. Inserts x as the
  last item in the first form, making a list of it if it is not a
  list already. If there are more forms, inserts the first form as the
  last item in second form, etc."
  {:added "1.1"}
  [x & forms]
  (loop [x x, forms forms]
    (if forms
      (let [form (first forms)
            threaded (if (seq? form)
              (with-meta `(~(first form) ~@(next form)  ~x) (meta form))
              (list form x))]
        (recur threaded (next forms)))
      x)))


(defmacro err->> [val & fns]
  (let [fcns (for [f fns] `(bind-error ~f))]
    `(->> ~val
          ~@fcns)))

(defmacro try->> [val & fns]
  (let [fns (for [f fns] `~f)]
    `(->> ~val
          ~@fns)))


(defn test1 []
  (try->>
   (str "aa")
   (str "bb")
   (str "cc")))


(defn test2 []
  (err->>
   (str "bb")
   (str "cc")))
