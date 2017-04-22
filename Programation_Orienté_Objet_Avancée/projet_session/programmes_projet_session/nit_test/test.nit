module test 

import actors 

class A 
        actor

        fun foo do print "foo"

        fun testfoo do foo

end

var a = new A

a.foo

#a.async.foo
a.testfoo
#a.async.testfoo
