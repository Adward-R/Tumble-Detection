__author__ = 'Adward'
import os

def form_trainset(rootpath):
    filename = 'svm_train_data.txt'
    if rootpath=='./testset/':
        filename = 'svm_test_data.txt'

    dir_list = os.listdir(rootpath)
    for d in dir_list:
        if '.DS_Store'==d:
            continue
        #for each case
        dict = {}
        dict['label'] = 0
        for fn in os.listdir(os.path.join(rootpath, d)):
            scene_type = int(str(fn).split('-')[0])
            if scene_type>=5 and scene_type<=7:
                dict['label'] = 1
            break
        
        dict['gravity'] = []
        dict['lacce'] = []
        dict['rotate'] = []
        for fn in os.listdir(os.path.join(rootpath, d)): #order: gravity, lacce, rotate
            fpath = os.path.join(rootpath, d, fn)
            with open(fpath, encoding='utf-8', errors='ignore') as f:
                line_cnt = 0
                for line in f:
                    if line_cnt<6:
                        line_cnt += 1
                    elif line=='\n' or line=='':
                        continue
                    else:
                        tup = [float(line.split(' ')[1]), float(line.split(' ')[2]), float(line.split(' ')[3].replace('\n',''))] #x,y,z
                        if fn.find('GRAVITY')!=(-1):
                            dict['gravity'].append(tup)
                        elif fn.find('LACCE')!=(-1):
                            dict['lacce'].append(tup)
                        else:
                            dict['rotate'].append(tup)
        start_point = 0
        for row in dict['lacce']:
            if row[2]>5: #5 is the limit
                break
            else:
                start_point += 1
        if dict['lacce'].__len__()-start_point <= 50:
            continue

        with open(filename,'a') as f:
        #with open('svm_test_data.txt','a') as f:
            index = 0
            f.write(str(dict['label']))
            for i in range(start_point, start_point+50):
                index += 1
                f.write(' '+str(index)+':'+str(dict['gravity'][i][0]))
                index += 1
                f.write(' '+str(index)+':'+str(dict['gravity'][i][1]))
                index += 1
                f.write(' '+str(index)+':'+str(dict['gravity'][i][2]))
            for i in range(start_point, start_point+50):
                index += 1
                f.write(' '+str(index)+':'+str(dict['lacce'][i][0]))
                index += 1
                f.write(' '+str(index)+':'+str(dict['lacce'][i][1]))
                index += 1
                f.write(' '+str(index)+':'+str(dict['lacce'][i][2]))
            for i in range(start_point, start_point+50):
                index += 1
                f.write(' '+str(index)+':'+str(dict['rotate'][i][0]))
                index += 1
                f.write(' '+str(index)+':'+str(dict['rotate'][i][1]))
                index += 1
                f.write(' '+str(index)+':'+str(dict['rotate'][i][2]))
            f.write('\n')

if __name__ == '__main__':
    form_trainset('./trainset_0/')
    form_trainset('./trainset_1/')
    form_trainset('./testset/')