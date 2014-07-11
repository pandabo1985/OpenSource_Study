abs(-10)
print int('124')

def my_age_judge(x):
	if not isinstance(x,(int,float)):
		raise TypeError('bad operand type')
	if x > 10:
		print 'x > 10'
	else:
		print 'x < 10'

my_age_judge(4)
my_age_judge(11)
#my_age_judge('10')

def get_two_num(x,y,z):
	if x > y:
		return x,z
	else:return y,z

print get_two_num(2,3,1)
print get_two_num(6,4,1)
t = get_two_num(5,3,2)
print t[0]
def calcNums(nums):
	sum = 0;
	for n in nums:
		sum=sum+n;
	return sum;

print calcNums([1,2,3])
def calcNums(*nums):
	sum = 0;
	for n in nums:
		sum=sum+n;
	return sum;
print calcNums(1,2,3)

def fact(n):
	if n==1:
		return 1;
	return n* fact(n-1)	
print fact(5)
print fact(100)