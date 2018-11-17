Fr=fopen('PCA_Feature.txt','r');
d=10;
for topic=1:5
    tline = fgetl(Fr);
    tline=str2num(tline);
    dnum =tline(1)
    modelNum = tline(2)
    dnum-modelNum
    lm = zeros(1,dnum*30);
    rm = zeros(1,dnum*30);
    Label = zeros(1,dnum);
    datam = zeros(1,dnum*30);
    num = 0;
    max = 0;
    for i=1:dnum
        tline = fgetl(Fr);
        tline = str2num(tline);
        Label(i) = tline(1);
        len = length(tline);
        for j=2:2:len
            num = num+1;
            lm(num) = i;
            rm(num) = tline(j);
            if (rm(num)>max) max =rm(num);end
            datam(num) = tline(j+1);
        end
    end
    rm = rm(1:num);
    lm = lm(1:num);
    datam = datam(1:num);
    Feature=sparse(lm,rm,datam,dnum,max);
    Y = compute_mapping(Feature,'LLE',d,25);
    out_file_name = strcat('ans_SVM_train_',strcat(num2str(topic),'.txt'));
    fw=fopen(out_file_name,'w');
    for i=1:dnum
        fprintf(fw,'%d\t',Label(i));
        for j=1:d
            fprintf(fw,'%d:%f\t',j,Y(i,j));
        end
        fprintf(fw,'\n');
        if (i==modelNum)
            fclose(fw);
            out_file_name = strcat('ans_SVM_test_',strcat(num2str(topic),'.txt'));
            fw=fopen(out_file_name,'w');
        end
    end
    
    fclose(fw);
    
end