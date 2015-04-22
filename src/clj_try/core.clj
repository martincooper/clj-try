(ns clj-try.core)

(defn bind-error [f [val err]]
  (if (nil? err)
    (f val)
    [nil err]))

; Try function.
(defn try> [fn]
  ())

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
