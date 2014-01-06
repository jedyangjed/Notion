

include BuildProperties.properties
build=$(build.major).$(build.minor).$(build.patch)
versionDir=Notion-$(build)
dir=zip-temp/$(versionDir)


# The Log4j setup

dist:
	(cd Documentation && make html)
	(cd Documentation && make epub)
	(cd ui/ && brunch build)
	ant clean 
	ant resolve
	ant jar
	rm -rf zip-temp
	mkdir -p $(dir)
	cp -r lib $(dir)
	cp Notion.jar $(dir)/Notion-$(build).jar
	cp Readme.md $(dir)
	mkdir -p $(dir)/Documentation
	cp -r Documentation/_build/html $(dir)/Documentation
	cp Documentation/_build/epub/Notion.epub $(dir)/Documentation
	echo "log4j.rootLogger=INFO, stdout" > $(dir)/lib/log4j.properties
	echo "log4j.appender.stdout=org.apache.log4j.ConsoleAppender" >> $(dir)/lib/log4j.properties
	echo "log4j.appender.stdout.layout=org.apache.log4j.PatternLayout" >> $(dir)/lib/log4j.properties
	echo "log4j.appender.stdout.layout.ConversionPattern=%5p - %m%n" >> $(dir)/lib/log4j.properties

	(cd zip-temp && zip -r $(versionDir).zip $(versionDir) && mv $(versionDir).zip ../)

watch:
	(cd Documentation && while :; do make html ; sleep 5s; done)
