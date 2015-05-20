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
        if scene_type>=5 and scene_type<=7: #is fall
            if timestamp not in os.listdir(another_rootpath):
                os.mkdir(os.path.join(another_rootpath, timestamp))
            shutil.move(rootpath+fn, another_rootpath+timestamp+'/'+fn)
        else:
            if timestamp not in os.listdir(rootpath):
                os.mkdir(os.path.join(rootpath, timestamp))
            shutil.move(rootpath+fn, rootpath+timestamp+'/'+fn)

def collect():
    rootpath = './trainset_0/'
    dir_list = os.listdir(rootpath)
    for d in dir_list:
        file_list = os.listdir(os.path.join(rootpath, d))
        for fn in file_list:
            shutil.move(os.path.join(rootpath, d, fn), os.path.join(rootpath, fn))

if __name__ == '__main__':
    divide()
    #collect()