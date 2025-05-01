import json

fp = open('names.txt')
lines = fp.readlines()
fp.close()

tmp_lines = [line.strip() for line in lines]
tmp_dict = {'names': tmp_lines}

fp = open('names.json', 'wb')
json.dump(tmp_dict, fp)
fp.close()
