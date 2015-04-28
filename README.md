# clj-try
Clojure Try / Error macros.

## Overview

This set of macros allows a more functional, composable way to handle exceptions similar in style to the Try computation (Monad) in other functional langages. These macros are based upon the three Clojure threading macros -> (thread first), ->> (thread last) and as-> (thread as), wrapping each expression up in a try catch handler, returning either the result of the function, or the exception in a map.

# Example Usage

## Creating

```clojure

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


### Credits

Clojure Try / Error macro is maintained by Martin Cooper : Copyright (c) 2015

## License

[Apache 2.0](http://www.apache.org/licenses/LICENSE-2.0)