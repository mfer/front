build: compile test translate

compile:
	javac lexer/*.java
	javac symbols/*.java
	javac inter/*.java
	javac parser/*.java
	javac main/*.java
	javac translater/Tradutor.java

test:
	@for i in `(cd tests; ls *.txt | sed -e 's/.txt$$//')`;\
		do echo $$i.txt in $$i.i;\
		java main.Main <tests/$$i.txt >tmp/$$i.i;\
	done
	
translate:
	@for i in `(cd tests; ls *.txt | sed -e 's/.txt$$//')`;\
		do echo $$i.i in $$i.java;\
		java translater.Tradutor tmp/$$i.i;\
		javac $$i.java;\
		mv *.class *.java tmp/;\
	done
	

clean:
	(cd lexer; rm *.class)
	(cd symbols; rm *.class)
	(cd inter; rm *.class)
	(cd parser; rm *.class)
	(cd main; rm *.class)
	(cd translater; rm *.class)
