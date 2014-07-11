a = 100
if a>=0:
	print 'a>=0, ',a
else:
	print 'a<0,',a

print 'Hi, %s, you age is %d' % ('panda',20)


classmates = ['big','two','five','six'] 
print len(classmates)
print classmates[0]
print classmates[-1]
print classmates
print classmates.pop()
print classmates
print classmates.insert(1,'panda')
print classmates
classmates = ('one','tow',['a','b']);
print classmates
print len(classmates)
classmates[2][0]='x'
classmates[2][1]='y'
print classmates

age = 20
if age > 20:
	print 'age small 20'
else:
	print 'age big or equal 20'

numbers = {'one','two','four'}
for num in numbers:
	print num
print range(10)

birth = int(raw_input('birth:'))
if birth>1900:
	print 'old man'
else:
	print'yong man'
nameSccor = {'panda':99,'dab':60,'xiaoliu':50,'xiaofang':40}
print nameSccor
nameSccor['xiaoliu']=100
print nameSccor
print nameSccor.get('xiaoliu')

name = set(['pan','lu','fang'])
print name
name.add('xiao')
print name