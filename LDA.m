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
    modeldatanum = 0;
    posi = 0;
    for i=1:dnum
        if (i==modelNum+1) modeldatanum = num;
            posi 
        end
        tline = fgetl(Fr);
        tline = str2num(tline);
        Label(i) = tline(1);
        if (Label(i)==1) posi= posi+1;end
        %num = num+1;
        %lm(num) = i;
        %rm(num) = 1;
        %datam(num) = Label(i);
        len = length(tline);
        for j=2:2:len
            num = num+1;
            lm(num) = i;
            rm(num) = tline(j)+1;
            if (rm(num)>max) max =rm(num);end
            datam(num) = tline(j+1);
        end
    end
    testrm = rm(modeldatanum+1:num);
    testlm = lm(modeldatanum+1:num);
    testdatam = datam(modeldatanum+1:num);
    rm = rm(1:modeldatanum);
    lm = lm(1:modeldatanum);
    datam = datam(1:modeldatanum);
    Feature=sparse(lm,rm,datam,modelNum,max);
    Label = Label(1:modelNum);
    MdlLinear = fitcdiscr(Feature,Label(1:modelNum),'discrimType','pseudoLinear');%pseudoQuadratic
    out_file_name = strcat('libsvm-3.21/',strcat(num2str(topic),'.ans'));
    fw=fopen(out_file_name,'w');
    for i=1:num-modeldatanum
        vector = zeros(1,max);
        vector(testrm(i)) =  testdatam(i);
        if (i+1>num-modeldatanum||testlm(i+1)~=testlm(i)) 
            
              tmp = predict(MdlLinear,vector);
              fprintf(fw,'%d\n',tmp);
                vector = zeros(1,max);
                
        end
    end
    
    fclose(fw);
    
end