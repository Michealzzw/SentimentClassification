import urllib
import urllib2
import random
import re
import sys
import time
reload(sys)
sys.setdefaultencoding('utf-8')
topics =["Feminist+Movement","Atheism","Climate+Change+is+a+Real+Concern","Hillary+Clinton","Legalization+of+Abortion"];
for topic in topics:
	url = 'https://en.wikipedia.org/w/index.php?title=Special:Search&limit=40&offset=0&profile=default&search='+topic+'&searchToken=7rdann7sajdz32herckhydgxb'
	try:
		request = urllib2.Request(url)
		response = urllib2.urlopen(request)
		html = response.read()
		pattern = re.compile('<li><div class=\'mw-search-result-heading\'><a href=\"/wiki/[^\"]+\" title=\"[^\"]+\" data-serp-pos="[0-9]+">',re.S)
	except urllib2.URLError, e:
		print "Error"
	pattern2 = re.compile('href=\"(/wiki/[^\"]+)\"',re.S)
	items = re.findall(pattern,html)
	file_object = open(topic.replace("+","")+'_wiki.txt', 'w')
	for item in items:
		print item
		tmps = re.findall(pattern2,item);
		for tmp in tmps:
			url =  "https://en.wikipedia.org"+tmp;
			print url;
			try:
				request = urllib2.Request(url)
				response = urllib2.urlopen(request)
				html = response.read()
				file_object.write(html);
			except urllib2.URLError, e:
				print "Error"
	file_object.close();
	#f.writelines(li)
