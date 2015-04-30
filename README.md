# clj-try
Clojure Try / Error macros.

## Overview

This set of macros allows a more functional, composable way to handle exceptions similar in style to the Try computation (Monad) found in other functional langages. These macros are based upon the three Clojure threading macros -> (thread first), ->> (thread last) and as-> (thread as). 

Each expression passed to a try block is evaluated in a try catch handler. If the expression doesn't fail, the result from that expression will be passed as an argument into the next expression (either as the first, last or specified argument). This in turn will get evaluated in a try / catch block until all expressions have been evaluated. If no exceptions occurred, the final result will be returned in a Success record with a :value key...

```clojure
;; -> #clj_try.core.Success{:value "My Result"}
```

If any expression in a try block fails / throws an exception, the call chain short-circuits returning the original exception in a Failure record with an :error key containing the exception...

```clojure
;; => #clj_try.core.Failure{:error #<ArithmeticException
;;          java.lang.ArithmeticException: Divide by zero>}
```

# Example Usage

## Adding a reference.

```clojure

[clj-try "0.2.0"]

;; In your ns statement:
(ns my.ns
  (:require [clj-try.core :refer :all]))

```

## Try - Thread first

The "Try Thread First" macro is based on the Clojure/core "thread first" -> macro. Each expression is evaluated in a try / catch block, if no exception occurs, the result is passed in as the **_first_** argument to the next expression.
```clojure
(try-> "a b c d" 
       .toUpperCase 
       (.replace "A" "X") 
       (.split " ") 
       first)

;; => #clj_try.core.Success{:value "X"}


(try-> "a b c d" 
       .toUpperCase 
       (.replace "A" "X") 
       (str (/ 100 0))     ;; Div by zero exception!! 
       first)

;; => #clj_try.core.Failure{:error #<ArithmeticException
;;          java.lang.ArithmeticException: Divide by zero>}
```

## Try - Thread last

The "Try Thread Last" macro is based on the Clojure/core "thread last" ->> macro. Each expression is evaluated in a try / catch block, if no exception occurs, the result is passed in as the **_last_** argument to the next expression.
```clojure
(try->> (range)
        (map #(* % %))
        (filter even?)
        (take 10)
        (reduce +))

;; => #clj_try.core.Success{:value 1140}


(try->> (range)
        (map #(* % %))
        (filter even?)
        (take (/ 10 0))   ;; Div by zero exception!! 
        (reduce +))

;; => #clj_try.core.Failure{:error #<ArithmeticException
;;         java.lang.ArithmeticException: Divide by zero>}
```

## Try - Thread as

The "Try Thread As" macro is based on the Clojure/core "thread as" as-> macro. Each expression is evaluated in a try / catch block, if no exception occurs, the result is passed in as the **_specified_** argument to the next expression (in this example, specified by the percentage symbol)
```clojure
(try-as-> " a b c d " %
       (.toUpperCase %) 
       (.replace % "A" "X") 
       (.trim %))

;; => #clj_try.core.Success{:value "X B C D"}


(try-as-> " a b c d " %
       (.toUpperCase %) 
       (.replace % "A" "X") 
       (Integer/parseInt %)   ;; NumberFormatException !!
       (.trim %)

;; => #clj_try.core.Failure{:error #<NumberFormatException
;;          java.lang.NumberFormatException: For input string: " X B C D ">}
```

### Credits

Clojure Try / Error macro is maintained by Martin Cooper : Copyright (c) 2015

## License

[Apache 2.0](http://www.apache.org/licenses/LICENSE-2.0)