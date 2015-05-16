import os
import codecs

def shaper(censor):
	result = {}
	result['name'] = censor
	result['children'] = []
	files = []
	path = './'+censor.upper()+'/'
	file_lst = os.listdir(path)
	for fn in file_lst:
		files.append(os.path.join(path,fn))
	file_cnt = 0
	for fn in files:
		if str(fn).find('.DS_Store') != (-1):
			continue
		tmp = {}
		tmp['name'] = file_cnt
		tmp['children'] = []
		with codecs.open(fn, encoding='utf-8', errors='ignore') as f:
			line_cnt = 0
			for line in f:
				if line_cnt<6 :
					line_cnt += 1
				elif line=='\n' or line=='': #last line
					pass
				else:
					tmp_line = {}
					tmp_line['X'] = float(line.split(' ')[1])
					tmp_line['Y'] = float(line.split(' ')[2])
					tmp_line['Z'] = float(line.split(' ')[3].replace('\n',''))
					tmp['children'].append(tmp_line)
		file_cnt += 1
		result['children'].append(tmp)

	smallest = 0
	for f in result['children']:
		for l in f['children']:
			if l['X']<smallest:
				smallest = l['X']
			elif l['Y']<smallest:
				smallest = l['Y']
			elif l['Z']<smallest:
				smallest = l['Z']
	if 1:#smallest<0:
		for f in result['children']:
			for l in f['children']:
				l['X'] -= smallest
				l['Y'] -= smallest
				l['Z'] -= smallest

	with codecs.open('./vis/'+censor+'.json','w',encoding='utf-8', errors='ignore') as f:
		f.write(str(result).replace("'",'"'))

if __name__=='__main__':
	shaper('gravity')
	shaper('lacce')
	shaper('rotate')