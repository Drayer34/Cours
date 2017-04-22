import actors


# A class anotated with `actor`
# It automatically gets the `async` property to make asynchronous calls on it
class A
    actor

    # Prints "foo"
    fun foo do print "foo"

    # Returns i^2
    fun bar(i : Int): Int do return i * i
end

# Create a new instance of A
var a = new A

# Make an asynchronous call
a.async.foo

# Make a synchronous call
a.foo

# Make an asynchronous call
# Which return a `Future[Int]` instead of `Int`
var r = a.async.bar(5)

# Retrieve the value of the future
print r.join

# Make a Synchronous call
print a.bar(5)

