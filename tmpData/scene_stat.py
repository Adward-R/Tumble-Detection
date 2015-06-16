__author__ = 'Adward'
import os

cnt = 15 * [0]
dir_list_0 = os.listdir('trainset_0/')
dir_list_1 = os.listdir('trainset_1/')
for d in dir_list_0:
    if '.DS_Store'==str(d):
        continue
    fns = os.listdir(os.path.join('trainset_0',d))
    for fn in fns:
        scene_num = int(str(fn).split('-')[0])
        cnt[scene_num] += 1

for d in dir_list_1:
    if '.DS_Store'==str(d):
        continue
    fns = os.listdir(os.path.join('trainset_1',d))
    for fn in fns:
        scene_num = int(str(fn).split('-')[0])
        cnt[scene_num] += 1

print(cnt)