__author__ = 'Adward'
import os
import subprocess
import random
import numpy

#param test_round: how many random selection round does the easy.py of svm r called

train_file = '/Users/Adward/Github/tmpData/svm_train_data.txt'
test_file = '/Users/Adward/Github/tmpData/svm_test_data.txt'
rootpath0 = '/Users/Adward/Github/tmpData/trainset_0/'
rootpath1 = '/Users/Adward/Github/tmpData/trainset_1/'

def form_trainset(test_ratio_0, test_ratio_1, sampling_rate, step, threshold):
    '''
    :param test_ratio_0: the ratio in 0 labeled cases that be extracted as test cases (other r train cases)
    :param test_ratio_1: the ratio in 1 labeled cases that be extracted as test cases (other r train cases)
    :param sampling_rate: how many consecutive group of censor data r used to form test and train data pack
    :param step: step length in sampling censor data
    :return:
    '''
    trainset = []

    dir_list_0 = os.listdir(rootpath0)
    dir_list_1 = os.listdir(rootpath1)
    test_annotate_0 = [0] * dir_list_0.__len__()
    test_annotate_1 = [0] * dir_list_1.__len__()
    testcase_num_0 = int(test_ratio_0 * (dir_list_0.__len__())-1) #len-1 is because the goddamnit .DS_Store on Mac
    testcase_num_1 = int(test_ratio_1 * (dir_list_1.__len__())-1)
    #rand = random.random()
    #if rand < 0.5:
    #    test_annotate_0[int(random.random() * (dir_list_0.__len__()-1))] = 1
    #else:
    #    test_annotate_1[int(random.random() * (dir_list_1.__len__()-1))] = 1

    for i in range(testcase_num_0):
        test_annotate_0[int(random.random() * (dir_list_0.__len__()-1))] = 1
    for i in range(testcase_num_1):
        test_annotate_1[int(random.random() * (dir_list_1.__len__()-1))] = 1

    index_cnt = 0
    for d in dir_list_0:
        if '.DS_Store'==d:
            continue
        append_fname = test_file
        if test_annotate_0[index_cnt]==0:
            append_fname = train_file
            trainset.append(str(d))
        #for each case
        dict = {}
        dict['label'] = 0
        dict['gravity'] = []
        dict['lacce'] = []
        dict['rotate'] = []
        for fn in os.listdir(os.path.join(rootpath0, d)): #order: gravity, lacce, rotate
            fpath = os.path.join(rootpath0, d, fn)
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
            if row[2]>threshold:
                break
            else:
                start_point += 1
        if dict['lacce'].__len__()-start_point < sampling_rate * step:
            continue
        if dict['lacce'].__len__()-start_point < sampling_rate * step:
            continue
        if dict['gravity'].__len__()-start_point < sampling_rate * step:
            continue

        with open(append_fname,'a') as f:
            index = 0
            f.write(str(dict['label']))
            for i in range(start_point, start_point + sampling_rate*step, step):
                index += 1
                f.write(' '+str(index)+':'+str(dict['gravity'][i][0]))
                index += 1
                f.write(' '+str(index)+':'+str(dict['gravity'][i][1]))
                index += 1
                f.write(' '+str(index)+':'+str(dict['gravity'][i][2]))
                with open('Gz0','a') as fgz:
                    fgz.write(str(dict['gravity'][i][2])+'\n')
            for i in range(start_point, start_point + sampling_rate*step, step):
                index += 1
                f.write(' '+str(index)+':'+str(dict['lacce'][i][0]))
                index += 1
                f.write(' '+str(index)+':'+str(dict['lacce'][i][1]))
                index += 1
                f.write(' '+str(index)+':'+str(dict['lacce'][i][2]))
            for i in range(start_point, start_point + sampling_rate*step, step):
                index += 1
                f.write(' '+str(index)+':'+str(dict['rotate'][i][0]))
                index += 1
                f.write(' '+str(index)+':'+str(dict['rotate'][i][1]))
                index += 1
                f.write(' '+str(index)+':'+str(dict['rotate'][i][2]))
            f.write('\n')
        index_cnt += 1

    index_cnt = 0
    for d in dir_list_1:
        if '.DS_Store'==d:
            continue
        append_fname = test_file
        if test_annotate_1[index_cnt]==0:
            append_fname = train_file
            trainset.append(str(d))
        #for each case
        dict = {}
        dict['label'] = 1
        dict['gravity'] = []
        dict['lacce'] = []
        dict['rotate'] = []
        for fn in os.listdir(os.path.join(rootpath1, d)): #order: gravity, lacce, rotate
            fpath = os.path.join(rootpath1, d, fn)
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
            if row[2]> threshold:
                break
            else:
                start_point += 1
        if dict['lacce'].__len__()-start_point < sampling_rate * step:
            continue
        if dict['rotate'].__len__()-start_point < sampling_rate * step:
            continue
        if dict['gravity'].__len__()-start_point < sampling_rate * step:
            continue

        with open(append_fname,'a') as f:
            index = 0
            f.write(str(dict['label']))
            for i in range(start_point, start_point + sampling_rate*step, step):
                index += 1
                f.write(' '+str(index)+':'+str(dict['gravity'][i][0]))
                index += 1
                f.write(' '+str(index)+':'+str(dict['gravity'][i][1]))
                index += 1
                f.write(' '+str(index)+':'+str(dict['gravity'][i][2]))
                with open('Gz1','a') as fgz:
                    fgz.write(str(dict['gravity'][i][2])+'\n')
            for i in range(start_point, start_point + sampling_rate*step, step):
                index += 1
                f.write(' '+str(index)+':'+str(dict['lacce'][i][0]))
                index += 1
                f.write(' '+str(index)+':'+str(dict['lacce'][i][1]))
                index += 1
                f.write(' '+str(index)+':'+str(dict['lacce'][i][2]))
            for i in range(start_point, start_point + sampling_rate*step, step):
                index += 1
                f.write(' '+str(index)+':'+str(dict['rotate'][i][0]))
                index += 1
                f.write(' '+str(index)+':'+str(dict['rotate'][i][1]))
                index += 1
                f.write(' '+str(index)+':'+str(dict['rotate'][i][2]))
            f.write('\n')
        index_cnt += 1

    return trainset

