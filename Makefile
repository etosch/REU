jama = Jama-1.0.3.jar
cp = target:lib/$(jama)

dirs : 
	if [[ ! -d lib ]] ; then mkdir lib ; fi
	if [[ ! -d target ]] ; then mkdir target ; fi
	wget http://math.nist.gov/javanumerics/jama/Jama-1.0.3.jar
	mv $(jama) lib/$(jama) 

build : dirs
	javac -cp $(cp) -d target src/PerceptronClassifier.java

run : build
	java -cp $(cp) PerceptronClassifier Bools data/bools.dat

clean : 
	rm target/*
