import re

while True:
	try:
		content = raw_input();
		content,number = re.subn("<[^>]+>","",content);
		print content;
	except:
		break;
