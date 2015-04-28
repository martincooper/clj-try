# clj-try
Clojure Try / Error macros.

## Overview

This set of macros allows a more functional, composable way to handle exceptions similar in style to the Try computation (Monad) found in other functional langages. These macros are based upon the three Clojure threading macros -> (thread first), ->> (thread last) and as-> (thread as). 

Each expression passed to a try block is evaluated in a try catch handler. If the expression doesn't fail, the result from that expression will be passed as an argument into the next expression (either as the first, last or specified argument). This in turn will get evaluated in a try / catch block until all expressions have been evaluated. If no exceptions occurred, the final result will be returned in a map with a :value key...

```clojure
;; => {:value "My Result"}
```

If any expression in a try block fails / throws an exception, the call chain short-circuits returning the original exception in a map with an :error key containing the exception...

```clojure
;; => {:error #<ArithmeticException java.lang.ArithmeticException: Divide by zero>}
```

# Example Usage

## Adding a reference.

```clojure

[clj-try "0.1.0"]

;; In your ns statement:
(ns my.ns
  (:require [clj-try.core :refer :all]))

```

## Try - Thread first
```clojure
(try-> "a b c d" 
       .toUpperCase 
       (.replace "A" "X") 
       (.split " ") 
       first)

;; => {:value "X"}

(try-> "a b c d" 
       .toUpperCase 
       (.replace "A" "X") 
       (str (/ 100 0))     ;; Div by zero exception!! 
       first)

;; => {:error #<ArithmeticException java.lang.ArithmeticException: Divide by zero>}
```

## Try - Thread last
```clojure
(try->> (range)
        (map #(* % %))
        (filter even?)
        (take 10)
        (reduce +))

;; => {:value 1140}

(try->> (range)
        (map #(* % %))
        (filter even?)
        (take (10 / 0))   ;; Div by zero exception!! 
        (reduce +))

;; => {:error #<ArithmeticException java.lang.ArithmeticException: Divide by zero>}
```

## Try - Thread as
```clojure
(try-as-> " a b c d " %
       (.toUpperCase %) 
       (.replace % "A" "X") 
       (.trim %))

;; => {:value "X B C D"}


(try-as-> " a b c d " %
       (.toUpperCase %) 
       (.replace % "A" "X") 
       (Integer/parseInt %)   ;; NumberFormatException !!
       (.trim %)

;; => {:error #<NumberFormatException java.lang.NumberFormatException: For input string: " X B C D ">}
```

### Credits

Clojure Try / Error macro is maintained by Martin Cooper : Copyright (c) 2015

## License

[Apache 2.0](http://www.apache.org/licenses/LICENSE-2.0)