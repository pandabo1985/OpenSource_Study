class Student(object):
	def __init__(self, name,score):
		self.score = score
		self.name = name
	def print_sorce(self):
		print '%s : %s' %(self.name,self.score)
	def get_grade(self):
		if self.score > 90:
			return 'A'
		elif self.score > 60:
			return 'b'
		else:
			return 'c'

stu = Student('panda',59)
print stu.print_sorce()
print stu.get_grade()

class Animal(object):
	def run(self):
		print 'Animal is run ....'

class Dog(Animal):
	 def run(self):
	 	print 'Dog is run ...'
class Cat(Animal):
	 pass
dog  = Dog()
cat = Cat()

print dog.run()
print cat.run()

print isinstance(dog,Dog)
print isinstance(dog, Animal)

print type(dog)==type(cat)
print dir(dog)

def set_age(self,age):
	self.age = age

from types import MethodType
stu.set_age = MethodType(set_age,stu,Student)
stu.set_age(100)
print stu.age

from hello import