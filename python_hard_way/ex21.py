def add(a,b):
    print "ADDING %d + %d " % (a,b)
    return a + b

def subtract(a, b):
    print "SUBTRACT %d - %d " % (a,b)
    return a -b

def multiply(a, b):
    print "MULTIPLY %d * %d " % (a,b)
    return a * b
def divide(a, b):
    print "DIVIDING %d / %d" % (a,b)
    return a/b

print "Let's do some math with just functions!"

age = add(20, 10)
height = subtract(178,8)
weight = multiply(20,3)
iq = divide(180,2)

print "Age: %d, Height: %d, Weight: %d, IQ: %d" %(age,height,weight,iq)

print "Here is a puzzle"

what = add(age, subtract(height, multiply(weight, divide(iq,2))))
print "That becoms:",what, "Can you do it by hand?"