if __name__=='__main__':
    #if "too much zero in training data" indicated by libsvm, then clear tmp train_data & test_data files
    round_num = 500
    classify_ratio = []
    trainset_union = []
    avg_case_ratio = {} #avg ratio when the case is applied to be used as trainset data
    for i in range(round_num):
        trainset = form_trainset(0.03, 0.03, 30, 1, 5)
        os.chdir('/Users/Adward/Github/libsvm/tools/')
        result = subprocess.check_output(['/Users/Adward/Github/libsvm/tools/easy.py',
                                        '/Users/Adward/GitHub/tmpData/svm_train_data.txt',
                                        '/Users/Adward/GitHub/tmpData/svm_test_data.txt'])

        print('Round '+str(i)+'\n'+result.decode())
        ratio = result.decode().split('\n')[0].split(' ')[2].replace('%','')
        print(ratio)
        if ratio=='nan':
            continue
        else:
            ratio = float(ratio)

        for case in trainset:
            if case in avg_case_ratio.keys():
                avg_case_ratio[case].append(ratio)
            else:
                avg_case_ratio[case] = [ratio, ]
        #if float(ratio)>85:
        #    print(trainset)
        #    #intersect with trainset_union
        #    trainset_union = [itm for itm in trainset_union if itm in trainset]

        classify_ratio.append(float(ratio))
        os.chdir('/Users/Adward/Github/tmpData/')
        with open(train_file, 'w') as f:
            f.write('')
        with open(test_file, 'w') as f:
            f.write('')

    print(classify_ratio)
    sum = 0
    for ratio in classify_ratio:
       sum += ratio
    avg = sum / round_num
    print(avg)
    print(numpy.std(classify_ratio))

    with open('case_value_as_trainset.txt','w') as fout:
        for key in avg_case_ratio.keys():
            lst = avg_case_ratio[key]
            fout.write(key+':'+str(numpy.mean(lst))+':'+str(numpy.std(lst))+':'+str(lst)+'\n')

    #with open('union_of_optimized_trainset.txt','w') as f:
    #    for item in trainset_union:
    #        f.write(item+'\n')

