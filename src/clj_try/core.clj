(ns clj-try.core)

(defmacro try>
  "Takes an expression and wraps it in a try / catch
  returning the value or exception in a map."
  [fn]
  `(try
     { :value (~@fn) }
     (catch Exception ex#
       { :error ex# })))

(defmacro bind-error->>
  "Bind error last. Determines if to run the expression or return the
  previous result if an error occurred."
  [fn value]
  `(let [{val# :value err# :error :as all#} ~value]
     (if (nil? err#)
       (try> (->> val# ~fn))
       all#)))

(defmacro try->>
  "Try last. Similar to the thread last macro, but wrapping each expression
  in a try / catch and passing the result to the following expression. If an error
  occurs, the chain will be short circuited returning the first error occurred."
  [val & fns]
  (let [fcns (for [f fns] `(bind-error->> ~f))]
    `(->> (try> ~val)
          ~@fcns)))

(defmacro bind-error->
  "Bind error last. Determines if to run the expression or return the
  previous result if an error occurred."
  [fn value]
  `(let [{val# :value err# :error :as all#} ~value]
     (if (nil? err#)
       (try> (-> val# ~fn))
       all#)))

(defmacro try->
  "Try last. Similar to the thread last macro, but wrapping each expression
  in a try / catch and passing the result to the following expression. If an error
  occurs, the chain will be short circuited returning the first error occurred."
  [val & fns]
  (let [fcns (for [f fns] `(bind-error-> ~f))]
    `(-> (try> ~val)
          ~@fcns)))
