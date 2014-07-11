L = []
n = 1
while n <= 99:
    L.append(n)
    n = n + 2
print L
L = ['one','two','three','four']
n = 3
r = []
for i in range(n):
	r.append(L[i])
print r
print L[0:3]
L = [x*x for x in xrange(1,10)]
print L
L = [x*x for x in xrange(1,10) if x % 2==0]
print L
L = (x*x for x in xrange(1,10) if x % 2==0)
for n in L:
	print n
