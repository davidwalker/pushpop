(ns pushpop.test-runner
 (:require
  [doo.runner :refer-macros [doo-tests]]
  [pushpop.core-test]
  [pushpop.common-test]))

(enable-console-print!)

(doo-tests 'pushpop.core-test
           'pushpop.common-test)
