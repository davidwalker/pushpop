(ns pushpopchestnutreless.test-runner
  (:require
   [doo.runner :refer-macros [doo-tests]]
   [pushpopchestnutreless.core-test]
   [pushpopchestnutreless.common-test]))

(enable-console-print!)

(doo-tests 'pushpopchestnutreless.core-test
           'pushpopchestnutreless.common-test)
