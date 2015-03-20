1. Copy files from this repo to local shadow say C:/temp/integrationExercise by:
	a. cd C:/temp/integrationExercise
	b. git init
	c. git remote add origin https://github.com/ashfaqinc/IntegrationExercise
	d. git pull origin master
2. Create war file as follows:
	a. mvn clean compile war:war
	b. The war file should be in the target directory