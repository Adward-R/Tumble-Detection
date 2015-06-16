__author__ = 'Adward'
import os
import numpy

threshold = 4.0

def select():
    cnt = 0
    with open('Gz0') as fgz:
        gz = []
        for line in fgz:
            if cnt < 30:
                gz.append(float(line))
                cnt = cnt + 1
            else:
                if True or numpy.std(gz) < threshold:
                    print(numpy.mean(gz) + numpy.std(gz))
                gz = []
                cnt = 0

    print('\n################\n')

    cnt = 0
    with open('Gz1') as fgz:
        gz = []
        for line in fgz:
            if cnt < 30:
                gz.append(float(line))
                cnt = cnt + 1
            else:
                if True or numpy.std(gz) >= threshold:
                    print(numpy.mean(gz) + numpy.std(gz))
                gz = []
                cnt = 0

if __name__=='__main__':
    select()



