from urllib import request

def main():
    file_list_url = "http://www.demohn.com:1234/hua_wei_cup/file_list"
    file_content_url = "http://www.demohn.com:1234/hua_wei_cup/file_content/"
    trainset_path = "./trainset/"
    file_list = eval(request.urlopen(file_list_url).read().decode('utf-8'))['list']
    for filename in file_list:
        with open(trainset_path+filename, 'w', encoding='utf-8') as f:
            file_content = request.urlopen(file_content_url+filename).read().decode('utf-8')
            f.write(file_content)

if __name__ == '__main__':
	main()
