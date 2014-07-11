def f(x):
	return x*x
print map(f,[1,2,3,4,5,6,7,8,9])

def fn(x,y):
	#print 'x = %d,y = %d'%(x,y)
	return x*10+y
print reduce(fn,[1,3,5,7,9])

def str2int(s):
    return reduce(fn,map(int,s))

print str2int('12345')

L = [2,3,5,1,8,7]
print sorted(L)

def lazy_sum(*args):
	def sum():
		ax = 0
		for  n in args:
			ax = ax+n
		return ax
	return sum

f = lazy_sum(1,2,3,4)
print f
print f()

print map(lambda x: x*x, [1,2,3,4,5])

def now():
	print '2014-07-09'
print now.__name__

def log(func):
	def wrapper(*args,**kw):
		print 'call %s():' % func.__name__
		return func(*args,**kw)
	return wrapper
@log
def now_new():
	print '2014-07-10-new'
print now_new()

def log(text):
    def decorator(func):
        def wrapper(*args, **kw):
            print '%s %s():' % (text, func.__name__)
            return func(*args, **kw)
        return wrapper
    return decorator

@log('execute')

def now_new_new():
	print '2014_now_new_new'
print now_new_new()

print int('100',base=2)
def int2(x,base=2):
	return int(x,base)
print int2('100')

import functools
int8 = functools.partial(int,base=8)
print int8('10')