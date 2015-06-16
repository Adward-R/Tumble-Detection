__author__ = 'Adward'
import os
import shutil

def divide():
    rootpath = './trainset_0/'
    another_rootpath = './trainset_1/'
    file_list = os.listdir(rootpath)
    for fn in file_list:
        if '.DS_Store'==fn:
            continue
        timestamp = fn.strip('.data').split('-')[2]
        scene_type = int(fn.strip('.data').split('-')[0])
        if scene_type>=4 and scene_type<=7: #is fall
            if timestamp not in os.listdir(another_rootpath):
                os.mkdir(os.path.join(another_rootpath, timestamp))
            shutil.move(rootpath+fn, another_rootpath+timestamp+'/'+fn)
        else: #is other abnormal cases
            if timestamp not in os.listdir(rootpath):
                os.mkdir(os.path.join(rootpath, timestamp))
            shutil.move(rootpath+fn, rootpath+timestamp+'/'+fn)

def collect():
    rootpath = './trainset_1/'
    dir_list = os.listdir(rootpath)
    for d in dir_list:
        if '.DS_Store'==d:
            continue
        file_list = os.listdir(os.path.join(rootpath, d))
        for fn in file_list:
            shutil.move(os.path.join(rootpath, d, fn), os.path.join(rootpath, fn))

def wipe(rootpath): #wipe out broken case
    dir_list = os.listdir(rootpath)
    for d in dir_list:
        if '.DS_Store'==d:
            continue
        file_list = os.listdir(os.path.join(rootpath, d))
        cnt = 0
        for fn in file_list:
            if '.DS_Store'==fn:
                continue
            cnt += 1
        if cnt!=3:
            print(d)

if __name__ == '__main__':
    divide()
    #collect()
    #wipe('./trainset_0')
    #wipe('./trainset_1')