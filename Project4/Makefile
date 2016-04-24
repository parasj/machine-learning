all: clean

scala: cleangradle
	(cd code && ./gradlew run)

submit: clean
	$(MAKE) zipsubmission
	$(MAKE) cleanTempDir

zipsubmission: copysubmission
	zip -r pjain67.zip pjain67 -x "*.DS_Store" -x "*/RL_sim*" -x "*/.gradle*"

copysubmission:
	mkdir pjain67
	cp -r src pjain67/
	cp writeup/tex/pjain67_analysis.pdf pjain67/
	cp Makefile pjain67/
	cp README.txt pjain67/

clean: cleanTempDir cleanZip cleangradle

cleanZip:
	rm -f pjain67.zip

cleanTempDir:
	rm -rf pjain67

cleangradle:
	(cd src/code && ./gradlew clean)