(ns clj-try.core)

; If value, apply function with value. If error, return error.
(defn bind-error [f {val :value err :error :as all}]
  (if (nil? err)
    (f val)
    all))

; Try function.
(defn try> [fn]
  (try
    { :value (fn) }
    (catch Exception ex
      { :error ex })))



; Simple run function macro.
(defmacro trym> [fn]
  `(~@fn))


; Try thread last macro.
(defmacro try->> [val & fns]
  (let [fns (for [f fns] `(bind-error ~f))]
    `(->> [~val nil]
          ~@fns)))

; Try thread first macro.
(defmacro try-> [val & fns]
  (let [fns (for [f fns] `(bind-error ~f))]
    `(-> [~val nil]
          ~@fns)))

; Try thread as macro.
(defmacro try-as->> [val & fns]
  (let [fns (for [f fns] `(bind-error ~f))]
    `(->> [~val nil]
          ~@fns)))

(defn test1 []
  (try> "aa"))
